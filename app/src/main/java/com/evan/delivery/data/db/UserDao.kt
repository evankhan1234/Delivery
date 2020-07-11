package com.evan.delivery.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.evan.delivery.data.db.entities.CURRENT_USER_ID
import com.evan.delivery.data.db.entities.User


@Dao
interface UserDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User) : Long

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getuser() : LiveData<User>

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getuserList() : LiveData<List<User>>

}