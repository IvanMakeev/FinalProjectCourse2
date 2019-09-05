package com.example.makeev.myfirstapplication.db

import android.arch.persistence.room.*
import com.example.makeev.myfirstapplication.model.Album
import com.example.makeev.myfirstapplication.model.AlbumComment
import com.example.makeev.myfirstapplication.model.Song


@Dao
interface MusicDao {

    //Album's part
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbums(albums: List<Album>)

    @Query("SELECT * FROM album")
    fun albums(): List<Album>

    @Query("SELECT * FROM album WHERE id = :albumId")
    fun album(albumId: Int): Album

    //удалить альбом
    @Delete
    fun deleteAlbum(album: Album)

    //удалить альбом по id
    @Query("DELETE FROM album WHERE id = :albumId")
    fun deleteAlbumById(albumId: Int)


    //Song's part
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongs(songs: List<Song>) : List<Long>

    @Query("SELECT * FROM song WHERE album_id = :albumId")
    fun songs(albumId: Int): List<Song>

    @Delete
    fun deleteSong(song: Song)

    @Query("DELETE FROM song WHERE id = :songId")
    fun deleteSongById(songId: Int)

    //Comment's part
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(comments: List<AlbumComment>) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: AlbumComment) : Long

    @Query("SELECT * FROM album_comment WHERE album_id = :albumId")
    fun comments(albumId: Int): List<AlbumComment>

    @Query("SELECT * FROM album_comment WHERE album_id = :albumId")
    fun comment(albumId: Int): AlbumComment

    @Delete
    fun deleteComment(comment: AlbumComment)

    @Query("DELETE FROM album_comment WHERE id = :commentId")
    fun deleteCommentById(commentId: Int)
}