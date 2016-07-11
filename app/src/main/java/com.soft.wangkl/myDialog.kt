package com.soft.wangkl

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button

/**
 * Created by 123456 on 2016/7/10.
 */

class myDialog(context: Context, theme: Int) : Dialog(context, theme) {
    val btnOK: Button by lazy { findViewById(R.id.input_OK) as Button }
    val btnCancel: Button by lazy { findViewById(R.id.input_cancel) as Button }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sale_db_input)
        btnOK.setOnClickListener { dismiss() }
        btnCancel.setOnClickListener { dismiss() }
    }
}