package com.example

import io.ktor.application.*
import io.ktor.application.Application
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.content.*
import io.ktor.auth.*
import io.ktor.sessions.*
import io.ktor.features.*
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import org.json.JSONObject
import org.json.JSONArray

fun main(args: Array<String>): Unit = io.ktor.server.tomcat.DevelopmentEngine.main(args)

fun Application.module() {
	val properties = Properties()
	with(properties){
		put("user", "root")
		put("password", "123456")
	}
	val connection:Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/date?useSSL=true&serverTimezone=GMT", properties);
	
	routing {
        get("/") {
		    call.respondText("地区IP-->http://localhost:8080/ip.html", contentType = ContentType.Text.Plain)
        }
		
		get("/ip") {
		    var res:JSONObject = queryRows(connection,"area_ip")
		    call.respondText(res.toString(), contentType = ContentType.Text.Plain)
        }
	
        static("/") {
            resources("page")
        }
	}
}

//查询列表，查询总量
fun queryRows(connection: Connection, table : String): JSONObject {
    val sql = "SELECT * FROM $table WHERE 1 = 1 ORDER BY access_count DESC;"
    val sqlSum = "SELECT sum(access_count) as sum FROM $table;"
	
    var rs = connection.createStatement().executeQuery(sql)
	var rsSum = connection.createStatement().executeQuery(sqlSum)
	
	var res = JSONObject()
	val array = JSONArray()
    while (rs.next()) {
		var jsonObj = JSONObject()
        jsonObj.put("areaCountry",rs.getString("area_country"))
        jsonObj.put("areaProvince",rs.getString("area_province"))
        jsonObj.put("areaCity",rs.getString("area_city"))
        jsonObj.put("accessCount",rs.getString("access_count"))
        jsonObj.put("lastAccessIp",rs.getString("last_access_ip"))
        jsonObj.put("logDate",rs.getString("log_date"))
        jsonObj.put("updateTime",rs.getString("update_time"))
		array.put(jsonObj);
    }
	while (rsSum.next()) {
		res.put("sum",rsSum.getString("sum"))
	}
	res.put("ips",array)
	
	return res
}

