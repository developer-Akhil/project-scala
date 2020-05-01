package com.etl.gcp

import java.util
import com.etl.gcp.ETLSystem.propertyObj
import org.apache.spark.sql.types.{DateType, StringType, StructField, StructType}
import org.apache.spark.sql.types._
import com.google.gson.Gson
import javax.script.ScriptException

object ETLSystem extends sparkEngine with App   {

  def getFileNameFromCommandLine(argsLength:Int):String= {
    var fileName:String = null
    if(argsLength == 0) {

      println("Pass input file with path in command line argument")

      sys.exit(1)
    } else {
      fileName= args(0)
    }
    fileName
  }

  private val propertyFile=getFileNameFromCommandLine(args.length)
  val propertyObj = ReadingConfigFile(propertyFile)
  private val fileOper = FileOperations


  //println(a.mapFun().get("nifi").toString("nifiurl"))
  //val driver = propertyObj.mapFun().get("nifi").asInstanceOf[java.util.Map[String, List[String]]].get("nifiurl")
  val driver = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String, _]].get("driver").toString
  val dbConnection = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String, _]].get("dbconnection").toString
  val dbUser = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String, _]].get("dbusername").toString
  val dbPasswd = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String, _]].get("dbpassword").toString
  val consumeTable = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String, _]].get("pgdetailstablename").toString
  val elkUrl = propertyObj.mapFun().get("elk").asInstanceOf[java.util.Map[String, _]].get("indexetllogread").toString
  val brokerList:String = propertyObj.mapFun().get("kafka").asInstanceOf[java.util.Map[String, _]].get("brokerUri").toString
  val topic:String = propertyObj.mapFun().get("kafka").asInstanceOf[java.util.Map[String,_]].get("topic").toString
  val sparkAppName = propertyObj.mapFun().get("spark").asInstanceOf[java.util.Map[String,_]].get("sparkappname").toString
  val pgdetailstablename = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String,_]].get("pgdetailstablename").toString

  val loggerMsg= Logger(elkUrl)
  val curTime=fileOper.getCurrentTimeStamp.toString

  var hashMap: Map[String, String] = Map(("Event Time", curTime),  ("Message", " File "))
  hashMap("Message") = "No Files Found in the Input Directory"

  println(hashMap)
  //loggerMsg.hashMapFunc("")= "tested successfully"

  //hashMap("Message") = "tested successfully"
  //val hashMap: Map[String, String] = Map(("Event Time", curTime),  ("Message", " Validating Directory for Input Files"))
  //PullFromDB(dbConnection,driver,dbUser,dbPasswd).readingDataFromDB(pgdetailstablename).show()

  //  val consumerNProducer=ConsumerNProducer(brokerList,topic)
  //
  //  //println(consumerNProducer.consumerMsg())
  //  consumerNProducer.consumeDataThroughSparkStreaming()


  //Loading data into hdfs file system
  //df.write.csv("hdfs://cluster/user/hdfs/test/example.csv")



}
