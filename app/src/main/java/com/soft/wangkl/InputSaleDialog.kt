package com.soft.wangkl

import android.app.Activity
import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
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
        setContentView(R.layout.input_sale_dialog)
        window.attributes.gravity = Gravity.TOP + Gravity.CENTER_HORIZONTAL
        window.attributes.width = window.windowManager.defaultDisplay.width
        ok.setOnClickListener {
            if (insertSaleData()) toast("收银成功!")
            else toast("收银失败!")
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
        map["je"] = je.toString()
        adapter.addData(map)
        title.text = "合计: ${adapter.getSumSl()}件, ${adapter.getSumJe()}.00元"
        this.tm.text.clear()
        this.sl.setText("1")
        this.tm.requestFocus()
    }

    private fun insertSaleData(): Boolean {
        var value = false
        if (lv.count < 1) {
            toast("提示: 列表为空!")
            return value
        }
        try {
            adapter.insertSaledData(db)
            value = true
        } catch (e: Exception) {
            value = false
        }
        return value
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