package com.soft.wangkl

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.Window
import android.widget.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yangglemu on 2016/7/12.
 */
class InputSaleDialog(activity: Activity, theme: Int, val db: SQLiteDatabase) : Dialog(activity, theme) {

    var row: Int = 0

    val adapter = InputSaleAdapter(activity)

    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    val decimalFormat = DecimalFormat("#,##0.00")

    val pm: PopupMenu by lazy {
        val pm = PopupMenu(activity, lv)
        pm.inflate(R.menu.input_sale_popup_menu)
        pm.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.input_sale_menu_delete -> {
                    adapter.removeData(row)
                }
                R.id.input_sale_menu_edit -> {
                    val et = EditText(context)
                    et.maxLines = 1
                    et.inputType = InputType.TYPE_CLASS_NUMBER
                    val d = AlertDialog.Builder(context)
                    d.setTitle("输入新价格")
                            .setIcon(R.drawable.icon)
                            .setView(et)
                            .setPositiveButton("确定", { dialogInterface, i ->
                                if (et.length() < 1) return@setPositiveButton
                                try {
                                    val je = et.text.toString().toInt()
                                    adapter.updateData(row, je)
                                    dialogInterface.dismiss()
                                    toast("修改价格成功!")
                                } catch (e: Exception) {
                                    toast("修改价格失败!\r\n${e.message}")
                                }
                            })
                            .setNegativeButton("退出", { dialogInterface, i -> dialogInterface.dismiss() })
                            .show()
                }
            }
            true
        }
        pm
    }

    val title: TextView by lazy {
        findViewById(R.id.input_sale_dialog_title) as TextView
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
    val sy: Button by lazy {
        findViewById(R.id.input_sale_dialog_sy) as Button
    }
    val ok: Button by lazy {
        findViewById(R.id.input_sale_dialog_buttonOK)!! as Button
    }
    val cancel: Button by lazy {
        findViewById(R.id.input_sale_dialog_buttonCancel)!! as Button
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.input_sale_dialog)
        window.decorView.setPadding(0, 0, 0, 0)
        window.attributes.gravity = Gravity.TOP + Gravity.CENTER_HORIZONTAL
        window.attributes.width = window.windowManager.defaultDisplay.width
        setCancelable(false)
        ok.setOnClickListener {
            try {
                adapter.insertSaleData(db)
                adapter.removeAllData()
                toast("收银成功!")
            } catch (e: Exception) {
                e.printStackTrace()
                toast("收银失败!\r\n${e.message}")
                //showException(context,e)
            }
        }
        cancel.setOnClickListener { dismiss() }
        sy.setOnClickListener {
            if (!checkTM(tm.text.toString())) {
                toast("条码不存在!")
                return@setOnClickListener
            }
            if (!checkSL(sl.text.toString())) {
                toast("数量输入错误!")
                return@setOnClickListener
            }
            addRow()
        }
        lv.adapter = adapter
        lv.setOnItemLongClickListener { parent, view, position, id ->
            row = position
            pm.show()
            true
        }
    }

    private fun addRow() {
        val id = lv.count + 1
        val tm = this.tm.text.toString().toInt()
        val sl = this.sl.text.toString().toInt()
        val je = tm * sl
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["tm"] = tm.toString()
        map["sl"] = sl.toString()
        map["zq"] = "1.00"
        map["je"] = decimalFormat.format(je)
        adapter.addData(map)
        val sum_sl = adapter.getSumSl()
        val sum_je = decimalFormat.format(adapter.getSumJe())
        title.text = "合计: $sum_sl 件, $sum_je 元"
        this.tm.text.clear()
        this.sl.setText("1")
        this.tm.requestFocus()
    }

    private fun checkTM(ctm: String): Boolean {
        var value = false
        if (ctm.length > 0) {
            val c = db.rawQuery("select count(*) from goods where tm='$ctm'", null)
            if (c.moveToNext()) {
                if (c.getInt(0) == 1) value = true
            }
            c.close()
        }
        return value
    }

    private fun checkSL(s: String): Boolean {
        var value = false
        if (s.length > 0) {
            try {
                val m = s.toInt()
                value = true
            } catch (e: Exception) {
                toast(e.message)
            }
        }
        return value
    }

    fun toast(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}