package com.soft.wangkl

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

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
        setContentView(R.layout.input_new_goods)
        window.decorView.setPadding(0, 0, 0, 0)
        window.attributes.width = window.windowManager.defaultDisplay.width
        window.attributes.gravity = Gravity.TOP + Gravity.CENTER_HORIZONTAL
        window.setIcon(R.drawable.icon)
        setTitle("新建商品")
        tm.requestFocus()
        this.setCancelable(false)
        ok.setOnClickListener {
            try {
                val ctm = tm.text.toString().toInt()
                db.execSQL("insert into goods (tm,sj,zq,sl) values('$ctm',$ctm,1.0,0)")
                toast("新建商品成功!")
            } catch (e: Exception) {
                toast("新建商品失败!")
            }finally {
                tm.text.clear()
                tm.requestFocus()
            }
        }
        cancel.setOnClickListener { dismiss() }
    }

    private fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}