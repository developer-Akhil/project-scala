package com.etl.gcp

import java.io.{File, FileInputStream, FileOutputStream}

import javax.script.ScriptException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

case class CryptographyAlgo(inputFile:String ,key:String,logger:Logger,hashMap: Map[String, String],curTime:String) {

  private var fileOper =  FileOperations

  /**
    * cryptoAlgo provide encrypted and decrypted files
    * @param cipherMode it contains mode information means Algorithm either ENCRYPT_MODE(1) or DECRYPT_MODE(2)
    * @param outputFile it is output file
    */

  def cryptoAlgo(cipherMode:Int,outputFile:String){
    try {


      val digester = MessageDigest.getInstance("SHA-256")
      digester.update(key.getBytes())
      val newKey = digester.digest
      val secretKey = new SecretKeySpec(newKey, "AES")
      val cipher = Cipher.getInstance("AES")

      cipher.init(cipherMode, secretKey)

      val inputStream = new FileInputStream(inputFile)
      val cnt = inputStream.available

      val inputBytes = Array.ofDim[Byte](cnt)


      val outputBytes = cipher.doFinal(inputBytes)

      val outputStream = new FileOutputStream(outputFile)
      outputStream.write(outputBytes)

      inputStream.close()
      outputStream.close()

    } catch {
      //case e: ScriptException => println("Error is " + e.printStackTrace())
      case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While encrypting/decrypting on file, process got fail" +  e.printStackTrace())))
    }

  }
  /**
    * cryptoAlgo provide encrypted and decrypted files
    * @param algo algo type either encrypt or decrypt
    *
    */
  def cryptoFun(algo:String) {

    //val key = "0123456789abcdef0123456789abcdef"
    //val inputFile: File = new File("/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/yellow_tripdata_2018-02.csv")
    //val encryptedFile = "/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/yellow_tripdata_2018-02.csv.enc"
    //val decryptedFile = "/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/decrypted-text.txt"

    val encryptedFile = inputFile + ".enc"
    val decryptedFile = inputFile


    try {

      if(algo=="encrypt") {
        cryptoAlgo(Cipher.ENCRYPT_MODE, encryptedFile)
        println("Sucess")
      } else if (algo=="decrypt"){
        cryptoAlgo(Cipher.DECRYPT_MODE, decryptedFile)
      } }catch {
      //case e: ScriptException => println("Error is " + e.printStackTrace())
      case e:ScriptException => logger.hashMapFunc(Map(("Event Time", curTime),  ("Message", " While encrypting/decrypting on file, process got fail" +  e.printStackTrace())))
    }
  }
}

//object xyz{
//
//  def main(args: Array[String]): Unit = {
//    var ak = CryptographyAlgo("/Users/achand/Documents/Project/BU/ETLSystem/src/main/resource/yellow_tripdata_2018-02.csv","0123456789abcdef0123456789abcdef")
//
//    ak.cryptoFun("encrypt")
//  }

//}

