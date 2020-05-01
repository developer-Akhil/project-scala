package com.etl.gcp

import cats.effect.IO
import com.twitter.finagle.http.Request
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.{Endpoint, InternalServerError, Ok}
import com.etl.gcp.model.consumeClass
import javax.script.ScriptException
import org.apache.spark.sql.DataFrame


/**
*
* "Finch is a thin layer of purely functional basic blocks atop of Finagle for building composable HTTP APIs."
* ConsumeThroughApi function consumes or produce log and message through restapi like POST,GET,PUT and DELETE
*
*
* */

case class ConsumeThroughApi(logger:Logger,hashMap: Map[String, String],curTime:String) extends Endpoint.Module[IO] {

//  curl -H "Content-Type: application/json" -X POST http://localhost:8000/msg-consumer-api -d '{
//    "coRelationId": "12w34fg4567asds3fgh0paszxq1234",
//    "filePath": "/Users/achand/Documents/Project/DQE/FileDeDup/src/main/resources/input/",
//    "fileName": "patient_01.txt,problem_list_01.txt, visit_01.txt,visit_02.txt,visit_03.txt,patient_01.txt”,
//    "OutFolder" : "/Users/achand/Documents/Project/DQE/FileDeDup/src/main/resources/output/"
//    "invalidFolderPath": "/Users/achand/Documents/Project/DQE/FileDeDup/src/main/resources/error/",
//    "lookupKey": "Client_Name+Layout_Name+Patient_tab"
//  }'

    case class SparkS() extends sparkEngine{
       val ss=sparkSession
    }
    logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Consuming log from rest api services" )))

      private val dfOper = new DFOperations
      private var DF = dfOper.emptyDF
      private val consumerApi = post("msg-consumer-api" :: jsonBody[consumeClass]) { m: consumeClass =>
        val consumeData = Seq(consumeClass(m.coRelationId, m.filePath, m.fileName, m.OutFolder, m.invalidFolderPath, m.lookupKey))
        //val sparkS= SparkSession.builder().appName("sparkAppName").master("local").getOrCreate()
        val sparkS = SparkS()

        import sparkS.ss.implicits._

        DF = consumeData.toDF

        Ok("Data consumed successfully")
      }

      val healthCheck: Endpoint[IO, String] = get("health") {
        Ok("test")
      }

      private val routes = consumerApi :+: healthCheck
      private val api =
      routes.handle {
        case e: Exception => InternalServerError(e)
        //case e: Exception => log("While initiating post call getting given error " + InternalServerError(e).toString)
      }

      val endpoints: Service[Request, com.twitter.finagle.http.Response] = api.toService

      try {
        logger.hashMapFunc( Map(("Event Time", curTime),  ("Message", " Initiating rest api" )))
        Await.ready(Http.server.serve(":8000", endpoints))
        //Await.ready(Http.server.serve(":8000", ak.api.toServiceAs[Text.Plain]))
        //Await.ready(ak.ak1 serve(s":$port", ak.api.toService))

      } catch {
        case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While consume data rest api ,process got fail" +  e.printStackTrace())))
          sys.exit()
      }

}

