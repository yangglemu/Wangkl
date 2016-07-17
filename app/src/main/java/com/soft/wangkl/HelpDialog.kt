package com.soft.wangkl

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.webkit.WebView
import android.widget.Button

/**
 * Created by 123456 on 2016/7/17.
 */

class HelpDialog(context: Context, theme: Int) : Dialog(context, theme) {
    val tv: WebView by lazy {
        findViewById(R.id.help_tv) as WebView
    }
    val ok: Button by lazy {
        findViewById(R.id.help_dialog_buttonOK) as Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.help_dialog)
        window.attributes.gravity = Gravity.TOP + Gravity.CENTER_HORIZONTAL
        window.attributes.width = window.windowManager.defaultDisplay.width
        ok.setOnClickListener { dismiss() }
        try {
            tv.loadUrl("file:///android_asset/help.html")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}