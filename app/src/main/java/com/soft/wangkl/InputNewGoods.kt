package com.soft.wangkl

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * Created by 123456 on 2016/7/15.
 */
class InputNewGoods(context: Context, theme: Int, val db: SQLiteDatabase) : Dialog(context, theme) {
    val tm by lazy {
        findViewById(R.id.goods_new_tm) as EditText
    }
    val ok by lazy {
        findViewById(R.id.goods_new_buttonOK) as Button
    }
    val cancel by lazy {
        findViewById(R.id.goods_new_buttonCancel) as Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goods_new)
        setTitle("新建商品")
        this.setCancelable(false)
        ok.setOnClickListener {
            val ctm = tm.text.toString().toInt()
            db.execSQL("insert ")
        }
        cancel.setOnClickListener { dismiss() }
    }
}