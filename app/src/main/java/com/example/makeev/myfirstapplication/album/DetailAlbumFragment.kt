package com.example.makeev.myfirstapplication.album

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.makeev.myfirstapplication.utils.ApiUtils
import com.example.makeev.myfirstapplication.App
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.comment.DetailCommentFragment
import com.example.makeev.myfirstapplication.db.MusicDao
import com.example.makeev.myfirstapplication.model.Album
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailAlbumFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    companion object {
        const val ALBUM_KEY = "ALBUM_KEY"

        fun newInstance(album: Album): DetailAlbumFragment {
            val args = Bundle()
            args.putSerializable(ALBUM_KEY, album)

            val fragment = DetailAlbumFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
    private lateinit var errorView: View
    private lateinit var album: Album
    private val disposable = CompositeDisposable()

    private val songsAdapter = SongsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fr_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler)
        refresher = view.findViewById(R.id.refresher)
        refresher.setOnRefreshListener(this)
        errorView = view.findViewById(R.id.errorView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        album = arguments!!.getSerializable(ALBUM_KEY) as Album

        activity!!.title = album.name

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = songsAdapter

        setHasOptionsMenu(true)
        onRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.comment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.comment -> fragmentManager!!.beginTransaction()
                    .replace(R.id.fragmentContainer, DetailCommentFragment.newInstance(album.id))
                    .addToBackStack(DetailCommentFragment::class.java.simpleName)
                    .commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        refresher.post {
            getAlbum()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun getAlbum() {
        disposable.add(ApiUtils.getApiService()
                .getAlbum(album.id)
                .subscribeOn(Schedulers.io())
                .doOnSuccess { album ->
                    album.songs!!.forEach { it.albumId = this.album.id }
                    getMusicDao().insertSongs(album.songs!!)
                }
                .onErrorReturn { throwable ->
                    if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.javaClass)) {
                        album.songs = getMusicDao().songs(album.id)
                        if (album.songs!!.isNotEmpty()) album
                        else null

                    } else null
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { refresher.isRefreshing = true }
                .doFinally { refresher.isRefreshing = false }
                .subscribe({ album ->
                    errorView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    songsAdapter.addData(album.songs!!.sortedBy { it.id }, true)
                }, {
                    errorView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                })
        )
    }

    private fun getMusicDao(): MusicDao {
        return (activity!!.application as App).getDataBase()!!.getMusicDao()
    }
}