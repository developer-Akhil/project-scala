package com.etl.gcp

import java.security.MessageDigest
import java.sql.Timestamp
import java.util.Date

import org.apache.commons.io.FileUtils
import java.io.{BufferedReader, File, FileInputStream, FileReader}
import java.nio.file.{Files, StandardCopyOption}

import com.google.gson.GsonBuilder
import javax.script.ScriptException

object FileOperations {

  /**
    * moveFile function is used to move file from one folder to another folder
    *
    *  @param sourceDirName source directory location
    *  @param destDirName  destination directory location
    *
    */

  def moveFile(sourceDirName:String, destDirName:String): Unit = {

    val sourceDir = FileUtils.getFile(sourceDirName).toPath
    val destDir = FileUtils.getFile(destDirName).toPath

    try {

      Files.move(sourceDir,destDir,StandardCopyOption.REPLACE_EXISTING)
      //sourceDir.renameTo(destDir)
      //FileUtils.moveFileToDirectory(src,tgt,true)

    }
    catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
        sys.exit(1)
    }
  }

  /**
    * isDirectoryExist function is used to check directory is exist or not
    * @param dirName pass directory as input
    */
  def isDirectoryExist(dirName:String): Unit = {

    try{

      val file = new File(dirName)
      if (file.exists()) {
        if (!file.isDirectory) {
          println(s"Directory $dirName doesn't exist")
          sys.exit(1)

        }else{
          println(s"Directory $dirName exist")
        }

      }
    } catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
        sys.exit(1)
    }
  }

  /**
    * isFileExist function is used to check file is exist or not
    * @param filename pass a file and checking file is empty or not
    */
  def isFileExist(filename:String): Unit = {
    try {

      val exists = new java.io.File(filename).exists
      if (exists) {
        println(s"File $filename exist")

      } else {
        println(s"File $filename does not exist")
        sys.exit(1)

      }
    } catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
        sys.exit(1)
    }
  }

  /**
    * isFileEmpty function checks file is empty or not
    * @param dirName directory where we have to check either file is empty or not
    * @param filenameWithPath
    */
  def isFileEmpty(dirName:String, filenameWithPath:String): Unit =  {
    try {
      val br = new BufferedReader(new FileReader(filenameWithPath))

      if (br.readLine() == null) {
        println("Moving file to Error Folder as File is empty")
        moveFile(dirName, filenameWithPath)
        sys.exit(1)
      }
    } catch {
      case e: ScriptException => println("Error is " + e.printStackTrace())
        sys.exit(1)
    }
  }

  /**
    * renameFile function rename the file with new name
    * @param filename it contains file name
    * @param stringName string which need to be appended
    * @param operation it contains either rename or replace
    */

  def renameFile(filename:String,stringName:String,operation:String): String ={
    var newFilename=""
    if (operation=="rename") {
      //newFilename = new File(filename).renameTo(new File(filename + stringName))
      newFilename=""
    } else if(operation == "replace")  {
      newFilename=filename.toString.replace(stringName,"")
    }

    newFilename
  }

  /**
    * calculateChecksum is used to calculate hash value of file
    *
    * @param filename argument contains file name and path to be calculated
    * @param algorithm variable is used to define the algorithm name like MD5/SHA1/Hash/SHA128/SHA256
    *
    *
    */
  def calculateChecksum(filename: String,algorithm:String): String = {
    val inputStream = new FileInputStream(filename)
    val buffer = new Array[Byte](8192)
    val messageDigest = MessageDigest.getInstance(algorithm)
    var numRead = 0

    try{
      do {
        numRead = inputStream.read(buffer)
        if (numRead > 0) messageDigest.update(buffer, 0, numRead)
      } while ( {
        numRead != -1
      })
      inputStream.close()


    } catch {
      case e: Exception => println("While calculating "+ algorithm +  e.printStackTrace())
      //case e: Exception => log.error("While calculating "+ algorithm + "process got failed with given error " + e.printStackTrace())

    }
    messageDigest.digest.map("%02x".format(_)).mkString
  }
  /**
    * getCurrentTimeStamp returns current time stamp
    *
    *
    */
  def getCurrentTimeStamp : Timestamp = {
    //Date object
    val date = new Date()
    //getTime() returns current time in milliseconds
    val time = date.getTime
    //Passed the milliseconds to constructor of Timestamp class
    val ts = new Timestamp(time)
    ts
  }

}
