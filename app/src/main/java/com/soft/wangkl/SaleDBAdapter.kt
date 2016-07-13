package com.soft.wangkl

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by 123456 on 2016/6/24.
 */

class SaleDBAdapter(context: MainActivity, db: SQLiteDatabase, start: Date, end: Date) :
        DataAdapter(context, db, start, end) {
    override fun initData() {
        if (end!!.before(start!!)) {
            toast("起始时间必须在结束时间之前!")
            return
        }
        val s = start?.toString(MainActivity.formatString)
        val e = end?.toString(MainActivity.formatString)
        var c = db.rawQuery("select rq,sl,je from sale_db where date(rq)>='$s' and date(rq)<='$e' order by rq asc", null)
        var id = 0
        while (c.moveToNext()) {
            val map = HashMap<String, String>()
            map["id"] = (++id).toString()
            map["rq"] = dateTimeFormatter.format(dateTimeFormatter.parse(c.getString(0)))
            map["sl"] = c.getString(1)
            map["je"] = decimalFormatter.format(c.getInt(2))
            mData.add(map)
        }
        c.close()
    }

    override fun compute() {
        val sum_sl = mData.sumBy { it["sl"]!!.toInt() }
        val sum_je = mData.sumBy { decimalFormatter.parseObject(it["je"]!!).toString().toInt() }
        val map = HashMap<String, String>()
        map["id"] = "合计"
        map["rq"] = "来客数:${mData.size}"
        map["sl"] = sum_sl.toString()
        map["je"] = decimalFormatter.format(sum_je)
        mData.add(map)
    }

    override fun setSort(v: View) {
        val rq = v.findViewById(R.id.sale_db_header_rq)
        val sl = v.findViewById(R.id.sale_db_header_sl)
        val je = v.findViewById(R.id.sale_db_header_je)
        setClick(sl, "sl")
        setClick(je, "je")
        setClick(rq, "rq")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var vh: ViewHolder
        var v: View
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_db_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val map = mData[position]
        vh.id.text = map["id"]
        vh.rq.text = map["rq"]
        vh.sl.text = map["sl"]
        vh.je.text = map["je"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_db_id) as TextView
        val rq = v.findViewById(R.id.sale_db_rq) as TextView
        val sl = v.findViewById(R.id.sale_db_sl) as TextView
        val je = v.findViewById(R.id.sale_db_je) as TextView
    }

}