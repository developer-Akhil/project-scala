package com.etl.gcp

import org.apache.spark.sql.DataFrame

case class TransformationProcess(dataFrame:DataFrame, propertyFile:ReadingConfigFile, logger:Logger, hashMap: Map[String, String], curTime:String) extends sparkEngine with App {

//case class TransformationProcess(dataFrame: DataFrame) extends sparkEngine with App {

  //val propertyFile="/Users/achand/Documents/Project/BU/ETLSystem/config/src/main/resource/Properties.json"
  private var dfOper = new DFOperations
  private val fileOper = FileOperations
  //val propertyObj = ReadingConfigFile(propertyFile)

  var pickupDF = dfOper.emptyDF
  var dropoffDF = dfOper.emptyDF
  var DF = dfOper.emptyDF

  // Data cleaning
  DF=dataFrame.na.drop()    // Removing null rows

  //Find the peak time of pickup/drops on a daily basis. Consider peak time to be interval of 1 hour.

  //val fileVal=dfOper.readFile("/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/yellow_tripdata_2018-02.csv","trip_record_data",propertyObj)



  //DF=dfOper.readFile("/Users/achand/Downloads/yellow_tripdata_2018-01.csv","trip_record_data",propertyObj)
  //
  DF.na.drop()

  DF.createOrReplaceTempView("tempTable")

  //Find the peak time of pickup/drops on a daily basis. Consider peak time to be interval of 1 hour
  pickupDF=sparkSession.sqlContext.sql("SELECT tpep_pickup_datetime AS datetime, count(*) AS PUcount FROM tempTable GROUP BY tpep_pickup_datetime").cache()

  pickupDF.createOrReplaceTempView("tempTable1")

  pickupDF=sparkSession.sqlContext.sql("""SELECT hour(from_unixtime(unix_timestamp(datetime, "YYYY-MM-DD hh:mm:ss ") ) ) as hours,
    count(PUcount) as pickup_count from tempTable1 group by hour(from_unixtime(unix_timestamp(datetime, "YYYY-MM-DD hh:mm:ss ") ) )
  """).cache()

  //sparkSession.sqlContext.sql("""SELECT hour(from_unixtime(datetimex) ) as hours, count(PUcount) as cnt_avg from tempTable1 group by hour(from_unixtime(datetime) )""").show

  dropoffDF =sparkSession.sqlContext.sql("SELECT tpep_dropoff_datetime AS datetime, count(*) AS PUcount FROM tempTable GROUP BY tpep_dropoff_datetime").cache()


  dropoffDF.createOrReplaceTempView("tempTable1")

  dropoffDF=sparkSession.sqlContext.sql("""SELECT hour(from_unixtime(unix_timestamp(datetime, "YYYY-MM-DD hh:mm:ss ") ) ) as hours,
    count(PUcount) as drop_count from tempTable1 group by hour(from_unixtime(unix_timestamp(datetime, "YYYY-MM-DD hh:mm:ss ") ))
    """).cache()


  DF=pickupDF.join(dropoffDF,pickupDF("hours")=== dropoffDF("hours")).drop(dropoffDF("hours")).cache()
  DF.createOrReplaceTempView("tempTable")
  sparkSession.sqlContext.sql("""SELECT max(pickup_count), min(drop_count) FROM tempTable """).show()


  //Find the average total amount per payment type.
  sparkSession.sqlContext.sql("SELECT payment_type AS Payment_Type, count(*) AS Count, avg(total_amount) Avg_Total_Amount FROM tempTable GROUP BY payment_type").show()

  //Categorise the trips based on its total amount to high, medium and low.
  sparkSession.sqlContext.sql("SELECT max(Total_Amount) ,avg(Total_Amount),min(Total_Amount) FROM tempTable ").show()

  //Find the average trip distance per RateCodeId
  sparkSession.sqlContext.sql("SELECT RateCodeId , count(*) AS Count,avg(trip_distance) as Trip_Distance  FROM tempTable GROUP BY RatecodeID").show()


  //Find anomalies in the average trip distance per Vendor Id.
  sparkSession.sqlContext.sql("SELECT VendorID , count(*) AS Count, avg(trip_distance)  FROM tempTable GROUP BY VendorID").show()

  //Rank the Vendor Id/per day based on the fare amount.
  sparkSession.sqlContext.sql("SELECT fare_amount,VendorID,rank() over(partition by fare_amount order by vendorID)  as Rank FROM tempTable").show()



}
