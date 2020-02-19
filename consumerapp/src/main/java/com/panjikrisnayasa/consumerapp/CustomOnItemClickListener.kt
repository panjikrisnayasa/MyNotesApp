package com.panjikrisnayasa.consumerapp

import android.view.View

class CustomOnItemClickListener(
    private val position: Int,
    private val onItemClickCallback: OnItemClickCallback
) : View.OnClickListener {

    override fun onClick(view: View?) {
        if (view != null)
            onItemClickCallback.onItemClicked(view, position)
    }

    interface OnItemClickCallback {
        fun onItemClicked(view: View, position: Int)
    }

}