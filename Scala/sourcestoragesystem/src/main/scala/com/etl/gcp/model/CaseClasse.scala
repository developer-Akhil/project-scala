package com.etl.gcp.model

case class consumeClass(coRelationId:String,
                        filePath:String,
                        fileName:String,
                        OutFolder:String,
                        invalidFolderPath:String,
                        lookupKey:String)

case class DeDupOnfileContainBody(msg: String)

case class DeDupOnfileContainResponse[T: io.circe.Encoder](coRelationId:String,
                                                           filePath:String,
                                                           fileName:String,
                                                           folderName:String,
                                                           folderPath:String,
                                                             lookupKey:String,
                                                           body: T)
