package com.soft.wangkl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.sun.mail.pop3.POP3Folder
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun Date.toString(formatString: String): String {
    return SimpleDateFormat(formatString, Locale.CHINA).format(this)
}
class Email(val context: Context, val db: SQLiteDatabase) {
    companion object {
        val smtpHost = "smtp.163.com"
        val pop3Host = "pop.163.com"
        val smtpPort = 465
        val pop3Port = 995
        val username = "yangglemu"
        val password = "yuanbo132"
        val mailBox = "yangglemu@163.com"
        val subject = "sunshine"
        val url = "jdbc:mysql://pc201408020832:3306/duobao?user=root&password=yuanbo960502"
    }

    fun send(content: String) {
        val p = Properties()
        p.put("mail.smtp.ssl.enable", true)
        p.put("mail.smtp.host", smtpHost)
        p.put("mail.smtp.port", smtpPort)
        p.put("mail.smtp.auth", true)
        val session = Session.getInstance(p, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(username, password)
        })
        val msg = MimeMessage(session)
        msg.setFrom(InternetAddress(mailBox))
        msg.setRecipients(MimeMessage.RecipientType.TO, mailBox)
        msg.subject = subject
        msg.setText(content)
        Transport.send(msg)
    }

    fun receive() {
        val p = Properties()
        p.put("mail.pop3.ssl.enable", true)
        p.put("mail.pop3.host", pop3Host)
        p.put("mail.pop3.port", pop3Port)
        val session = Session.getInstance(p)
        val store = session.getStore("pop3")
        store.connect(username, password)
        val folder = store.getFolder("INBOX") as POP3Folder
        folder.open(Folder.READ_ONLY)
        for (msg in folder.messages) {
            if (msg.subject != subject) continue
            val uid = folder.getUID(msg)
            if (isNewMessage(uid)) {
                insertIntoDatabase(msg.content.toString())
                db.execSQL("insert into history(uid,rq) values('$uid','${Date().toString("yyyy-MM-dd HH:mm:ss")}')")
            }
        }
        folder.close(false)
        store.close()
    }

    fun insertIntoDatabase(content: String) {
        val doc = string2Document(content)
        val root = doc.documentElement
        val goods = root.getElementsByTagName("goods").item(0).childNodes
        val sale_mx = root.getElementsByTagName("sale_mx").item(0).childNodes
        val sale_db = root.getElementsByTagName("sale_db").item(0).childNodes

        if (goods.length > 0) {
            for (index in 0..goods.length - 1) {
                val attr = goods.item(index).attributes
                val tm = attr.getNamedItem("tm").nodeValue
                var sj = tm
                var sl = attr.getNamedItem("sl").nodeValue
                var zq = "1.00"
                db.execSQL("replace into goods (tm,sj,zq,sl) values('$tm',$sj,$zq,$sl)")
            }
        }

        if (sale_db.length > 0) {
            for (index in 0..sale_db.length - 1) {
                val attr = sale_db.item(index).attributes
                val rq = attr.getNamedItem("rq").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val je = attr.getNamedItem("je").nodeValue
                db.execSQL("replace into sale_db (rq,sl,je) values('$rq',$sl,$je)")
            }
        }

        if (sale_mx.length > 0) {
            for (index in 0..sale_mx.length - 1) {
                val attr = sale_mx.item(index).attributes
                val id = attr.getNamedItem("id").nodeValue
                val rq = attr.getNamedItem("rq").nodeValue
                val tm = attr.getNamedItem("tm").nodeValue
                val sl = attr.getNamedItem("sl").nodeValue
                val zq = attr.getNamedItem("zq").nodeValue
                val je = attr.getNamedItem("je").nodeValue
                db.execSQL("replace into sale_mx (id,rq,tm,sl,zq,je) values('$id','$rq','$tm',$sl,$zq,$je)")
            }
        }
    }

    fun string2Document(content: String): Document {
        val reader = StringReader(content)
        val stream = InputSource(reader)
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream)
        return doc
    }

    fun isNewMessage(uid: String): Boolean {
        var value = true
        val cursor = db.rawQuery("select count(*) as count from history where uid='$uid'", null)
        if (cursor.moveToNext()) {
            val count = cursor.getInt(0)
            if (count > 0) value = false
        }
        cursor.close()
        return value
    }

    fun getXmlContent(date: Date): String {
        var formatString = "yyyy-MM-dd"
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        var root = doc.createElement("duobao")
        doc.appendChild(root)

        var connection = DriverManager.getConnection(url)
        var statement = connection.createStatement()
        var res_goods = statement.executeQuery("select sj as tm,sum(kc) as sl from goods group by sj")
        var goods = doc.createElement("goods")
        root.appendChild(goods)
        while (res_goods.next()) {
            var tm = res_goods.getInt("tm").toString()
            var sl = res_goods.getInt("sl").toString()
            var element = doc.createElement("goods")
            element.setAttribute("tm", tm)
            element.setAttribute("sl", sl)
            goods.appendChild(element)
        }

        var res_sale_mx = statement.executeQuery("select sale_mx.id as id,sale_db.rq as rq,sale_mx.sj as tm,sale_mx.sl as sl,"
                + "sale_mx.zq as zq,sale_mx.je as je from sale_mx join sale_db "
                + "on(sale_mx.djh=sale_db.djh) where date(sale_db.rq)='${date.toString(formatString)}'")
        var sale_mx = doc.createElement("sale_mx")
        root.appendChild(sale_mx)
        while (res_sale_mx.next()) {
            val id = res_sale_mx.getString("id")
            var rq = res_sale_mx.getString("rq")
            var tm = res_sale_mx.getString("tm")
            var sl = res_sale_mx.getString("sl")
            var zq = res_sale_mx.getString("zq")
            var je = res_sale_mx.getString("je")
            var element = doc.createElement("sale_mx")
            element.setAttribute("id", id)
            element.setAttribute("rq", rq)
            element.setAttribute("tm", tm)
            element.setAttribute("sl", sl)
            element.setAttribute("zq", zq)
            element.setAttribute("je", je)
            sale_mx.appendChild(element)
        }

        var res_sale_db = statement.executeQuery("select rq,sl,je from sale_db where date(rq)='${date.toString(formatString)}'")
        var sale_db = doc.createElement("sale_db")
        root.appendChild(sale_db)
        while (res_sale_db.next()) {
            var rq = res_sale_db.getString("rq")
            var sl = res_sale_db.getString("sl")
            var je = res_sale_db.getString("je")
            var element = doc.createElement("sale_db")
            element.setAttribute("rq", rq)
            element.setAttribute("sl", sl)
            element.setAttribute("je", je)
            sale_db.appendChild(element)
        }
        res_goods.close()
        statement.close()
        connection.close()

        var stream = ByteArrayOutputStream()
        var tans = TransformerFactory.newInstance().newTransformer()
        tans.transform(DOMSource(doc), StreamResult(stream))
        var content = stream.toString()
        stream.close()
        return content
    }

}