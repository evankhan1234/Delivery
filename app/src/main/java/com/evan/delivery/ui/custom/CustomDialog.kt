package com.evan.delivery.ui.custom

import android.app.Dialog
import android.content.Context

class CustomDialog : Dialog {
    constructor(context: Context?) : super(context!!) {// TODO Auto-generated constructor stub
        setCancelable(false)
    }

    constructor(context: Context?, style: Int) : super(
        context!!,
        style
    ) {// TODO Auto-generated constructor stub
        setCancelable(false)
    }

    override fun onBackPressed() {
        dismiss()
    }
}