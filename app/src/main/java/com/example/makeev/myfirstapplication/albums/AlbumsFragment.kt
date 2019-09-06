package com.example.makeev.myfirstapplication.albums

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.makeev.myfirstapplication.utils.ApiUtils
import com.example.makeev.myfirstapplication.App
import com.example.makeev.myfirstapplication.R
import com.example.makeev.myfirstapplication.album.DetailAlbumFragment
import com.example.makeev.myfirstapplication.db.MusicDao
import com.example.makeev.myfirstapplication.model.Album
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class AlbumsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    lateinit var recyclerView: RecyclerView
    lateinit var refresher: SwipeRefreshLayout
    lateinit var errorView: View
    private val disposable = CompositeDisposable()

    private val albumAdapter = AlbumsAdapter(object : AlbumsAdapter.OnItemClickListener {
        override fun onItemClick(album: Album) {
            fragmentManager!!.beginTransaction()
                    .replace(R.id.fragmentContainer, DetailAlbumFragment.newInstance(album))
                    .addToBackStack(DetailAlbumFragment::class.java.simpleName)
                    .commit()
        }
    })

    companion object {
        fun newInstance(): AlbumsFragment {
            return AlbumsFragment()
        }
    }

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
        activity!!.setTitle(R.string.albums)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = albumAdapter

        onRefresh()
    }


    override fun onRefresh() {
        refresher.post {
            getAlbums()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun getAlbums() {
        disposable.add(ApiUtils.getApiService()
                .getAlbums()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { albums ->
                    getMusicDao().insertAlbums(albums)
                }
                .onErrorReturn { throwable ->
                    if (ApiUtils.NETWORK_EXCEPTIONS.contains(throwable.javaClass)) {
                        getMusicDao().albums()
                    } else ArrayList()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { refresher.isRefreshing = true }
                .doFinally { refresher.isRefreshing = false }
                .subscribe({ albums ->
                    recyclerView.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                    albumAdapter.addData(albums, true)
                }, {
                    recyclerView.visibility = View.GONE
                    errorView.visibility = View.VISIBLE
                })
        )
    }

    private fun getMusicDao(): MusicDao {
        return (activity!!.application as App).getDataBase()!!.getMusicDao()
    }
}