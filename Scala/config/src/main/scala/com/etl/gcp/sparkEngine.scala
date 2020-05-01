package com.etl.gcp

/**
  * Initiating spark session to access spark related functionality through sparkSession object
  *
  */
import org.apache.spark.sql.SparkSession

trait sparkEngine {

  var sparkSession: SparkSession = _

  try {

    sparkSession = SparkSession.builder().appName("sparkAppName").master("local").getOrCreate()
    //.config("hive.metastore.uris","thrift://cdts10hdbm01d.rxcorp.com:9083")

  } catch {
    case e: Exception => println("While initializing sparkSession session process got failed,please check the stackTrace for more details" + e.printStackTrace())

      sys.exit(1)
  }

}
