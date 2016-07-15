package com.soft.wangkl

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.*

/**
 * Created by 123456 on 2016/7/15.
 */
class InputCkAndRkDialog(context: Context, theme: Int, name: String, val db: SQLiteDatabase) : Dialog(context, theme) {
    val title = name
    val tm: EditText by lazy {
        findViewById(R.id.input_ck_and_rk_tm_editText) as EditText
    }
    val sl: EditText by lazy {
        findViewById(R.id.input_ck_and_rk_sl_editText) as EditText
    }
    val ok: Button by lazy {
        findViewById(R.id.input_ck_and_rk_buttonOK) as Button
    }
    val cancel: Button by lazy {
        findViewById(R.id.input_ck_and_rk_buttonCancel) as Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_ck_and_rk)
        window.attributes.width = window.windowManager.defaultDisplay.width
        window.setGravity(Gravity.TOP + Gravity.CENTER_HORIZONTAL)
        setCancelable(false)
        setTitle(title)
        ok.setOnClickListener {
            if (title.contains("入")) rk()
            else if (title.contains("出")) ck()
        }
        cancel.setOnClickListener { dismiss() }
    }

    fun rk() {
        val date = Date().toString("yyyy-MM-dd HH:mm:ss")
        val ctm = tm.text.toString()
        val csl = sl.text.toString()
        db.beginTransaction()
        try {
            db.execSQL("update goods set sl=sl+$csl where tm='$ctm'")
            db.execSQL("insert into rk (rq,tm,sl) values('$date','$ctm',$csl)")
            db.setTransactionSuccessful()
            tm.text.clear()
            sl.text.clear()
            Toast.makeText(context, "入库成功!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "入库失败!", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }

    }

    fun ck() {
        val date = Date().toString("yyyy-MM-dd HH:mm:ss")
        val ctm = tm.text.toString()
        val csl = sl.text.toString()
        db.beginTransaction()
        try {
            db.execSQL("update goods set sl=sl-$csl where tm='$ctm'")
            db.execSQL("insert into ck (rq,tm,sl) values('$date','$ctm',$csl)")
            db.setTransactionSuccessful()
            Toast.makeText(context, "出库成功!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "出库失败!", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }

    }
}