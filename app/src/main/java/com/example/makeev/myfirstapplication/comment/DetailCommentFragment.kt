package com.example.makeev.myfirstapplication.comment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.makeev.myfirstapplication.ApiUtils
import com.example.makeev.myfirstapplication.App
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.db.MusicDao
import com.example.makeev.myfirstapplication.model.Comment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class DetailCommentFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    companion object {
        const val ALBUM_ID_KEY = "ALBUM_ID_KEY"


        fun newInstance(albumId: Int): DetailCommentFragment {
            val args = Bundle()
            args.putSerializable(ALBUM_ID_KEY, albumId)

            val fragment = DetailCommentFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
    private lateinit var errorView: View
    private lateinit var commentButton: Button
    private lateinit var commentText: TextView
    private var albumId: Int = 0
    private val disposable = CompositeDisposable()
    private val commentsAdapter = CommentsAdapter()
    private var oldSizeComments = 0
    private var hasConnection = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fr_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler_comment)
        refresher = view.findViewById(R.id.refresher_comment)
        refresher.setOnRefreshListener(this)
        errorView = view.findViewById(R.id.error_comment_view)
        commentButton = view.findViewById(R.id.btn_comment)
        commentText = view.findViewById(R.id.et_comment)

        commentButton.setOnClickListener(onCommentClickListener)

        commentText.setOnEditorActionListener(onEnterActionListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        albumId = arguments!!.getSerializable(ALBUM_ID_KEY) as Int
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = commentsAdapter

        onRefresh()
    }

    override fun onRefresh() {
        refresher.post {
            getComments()
        }
    }

    private val onCommentClickListener = View.OnClickListener {
        postComment()
    }

    private var onEnterActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            postComment()
        }
        true
    }

    private fun getComments() {
        disposable.add(ApiUtils.getApiService()
                .getCommentsForAlbum(albumId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess { comments ->
                    getMusicDao().insertComments(comments)
                }
                .onErrorReturn { throwable ->
                    if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.javaClass)) {
                        val comments = getMusicDao().comments(albumId)
                        if (comments.isNotEmpty()) {
                            hasConnection = false
                            comments
                        } else null

                    } else null
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { }
                .doOnSubscribe { refresher.isRefreshing = true }
                .doFinally { refresher.isRefreshing = false }
                .subscribe({ comments ->
                    if (comments.isNotEmpty()) {
                        errorView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        commentsAdapter.addData(comments, true)
                        if (oldSizeComments != 0) {
                            if (oldSizeComments == comments.size) {
                                showMessage("Новых комментариев нет")
                            } else {
                                showMessage("Комментарии обновлены")
                            }
                        }
                        if (!hasConnection) {
                            showMessage("Отсутствует подключение")
                            hasConnection = true

                        }
                        oldSizeComments = comments.size
                    } else {
                        errorView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }

                }, {
                    it.printStackTrace()
                    errorView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                })
        )

    }

    private fun postComment() {
        if (!TextUtils.isEmpty(commentText.text)) {
            disposable.add(ApiUtils.getApiService()
                    .postComment(Comment(commentText.text.toString(), albumId.toString()))
                    .subscribeOn(Schedulers.io())
                    .flatMap { commentId ->
                        ApiUtils.getApiService()
                                .getComment(commentId.id)
                    }
                    .doOnSuccess { comment ->
                        getMusicDao().insertComment(comment)

                    }
                    .onErrorReturn { throwable ->
                        if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.javaClass)) {
                            val comment = getMusicDao().comment(albumId)
                            comment
                        } else null
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { refresher.isRefreshing = true }
                    .doFinally { refresher.isRefreshing = false }
                    .subscribe({ comment ->
                        commentsAdapter.addData(arrayListOf(comment), isRefresher = false, isNewComment = true)
                        commentText.text = ""
                        if (errorView.visibility == View.VISIBLE) {
                            errorView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                    }, { throwable ->
                        if (throwable is HttpException) {
                            showMessage(throwable.response().message())
                        }
                        if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.javaClass)) {
                            showMessage("Проверьте подключение и повторите попытку!")
                        }
                    })
            )
        } else {
            showMessage("Нет текста для отправки!")
        }

    }

    private fun showMessage(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    private fun getMusicDao(): MusicDao {
        return (activity!!.application as App).getDataBase()!!.getMusicDao()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}