package com.soft.wangkl

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.widget.Toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuan on 2016/6/16.
 */
abstract class DataAdapter(context: MainActivity, sqlite: SQLiteDatabase, start: Date? = null, end: Date? = null) : BaseAdapter() {
    val mContext = context
    val mInflater = LayoutInflater.from(mContext)
    val mData = ArrayList<HashMap<String, String>>()
    val db = sqlite
    var start: Date? = start
    var end: Date? = end
    val decimalFormatter = DecimalFormat("#,###.00")
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    init {
        initData()
        compute()
    }

    protected abstract fun initData()

    protected abstract fun compute()

    abstract fun setSort(v: View)

    fun setClick(tv: View, name: String) {

        when (name) {
            "rq" -> {
                tv.setOnClickListener {
                    val lastMap = mData[mData.size-1]
                    mData.remove(lastMap)
                    if (tv.tag == "desc") {
                        try {
                            mData.sortByDescending { dateTimeFormatter.parse(it["rq"]) }
                        }catch (e:Exception){
                            Log.e("desc","$e")
                        }
                        tv.tag = "asc"
                    } else {
                        mData.sortBy { dateTimeFormatter.parse(it[name]) }
                        tv.tag = "desc"
                    }
                    mData.add(lastMap)
                    notifyDataSetChanged()
                }
            }
            "zq" -> {
                tv.setOnClickListener {
                    val lastMap = mData[mData.size-1]
                    mData.remove(lastMap)
                    if (tv.tag == "desc") {
                        mData.sortByDescending { it[name]?.toFloat() }
                        tv.tag = "asc"
                    } else {
                        try {
                            mData.sortBy { it[name]?.toFloat() }
                        }catch (e:Exception){
                            Log.e("zq","$e")
                        }
                        tv.tag = "desc"
                    }
                    mData.add(lastMap)
                    notifyDataSetChanged()
                }
            }
            else -> {
                tv.setOnClickListener {
                    val lastMap = mData[mData.size-1]
                    mData.remove(lastMap)
                    if (tv.tag == "desc") {
                        mData.sortByDescending { it[name]?.toFloat() }
                        tv.tag = "asc"
                    } else {
                        try {
                            mData.sortBy { it[name]?.toFloat() }
                        }catch (e:Exception){
                            Log.e("else","$e")
                        }
                        tv.tag = "desc"
                    }
                    mData.add(lastMap)
                    notifyDataSetChanged()
                }
            }
        }

    }


    override fun getCount(): Int = mData.size

    override fun getItem(position: Int): Any? = mData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun toast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }
}