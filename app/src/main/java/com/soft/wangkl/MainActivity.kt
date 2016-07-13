package com.soft.wangkl

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.util.*

class MainActivity : Activity() {
    val db: SQLiteDatabase by lazy {
        DbHelper(this).writableDatabase
    }
    val email: Email by lazy {
        Email(this, db)
    }
    val mainLayout: LinearLayout by lazy {
        findViewById(R.id.mainLayout) as LinearLayout
    }
    lateinit var listLayout: View
    lateinit var listView: ListView
    var timer: Timer? = null

    companion object {
        var formatString = "yyyy-MM-dd"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val date = Date()
        createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, SaleFLAdapter(this, db, date, date))
    }

    fun createListLayout(layoutId: Int, listViewId: Int, adapter: DataAdapter) {
        mainLayout.removeAllViews()
        listLayout = layoutInflater.inflate(layoutId, null)
        listView = listLayout.findViewById(listViewId) as ListView
        listView.adapter = adapter
        adapter.setSort(listLayout)
        mainLayout.addView(listLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sp -> {
                createListLayout(R.layout.goods, R.id.listView_goods, GoodsAdapter(this, db))

                toast("商品资料")
            }
            R.id.mx -> {
                createListLayout(R.layout.sale_mx, R.id.listView_sale_mx, SaleMXAdapter(this, db, Date(), Date()))
                toast("本日销售明细")
            }
            R.id.db -> {
                val date = Date()
                val adapter = SaleDBAdapter(this, db, date, date)
                createListLayout(R.layout.sale_db, R.id.listView_sale_db, adapter)
                toast("本日销售单笔")
            }
            R.id.fl -> {
                val date = Date()
                val adapter = SaleFLAdapter(this, db, date, date)
                createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, adapter)
                toast("本日分类汇总")
            }
            R.id.day -> {
                val adapter = SaleDayAdapter(this, db)
                createListLayout(R.layout.sale_day, R.id.listView_sale_day, adapter)
                toast("本月按天汇总")
            }
            R.id.rq_mx -> {
                val dp = MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_mx = SaleMXAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_mx, R.id.listView_sale_mx, sale_mx)
                        toast("选择日期:销售明细")
                    }
                })
                dp.show()
            }
            R.id.rq_db -> {
                val dp = MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val adapter = SaleDBAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_db, R.id.listView_sale_db, adapter)
                        toast("选择日期:销售单笔")
                    }
                })
                dp.show()
            }
            R.id.rq_fl -> {
                val dp = MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_fl = SaleFLAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_fl, R.id.listView_sale_fl, sale_fl)
                        toast("选择日期:分类汇总")
                    }
                })
                dp.show()
            }
            R.id.rq_day -> {
                val dp = MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_day = SaleDayAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_day, R.id.listView_sale_day, sale_day)
                        toast("选择日期:按天汇总")
                    }
                })
                dp.show()
            }
            R.id.newGoods -> {
                val input = AlertDialog.Builder(this,android.R.style.Theme_Holo_Dialog)
                val et = EditText(this)
                input.setTitle("请输入商品价格")
                        .setView(et)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(false)
                        .setPositiveButton("新增", { dialogInterface, i ->
                            if (et.length() > 0) {
                                try {
                                    val tm = et.text.toString().toInt()
                                    addGoods(tm)
                                } catch (e: Exception) {
                                    toast(e.message ?: "请输入数字!")
                                }
                            }
                        })
                        .setNegativeButton("退出", { dialogInterface, i ->
                            dialogInterface.cancel()
                        }).show()
            }
            R.id.sy -> {
                val sy = InputSaleDialog(this@MainActivity, R.style.input_sale_dialog_style, db)
                sy.setCancelable(false)
                sy.show()
                toast("收银")
            }
            R.id.rk -> {
                toast("入库")
            }
            R.id.ck -> {
                toast("出库")
            }
            R.id.exit -> finish()
            else -> return false
        }
        return true
    }

    private fun addGoods(tm: Int) {

    }

    fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        val c = Calendar.getInstance(Locale.CHINA)
        c.time = Date()
        c.add(Calendar.DAY_OF_MONTH, -29)
        val date = c.time.toString(formatString)
        val sql = "delete from history where date(rq)<'$date'"
        db.execSQL(sql)
        db.close()
        super.onDestroy()
    }

    interface IPostMessage {
        fun postMessage(start: Date, end: Date): Unit
    }
}