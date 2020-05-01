package com.etl.gcp

import javax.script.ScriptException
import org.apache.spark.sql.{DataFrame, SaveMode}


case class PullFromDB(url:String,driver:String,user:String,passwd:String,logger:Logger,hashMap: Map[String, String],curTime:String) extends sparkEngine {

  private val dfOper = new DFOperations
  private var dataFrame = dfOper.emptyDF

  def readingDataFromDB(sqlQuery:String): DataFrame = {
    try {
      logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Consuming data from Oracle database" )))
      dataFrame = sparkSession.sqlContext.read
        .format("jdbc")
        .option("url", url)
        .option("driver", driver)
        .option("useUnicode", "true")
        .option("continueBatchOnError", "true")
        .option("useSSL", "false")
        .option("user", user)
        .option("password", passwd)
        .option("dbtable", sqlQuery)
        .load().toDF().cache()
      dataFrame
    }catch{
        case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While reading data from Oracle database ,process got fail" +  e.printStackTrace())))
          sys.exit()
      }
    }


  def writeDataFrameDB(dataFrame:DataFrame): Unit = {
    try {
      logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Loading data into Oracle database " )))
      dataFrame.write
        .format("jdbc")
        .mode(SaveMode.Overwrite)
        .option("url", url)
        .option("driver", driver)
        .option("useUnicode", "true")
        .option("continueBatchOnError", "true")
        .option("useSSL", "false")
        .option("user", user)
        .option("password", passwd)
        .save() //.saveAsTable()
    } catch {
      case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While writing data into Oracle database ,process got fail" +  e.printStackTrace())))
        sys.exit()
    }
  }


}
