package com.evan.delivery.ui.home.notice

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.evan.delivery.R
import com.evan.delivery.data.db.entities.Notice


class NoticeViewFragment : Fragment() {

    var img_notice: ImageView?=null
    var tv_title: TextView?=null
    var tv_content: TextView?=null
    var notice: Notice?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_notice_view, container, false)
        img_notice=root?.findViewById(R.id.img_notice)
        tv_title=root?.findViewById(R.id.tv_title)
        tv_content=root?.findViewById(R.id.tv_content)
        val args: Bundle? = arguments
        if (args != null) {
            if (args?.containsKey(Notice::class.java.getSimpleName()) != null) {
                notice = args?.getParcelable(Notice::class.java.getSimpleName())
                var title: String = ""
                var content: String = ""
                title = "<b> <font color=#BF3E15>Title  </font> : </b>" + notice?.Title.toString()
                content = "<b> <font color=#BF3E15>Content </font> : </b>" + notice?.Content.toString()
                tv_title?.text = Html.fromHtml(title!!)
                tv_content?.text = Html.fromHtml(content!!)
                Glide.with(context!!)
                    .load(notice?.Image)
                    .into(img_notice!!)
            }

        }
        return root
    }


}