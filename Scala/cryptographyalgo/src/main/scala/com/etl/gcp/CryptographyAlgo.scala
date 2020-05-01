package com.etl.gcp

import javax.script.ScriptException

case class CryptographyAlgo(privateKeyPath:String,publicKeyPath:String,logger:Logger,hashMap: Map[String, String],curTime:String) {

  private var fileOper =  FileOperations


//  def encrypt(inputFile:String,passwd:String): Unit ={
//    val encryptor = new BCPGPEncryptor
//    try {
//      encryptor.setArmored(false)
//      encryptor.setCheckIntegrity(true)
//
//      encryptor.setPublicKeyFilePath(publicKeyPath)
//      encryptor.setSigning(true)
//      encryptor.setSigningPrivateKeyFilePath(privateKeyPath)
//      encryptor.setSigningPrivateKeyPassword(passwd)
//      //encryptor.encryptFile("./test.txt", "./test.txt.signed.enc")
//      val encryptedFile=fileOper.renameFile(inputFile,".enc","rename")
//      tryencryptor.encryptFile(inputFile, encryptedFile)
//    }
//    catch {
//      case e:ScriptException => println(hashMap("Message") +  Map(("Event Time", curTime),  ("Message", " While encrypting file process got fail :" +  e.printStackTrace())))
//    }
//
//  }
//
//
//  def decrypt(encyptedFile:String,passwd:String): Unit ={
//    val decryptor = new BCPGPDecryptor
//    try {
//      decryptor.setPrivateKeyFilePath(privateKeyPath)
//      decryptor.setPassword(passwd)
//      decryptor.setSigned(true)
//      decryptor.setSigningPublicKeyFilePath(publicKeyPath)
//      val decryptedFile=fileOper.renameFile(inputFile,".enc","replace")
//      // this file is encrypted with weili's public key and signed using wahaha's private key
//      decryptor.decryptFile(encyptedFile, decryptedFile)
//    } catch {
//      //case e: ScriptException => println("Error is " + e.printStackTrace())
//      case e:ScriptException => println(hashMap("Message") +  Map(("Event Time", curTime),  ("Message", " While decrypting file process got fail :" +  e.printStackTrace())))
//    }
//  }

}
