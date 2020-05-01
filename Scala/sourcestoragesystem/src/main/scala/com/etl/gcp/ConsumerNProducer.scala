package com.etl.gcp

import java.util
import java.util.Properties

import com.google.gson.GsonBuilder
import javax.script.ScriptException
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StringType, StructType, TimestampType}

import scala.collection.JavaConverters._

case class ConsumerNProducer(brokerList:String,topic:String,logger:Logger,hashMap: Map[String, String],curTime:String) extends sparkEngine {

  /**
    * Setting up the kafka related configuration
    *
    */
  val props = new Properties ()
    props.put ("bootstrap.servers", brokerList)
    props.put ("acks", "1")
    props.put("group.id", "test")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("session.timeout.ms", "30000")
    props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
    //props.put("partition.assignment.strategy", "range")


//  def msgProducer():Unit ={
//
//    val producer = new KafkaProducer[String, String] (props)
//
//    for (i <- 1 to 50) {
//      val record = new ProducerRecord (topic, "key" + i, "value" + i)
//      producer.send (record)
//    }
//    producer.close ()
//  }

  /**
    * producerMsg function is used to produce the log
    *
    *  @param msg it contains massage and logs
    *
    */

  def producerMsg(msg:String):Unit ={

    val producer = new KafkaProducer[String, String] (props)
    val record = new ProducerRecord (topic, "key"+ 1, msg)
    producer.send (record)
    producer.flush()
    producer.close ()
  }

  /**
    * consumerMsg function is used to consume the log and message
    *
    */
  def consumerMsg:String= {
    logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Consuming log from messaging queue or kafka system" )))
    val consumer = new KafkaConsumer[String, String](props)
    var msg = ""
    consumer.subscribe(util.Collections.singletonList(topic))

    while (true) {
      val records = consumer.poll(100)
      for (record <- records.asScala) {
        msg=record.value()
      }
    }
    msg
  }

  /**
    * consumeDataThroughSparkStreaming function is used to consume the log and message from message queue system like kafka,streamsets,web logs
    *
    */
  def consumeDataThroughSparkStreaming(): DataFrame ={
    try{
      logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Consuming log from messaging queue or kafka system" )))
      var fileDFOper = new DFOperations
      var DF = fileDFOper.emptyDF
      val schema = new StructType()
        .add("Component Type",StringType)
        .add("Component Name",StringType)
        .add("Event Time",TimestampType)
        .add("Filename",StringType)

      DF = sparkSession
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", brokerList)
        .option("subscribe", topic)
        .option("startingOffsets", "earliest")
        //.option("endingOffsets", "latest")
        .load()

      DF=DF.selectExpr("CAST(value AS STRING)")
      DF=DF.select(from_json(col("value"), schema).as("data"))
      .select("data.*")

      val query = DF.writeStream
        .outputMode("append")
        .format("console")
        .start()
        query.awaitTermination()
      DF
  } catch {
      case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While consume data messaging queue or kafka system,process got fail" +  e.printStackTrace())))
        sys.exit()
    }

}}
