package com.etl.gcp

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

case class DumpToDataLake() {


  def dumptoDatalake(srcFilePath:String,destFilePath:String) {

    val hadoopConf = new Configuration()
    val hdfs = FileSystem.get(hadoopConf)


    val srcPath = new Path(srcFilePath)

    val destPath = new Path(destFilePath)


    hdfs.copyFromLocalFile(srcPath, destPath)
  }

}
