package com.plcoding.spotifycloneyt.data.remote

import com.plcoding.spotifycloneyt.data.entities.Song
import com.plcoding.spotifycloneyt.other.Constants.DATABASE_PASSWORD
import com.plcoding.spotifycloneyt.other.Constants.DATABASE_USER
import com.plcoding.spotifycloneyt.other.Constants.JDBC_CONNECTION_URL
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.Properties

class MusicDatabase {
    private var conn: Connection? = null
    private var username = DATABASE_USER // provide the username
    private var password = DATABASE_PASSWORD // provide the corresponding password

    private fun executeMySQLQuery() : List<Song> {
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        val listOfSongs: MutableList<Song> = mutableListOf()

        try {
            statement = conn!!.createStatement()
            resultSet = statement!!.executeQuery("SELECT * FROM Track;")

            if (statement.execute("SELECT * FROM Track;")) {
                resultSet = statement.resultSet
            }

            while (resultSet!!.next()) {
                println(resultSet.getString("track_id"))
                val trackId = resultSet.getString("track_id")
                val name = resultSet.getString("name")
                val author = resultSet.getString("author")
                val genre = resultSet.getString("genre")
                val mood = resultSet.getString("mood")
                val musicUrl = resultSet.getString("music_url")
                val coverUrl = resultSet.getString("cover_url")
                val streams = resultSet.getString("streams")
                val song = Song(trackId, name, author, genre, mood, musicUrl, coverUrl, streams)
                listOfSongs += song
            }
            // FIXME: optimize or refactor error checking
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            // release resources
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (_: SQLException) {
                }

                resultSet = null
            }

            if (statement != null) {
                try {
                    statement.close()
                } catch (_: SQLException) {
                }

                statement = null
            }

            if (conn != null) {
                try {
                    conn!!.close()
                } catch (_: SQLException) {
                }

                conn = null
            }
        }
        return listOfSongs
    }

    private fun getConnection() {
        val connectionProps = Properties()
        connectionProps["user"] = username
        connectionProps["password"] = password
        try {
            Class.forName("com.mysql.jdbc.Driver")
            conn = DriverManager.getConnection(JDBC_CONNECTION_URL, connectionProps)
        } catch (ex: SQLException) {
            // FIXME: optimize or refactor error checking
            ex.printStackTrace()
        } catch (ex: Exception) {
            // FIXME: optimize or refactor error checking
            ex.printStackTrace()
        }
    }

    fun getAllSongs() : List<Song> {
        getConnection()
        return executeMySQLQuery()
    }
}