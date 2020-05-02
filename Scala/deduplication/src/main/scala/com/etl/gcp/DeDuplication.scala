package com.etl.gcp

import javax.script.ScriptException
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, concat_ws, md5}


case class DeDuplication(fileName:String,logger:Logger, hashMap: Map[String, String], curTime:String, historyTable:String, dedupTab:String, hiveschema:String) extends sparkEngine {

    private var dfOper = new DFOperations
    private var sourDF = dfOper.emptyDF
    private var tagDF = dfOper.emptyDF
    private var freeDF = dfOper.emptyDF
    private val fileOper = FileOperations
    private var freeDFDeDup = dfOper.emptyDF
    private val currentTS=fileOper.getCurrentTimeStamp

  /**
    * deDupOnFileContain function is used to check Duplicate and unique file and load accordingly into regular and move into error folder
    * MD5 : Calculates the MD5 digest of a binary column and returns the value  as a 32 character hex string
    *
    */
    def deDupOnFileContain:DataFrame= {
    //fileOper.calculateChecksum("/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/yellow_tripdata_2018-02.csv", "MD5")
      logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " Initiating file deduplication on file contains" )))

      val checkSum=fileOper.calculateChecksum(fileName, "MD5")

    //sparkSession.sqlContext.sql("select * from devl_us9_mmi_batch.problem_list_err")

      sourDF = sparkSession.sqlContext.sql(s"SELECT * from $hiveschema.$historyTable where checkSum='$checkSum'").cache()
      val countVal:Long = sparkSession.sqlContext.sql(s"SELECT * from $hiveschema.$historyTable where checkSum='$checkSum'").cache().count()

      try {


      if (countVal == 0) {

        sourDF

      } else {
        sparkSession.sqlContext.sql(s"insert into $hiveschema.$dedupTab select $checkSum,$fileName,$currentTS ")
        println("File is deduplicate")
        sys.exit()

      }
    } catch{
      //case e: ScriptException => println("Error is " + e.printStackTrace())
      case e:ScriptException => logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " While dedup on file contains process got fail" +  e.printStackTrace())))
      sys.exit()
    }

    }

  /**
    * deDupWithInFileAcrossDB function is used to check Duplicate and unique records and load accordingly into regular and bad table
    * MD5 : Calculates the MD5 digest of a binary column and returns the value  as a 32 character hex string
    *
    * @param tableName table name
    * @param propertyObj it contains properties and user defined variable
    */
    def deDupWithInFileNAcrossDB(tableName:String,propertyObj:ReadingConfigFile):DataFrame= {

      val DF=dfOper.readFile(fileName:String,tableName:String,propertyObj:ReadingConfigFile).na.drop()

      //val fileVal=dfOper.readFile("/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/yellow_tripdata_2018-02.csv","trip_record_data",propertyObj)

      logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Initiating deduplication with file and across the file" )))
      try {
        sourDF = DF.withColumn("md5_32bitCol", md5(concat_ws("||", DF.columns.map(col): _*))).cache()

        val uniqueDF = sourDF.dropDuplicates("md5_32bitCol")      //DeDup within the file//
        val duplicateDF = sourDF.exceptAll(uniqueDF)              //******************//

        if (duplicateDF.count() > 0) {
          //sourDF = duplicateDF.drop("md5_32bitCol")
          //DeDup between table//
          //Process to get unique records//
          sourDF.createOrReplaceTempView("sourTable")
          tagDF=sparkSession.sqlContext.sql(s"select * from $hiveschema.$historyTable").cache()
          tagDF=tagDF.withColumn("md5_32bitCol", md5(concat_ws("||", DF.columns.map(col): _*))).cache()
          freeDF=sparkSession.sqlContext.sql(s"select * from sourTable s where not exists(select 1 from $hiveschema.$historyTable h where s.md5_32bitCol=h.md5_32bitCol)")
          .drop("md5_32bitCol")
          .cache()
          freeDF.createOrReplaceTempView("freeTable")
          //sparkSession.sqlContext.sql("insert into devl_edt_nyc_batch.problem_list  select * from  freeTable ").cache()
          sparkSession.sqlContext.sql(s"insert into $hiveschema.$dedupTab select md5_32bitCol,$fileName,$currentTS from freeTable")
          //Process to get duplicate records//
          freeDFDeDup=sparkSession.sqlContext.sql(s"select * from sourTable s where exists(select 1 from  $hiveschema.$historyTable h where h.md5_32bitCol=g.md5_32bitCol) ").cache()
          .drop("md5_32bitCol")
          .cache()
          freeDFDeDup.createOrReplaceTempView("freeTable")
          sparkSession.sqlContext.sql(s"insert into $hiveschema.$dedupTab select md5_32bitCol,$fileName,$currentTS from freeTable").cache()
          dfOper.releaseDF(sourDF)
          dfOper.releaseDF(tagDF)
          dfOper.releaseDF(freeDFDeDup)
          freeDF
        }
        else {
          sourDF = duplicateDF.drop("md5_32bitCol")
          sourDF.createOrReplaceTempView("tmpTable_err")
          sparkSession.sqlContext.sql("insert into devl_edt_nyc_batch.historyTab  select * from  tmpTable_err ")
          dfOper.releaseDF(sourDF)
          freeDF
        }
      }catch {
        //case e: ScriptException => println("Error is " + e.printStackTrace())
        case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While dedup on file contains process got fail" +  e.printStackTrace())))
        sys.exit()
      }

    }



  }


