package com.etl.gcp

import com.google.gson.GsonBuilder
import javax.script.ScriptException
import scalaj.http.{Http, HttpOptions}

case class Logger(url:String) {

  /* Hash map function example */
  def hashMapFunc( hm:Map[String,String]):Unit= {
    val gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation.create()
    val jsonString = gson.toJson(hm,hm.getClass)
    println(jsonString)
    //logtoELK(jsonString)
  }

  def logtoELK(body:String): Unit ={
    try {
     //val result = Http(url).param("","").asString
     //val result = Http(url).put(body).asString

    //Http(url).
      val result = Http(url).postData(body)
        .header("Content-Type", "application/json")
        .header("Charset", "UTF-8")
        .option(HttpOptions.readTimeout(10000)).asString

      if (result.code != 200 && result.isSuccess){
        println("While inserting message into ELK it is failing")
      }

    } catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
    }

  }

//  val body: String = """{
//  "Component Type": "GetSFTP",
//  "Component Name": "GetSFTP",
//  "Event Time": "2020-03-25 07:19:14.234",
//  "Filename": "Z_VPV021L_016860_02022020_999.enc",
//  "File Siz": "209.3 KB",
//  "Path": "sftp://mft-int.imshealth.com//in/Z_VPV021L_016860_02022020_001.enc",
//  "Message": "Move file from sftp server to nifi filesystem",
//  "Source Type": "RECEIVE",
//  "Processor Id": "2380e387-853b-3c74-3136-87eb205a740b",
//  "Process Group Id": "cf16594f-ce61-3fd1-adcb-631585c20cdd",
//  "Flowfile UUID": "7a82503b-fd88-449a-b9bd-cc937ed71b64",
//  "Connection Id": "No Value"
//  }""".stripMargin
//  val url="http://bdfelasticdev.rxcorp.com/dqe_rx_processor_log_dev_read/doc/2380e387-853b-3c74-3136-87eb205a740b_Z_VPV021L_016860_02022020_001_N"

  //println(logtoELK(url,body))

}
