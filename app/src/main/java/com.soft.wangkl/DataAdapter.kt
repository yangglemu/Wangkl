package com.soft.wangkl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.widget.Toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuan on 2016/6/16.
 */
abstract class DataAdapter(context: Context, sqlite: SQLiteDatabase, var start: Date? = null, var end: Date? = null) : BaseAdapter() {
    val mContext = context
    val mInflater = LayoutInflater.from(mContext)
    val mData = ArrayList<HashMap<String, String>>()
    val db = sqlite
    val decimalFormatter = DecimalFormat("#,###.00")
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    init {
        initData()
    }

    protected abstract fun initData()
    abstract fun compute()

    override fun getCount(): Int = mData.size

    override fun getItem(position: Int): Any? = mData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun toast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }
}