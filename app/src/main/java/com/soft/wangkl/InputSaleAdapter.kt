package com.soft.wangkl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * Created by yangglemu on 2016/7/12.
 */
class InputSaleAdapter(val context: Context) : BaseAdapter() {
    private val mData = ArrayList<HashMap<String, String>>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.input_sale_dialog_item, null)
            vh = ViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }
        val map = mData[position]
        vh.id.text = map["id"]
        vh.tm.text = map["tm"]
        vh.sl.text = map["sl"]
        vh.zq.text = map["zq"]
        vh.je.text = map["je"]
        return view
    }

    fun addData(map: HashMap<String, String>) {
        mData.add(map)
        for (index in 0..mData.size - 1) mData[index]["id"] = (index + 1).toString()
        notifyDataSetChanged()
    }

    fun removeData(position: Int) {
        mData.removeAt(position)
        for (index in 0..mData.size - 1) mData[index]["id"] = (index + 1).toString()
        notifyDataSetChanged()
    }

    fun getSumSl(): Int = mData.sumBy { it["sl"]?.toInt() ?: 0 }

    fun getSumJe(): Int = mData.sumBy { it["je"]?.toInt() ?: 0 }

    fun insertSaledData(db: SQLiteDatabase) {
        val list = ArrayList<String>()
        val date = Date().toString("yyyy-MM-dd HH:mm:ss")
        val sb = StringBuilder("insert into sale_db(rq,sl,je) ")
        sb.append("values(")
        sb.append("'$date',")
        sb.append("${getSumSl()},")
        sb.append("${getSumJe()})")
        list.add(sb.toString())
        sb.delete(0, sb.length)
        for (index in 0..mData.lastIndex) {
            val map = mData[index]
            sb.append("insert into sale_mx(rq,tm,sl,zq,je) values(")
            sb.append("'$date',")
            sb.append("'${map["tm"]}',")
            sb.append("${map["sl"]},")
            sb.append("${map["zq"]},")
            sb.append("${map["je"]})")
            list.add(sb.toString())
            sb.delete(0, sb.length)
            list.add("update goods set sl=sl-${map["sl"]} where tm='${map["tm"]}'")
        }
        db.beginTransaction()
        try {
            list.forEach { db.execSQL(it) }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            e.printStackTrace(pw)
            toast(sw.toString())
        } finally {
            db.endTransaction()
        }
    }

    override fun getItem(position: Int): Any = mData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mData.size

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.input_sale_dialog_item_id) as TextView
        val tm = v.findViewById(R.id.input_sale_dialog_item_tm) as TextView
        val sl = v.findViewById(R.id.input_sale_dialog_item_sl) as TextView
        val zq = v.findViewById(R.id.input_sale_dialog_item_zq) as TextView
        val je = v.findViewById(R.id.input_sale_dialog_item_je) as TextView
    }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}