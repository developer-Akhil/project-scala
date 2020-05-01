package com.etl.gcp


import com.google.gson.Gson
import javax.script.ScriptException

import scala.io.Source

/**
  * Created by AChand on 2/7/2017.
  */
case class ReadingConfigFile(jsonConfigFile: String) {

  def mapFun() :java.util.Map[String,_] ={

    var jsonString: String = ""
    try {
      for (line <- Source
        .fromFile(jsonConfigFile)
        .getLines()) jsonString += line
    }
    catch {
      //case e: ScriptException => println("Error is " + e.printStackTrace())
      case e: Exception => println("Error is " + e.printStackTrace())
    }
    val gson = new Gson ()
    var json2Map: java.util.Map[String, String] = null
    try {
      json2Map = gson.fromJson (jsonString, classOf[java.util.Map[String, String]] )
    }
    catch {
      //case e: ScriptException => println("Error is " + e.printStackTrace())
      case e: Exception => println("While converting json to Map process got failed,please check the stackTrace for more details" + e.printStackTrace())
        sys.exit ()
    }

    json2Map
  }
  //  def mapFun(tableName:String) :Map[String,List[String]]= {
  //        val jsonFile = jsonConfigFile
  //        val fileContent = Source.fromFile(jsonFile, "UTF-8").getLines.mkString
  //        val mapper = new ObjectMapper() with ScalaObjectMapper
  //        mapper.registerModule(DefaultScalaModule)
  //        val withMap = mapper.readValue(fileContent, classOf[Map[String, _]])
  //        val mapVal = withMap(tableName).asInstanceOf[Map[String, String]]("schema").asInstanceOf[Map[String, List[String]]]
  //
  //        mapVal



}
