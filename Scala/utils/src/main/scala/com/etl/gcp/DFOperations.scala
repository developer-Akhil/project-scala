package com.etl.gcp

import javax.script.ScriptException
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.types._

class DFOperations extends sparkEngine {

  /**
    * Creating an empty dataframe for regular purpose
    *
    */

  def emptyDF: DataFrame = {
    var DF:DataFrame= null
    try {
      val schema = StructType(List())
      val sc = sparkSession.sparkContext
      DF = sparkSession.createDataFrame(sc.emptyRDD[Row], schema)

    }catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
    }
    DF
  }

  /**
    * Releaseing dataframe from memory
    *
    *  @param dataFrame pass dataframe as input to releaseDF
    *
    */
  def releaseDF(dataFrame:DataFrame){
    try {
      dataFrame.unpersist()
    } catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
    }
  }



  def getDataType (dataTypeVal: String): DataType = {
    var dataType: DataType = null
    if (dataTypeVal == "IntegerType") {
      dataType = IntegerType
    }
    else if (dataTypeVal == "StringType") {
      dataType = StringType
    }
    else if (dataTypeVal == "LongType") {
      dataType = LongType
    } else if (dataTypeVal == "TimestampType"){
      dataType = TimestampType
    } else if (dataTypeVal == "DoubleType"){
      dataType = DoubleType
    }
    dataType
  }

  /**
    * customSchema creating a custom schema for given input file
    * @param tableName get the schema for given table
    * @param propertyObj is a object to access the propertires.json file
    */

  def customSchema (tableName:String,propertyObj:ReadingConfigFile):StructType = {
    val array: java.util.ArrayList[String] = new java.util.ArrayList[String]()
    val strucArray: java.util.ArrayList[StructField] = new java.util.ArrayList[StructField]()
    val schema = propertyObj.mapFun().get("schema").asInstanceOf[java.util.Map[String, String]].get(tableName).asInstanceOf[java.util.Map[String, String]]
    schema.keySet().forEach(i => array.add(i + "->" + schema.get(i)))

    array.forEach(i => strucArray.add(StructField(i.toString.split("->")(0), getDataType(i.toString.split("->")(1).replace("[", "").replace("]", "").split(",")(0)))))
    StructType(strucArray)
  }

  /**
    * readFile function read the input file and return a Dataframe
    *
    *  @param fileName readFile function required file name as input
    *  @param tableName get the schema for given table
    *  @param propertyObj is a object to access the propertires.json file
    */

  def readFile(fileName:String,tableName:String,propertyObj:ReadingConfigFile): DataFrame ={

    val schema = customSchema(tableName,propertyObj)
    var DF = emptyDF
    try {
      DF = sparkSession.read.
        format("com.databricks.spark.csv").
        schema(schema).
        option("delimiter", ",").
        load(fileName).persist()


    } catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
    }
    DF
  }
}
