package com.evan.delivery.ui.home.notice

import com.evan.delivery.data.db.entities.Notice


interface INoticeUpdateListener {
    fun onUpdate(notice: Notice)

}