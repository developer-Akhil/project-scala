package com.etl.gcp

import java.util

import org.apache.spark.sql.types.{DateType, StringType, StructField, StructType}
import org.apache.spark.sql.types._
import com.google.gson.Gson
import javax.script.ScriptException
import com.etl.gcp.DeDuplication
import java.nio.file.Paths




object ETLSystem extends sparkEngine with App   {

  def getFileNameFromCommandLine(argsLength:Int):String = {

    var fileName:String = null

    if(argsLength == 0) {

      println("Pass input file with path in command line argument")

      sys.exit(1)
    } else {

      fileName= args(0) + "," + args(1)
    }


    fileName
  }
  
  private val propertyFile= getFileNameFromCommandLine(args.length).split(",")(1)
  val filename = getFileNameFromCommandLine(args.length).split(",")(0)
  val propertyObj = ReadingConfigFile(propertyFile)
  private val fileOper = FileOperations
  private val dfOper = new DFOperations


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
  val historytable = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String,_]].get("historytable").toString
  val duplicatetable = propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String,_]].get("duplicatetable").toString
  val table = propertyObj.mapFun().get("schema").asInstanceOf[java.util.Map[String,_]].get("table").toString
  val hiveschema=propertyObj.mapFun().get("db").asInstanceOf[java.util.Map[String,_]].get("hiveschema").toString
  val key=propertyObj.mapFun().get("crpyto").asInstanceOf[java.util.Map[String,_]].get("key").toString

  val loggerMsg= Logger(elkUrl)
  val curTime=fileOper.getCurrentTimeStamp.toString

  var hashMap: Map[String, String] = Map(("Event Time", curTime),  ("Message", s" File processing is read for $filename "))

  fileOper.isFileExist(filename)
  val path = Paths.get(filename)
  val dirName = path.getParent
  fileOper.isFileEmpty(dirName.toString,filename)
  val schema=dfOper.customSchema(table,propertyObj)

  //Consume from kafka producer
  val conNProd=ConsumerNProducer(brokerList,topic,loggerMsg,hashMap,curTime)
  conNProd.consumeDataThroughSparkStreaming()
  //Consume from rest api
  val condTroughApi=ConsumeThroughApi(loggerMsg,hashMap,curTime)
  condTroughApi.routes
  //Consume from through db
  val pullFromDB=PullFromDB(dbConnection,driver,dbUser,dbPasswd,loggerMsg,hashMap,curTime)
  pullFromDB.readingDataFromDB(consumeTable)


  //Deduplication
  val dedup = DeDuplication(filename,loggerMsg,hashMap,curTime,historytable,duplicatetable,hiveschema)
  dedup.deDupOnFileContain(filename)
  val DF =dedup.deDupWithInFileNAcrossDB(table,propertyObj)

  //Tranformation
  TransformationProcess(DF,propertyObj,loggerMsg,hashMap,curTime)

  val inputFile="/some/location/"

  DF.write.csv(inputFile)

  //Encryption
  val crypto=CryptographyAlgo(inputFile,key,loggerMsg,hashMap,curTime)
  crypto.cryptoFun("encrypt")

  val destFilePath="hdfs://cluster/user/hdfs/test/example.csv"
  //Loading data into hdfs file system
  val dumpToDataLake=DumpToDataLake()
  dumpToDataLake.dumptoDatalake(inputFile,destFilePath)
  //df.write.csv("hdfs://cluster/user/hdfs/test/example.csv")



  //loggerMsg.hashMapFunc("")= "tested successfully"

  //hashMap("Message") = "tested successfully"
  //val hashMap: Map[String, String] = Map(("Event Time", curTime),  ("Message", " Validating Directory for Input Files"))
  //PullFromDB(dbConnection,driver,dbUser,dbPasswd).readingDataFromDB(pgdetailstablename).show()

  //  val consumerNProducer=ConsumerNProducer(brokerList,topic)
  //
  //  //println(consumerNProducer.consumerMsg())
  //  consumerNProducer.consumeDataThroughSparkStreaming()

}
