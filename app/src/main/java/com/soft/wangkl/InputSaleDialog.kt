package com.soft.wangkl

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*

/**
 * Created by yangglemu on 2016/7/12.
 */
class InputSaleDialog(activity: Activity, theme: Int, val db: SQLiteDatabase) : AlertDialog(activity) {
    var row: Int = 0
    val pm: PopupMenu by lazy {
        val pm = PopupMenu(activity, lv)
        pm.inflate(R.menu.input_sale_popup_menu)
        pm.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.input_sale_menu_delete -> {
                    val adapter = lv.adapter
                    if (adapter is InputSaleAdapter) {
                        adapter.removeData(row)
                    }
                }
            }
            true
        }
        pm
    }

    val lv: ListView by lazy {
        findViewById(R.id.input_sale_dialog_listView) as ListView
    }
    val tm: EditText by lazy {
        findViewById(R.id.input_sale_dialog_tm) as EditText
    }
    val sl: EditText by lazy {
        findViewById(R.id.input_sale_dialog_sl) as EditText
    }
    val ok: Button by lazy {
        findViewById(R.id.input_sale_dialog_buttonOK)!! as Button
    }
    val cancel: Button by lazy {
        findViewById(R.id.input_sale_dialog_buttonCancel)!! as Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_sale_dialog)
        val title = TextView(context)
        title.textSize = 14.0f
        title.text = "共 0 件商品，计 0 元"
        title.gravity = Gravity.CENTER
        setCustomTitle(title)
        cancel.setOnClickListener { dismiss() }
        lv.setOnItemLongClickListener { parent, view, position, id ->
            row = position
            pm.show()
            true
        }
    }

    private fun insertSaleData(): Boolean {
        if (lv.count < 1) {
            toast("提示: 列表为空!")
            return false
        }
        if (!checkTM()) {
            toast("条码不存在!")
            return false
        }
        if (!checkSL()) {
            toast("数量输入错误!")
            return false
        }
        db.beginTransaction()
        var sql = "insert into sale_db (rq,sl,je) values('',,,)"
        db.endTransaction()
        return true
    }

    private fun checkTM(): Boolean {
        var value = false;
        if (tm.length() > 0) {
            val c = db.rawQuery("select count(*) from goods where tm=${tm.text.toString()}", null)
            if (c.moveToNext()) {
                if (c.getInt(0) == 1) value = true
            }
            c.close()
        }
        return value
    }

    private fun checkSL(): Boolean {
        var value = false
        if (sl.length() > 0) {
            try {
                val m = sl.text.toString().toInt()
                value = true
            } catch (e: Exception) {
                toast(e.message)
            }
        }
        return value
    }

    fun showPopupMenu() {

    }

    fun toast(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}