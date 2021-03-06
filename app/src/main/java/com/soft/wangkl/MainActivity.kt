package com.soft.wangkl

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

class MainActivity : Activity() {
    val db: SQLiteDatabase by lazy {
        DbHelper(this).writableDatabase
    }
    /*
    val email: Email by lazy {
        Email(this, db)
    }
    */
    val mainLayout: LinearLayout by lazy {
        findViewById(R.id.mainLayout) as LinearLayout
    }
    lateinit var listLayout: View
    lateinit var listView: ListView
    //var timer: Timer? = null

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
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        val sale_day = SaleDayAdapter(this@MainActivity, db, start, end)
                        createListLayout(R.layout.sale_day, R.id.listView_sale_day, sale_day)
                        toast("选择日期:按天汇总")
                    }
                }).show()
            }
            R.id.newGoods -> {
                InputNewGoods(this, R.style.input_dialog_style, db).show()
                toast("新建商品")
            }
            R.id.sy -> {
                InputSaleDialog(this@MainActivity, R.style.input_dialog_style, db).show()
                toast("收银")
            }
            R.id.rk -> {
                InputCkAndRkDialog(this, R.style.input_dialog_style, "入库操作", db).show()
                toast("入库")
            }
            R.id.ck -> {
                InputCkAndRkDialog(this, R.style.input_dialog_style, "出库操作", db).show()
                toast("出库")
            }
            R.id.rk_ls -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        try {
                            val adapter = HistoryRkAdapter(this@MainActivity, db, start, end)
                            createListLayout(R.layout.history, R.id.listView_history, adapter)
                            toast("入库历史")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }).show()
            }
            R.id.ck_ls -> {
                MyDatePicker(this, R.style.datePickerDialog, object : IPostMessage {
                    override fun postMessage(start: Date, end: Date) {
                        try {
                            val adapter = HistoryCkAdapter(this@MainActivity, db, start, end)
                            createListLayout(R.layout.history, R.id.listView_history, adapter)
                            toast("出库历史")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }).show()
            }
            R.id.help->{
                HelpDialog(this, android.R.style.Theme_Holo_Dialog).show()
            }
            R.id.exit -> finish()
            else -> return false
        }
        return true
    }


    fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        /*
        val c = Calendar.getInstance(Locale.CHINA)
        c.time = Date()
        c.add(Calendar.DAY_OF_MONTH, -29)
        val date = c.time.toString(formatString)
        val sql = "delete from history where date(rq)<'$date'"
        db.execSQL(sql)
        */
        db.close()
        super.onDestroy()
    }

    interface IPostMessage {
        fun postMessage(start: Date, end: Date) {
        }
    }

}