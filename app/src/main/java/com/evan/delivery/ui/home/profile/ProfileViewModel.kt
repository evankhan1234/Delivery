package com.evan.delivery.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.evan.delivery.data.db.entities.User
import com.evan.delivery.data.repositories.UserRepository
import com.evan.delivery.util.Coroutines
import com.evan.delivery.util.lazyDeferred


class ProfileViewModel(
    private val  repository: UserRepository
) : ViewModel() {

    fun user() = repository.getUser()
   // fun userList() = repository.getUserList()
    val quotes by lazyDeferred {
        repository.getUserList()
    }

    fun saveUser(user: User){
        Coroutines.main {
            Log.e("sdfds","Sds"+user.created_at)
            repository.saveUser(user)
        }

    }


}
