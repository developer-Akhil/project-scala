import json,os


class NifiProcessor:

    def getProcessDetails(self,response,timeStamp,processorDetailsTableName,nifiDBPGDetail,restApi,elkUrl,elkIndexNameProcessorWrite,logMessage,myconverter,
                          ftpAccountDetailsTable,nifiUrl):

        if (response[0]== "OK" and response[1] == 200):
            newJsonPayload = json.loads(response[2])

            for i in newJsonPayload["processors"]:
                nifiDBPGDetail.processorDetails(processorDetailsTableName, i["id"],i["uri"], i["component"]["parentGroupId"], i["component"]["name"],
                                                i["component"]["type"],json.dumps(i["component"]["config"]["properties"]),timeStamp,None)

                response=restApi.getCallNifi(restApi,nifiUrl,None,None,i["uri"],None)

                if (response[0] == "OK" and response[1] == 200):
                    jsonPayload=json.loads(response[2])
                    message = None

                    if(len(jsonPayload["bulletins"])!=0):
                        for i in range(1, len(jsonPayload["bulletins"])):
                            message = {"Process Group Id": jsonPayload["status"]["groupId"],
                                   "Processor Id": jsonPayload["status"]["id"],
                                   "Customized Component Name": jsonPayload["status"]["name"],
                                   "Component Name": jsonPayload["status"]["aggregateSnapshot"]["type"],
                                   "Status": jsonPayload["status"]["runStatus"],
                                   "Last Refreshed Time": jsonPayload["status"]["statsLastRefreshed"],
                                   "Bytes Read": jsonPayload["status"]["aggregateSnapshot"]["bytesRead"],
                                   "Bytes Written": jsonPayload["status"]["aggregateSnapshot"]["bytesWritten"],
                                   "Read": jsonPayload["status"]["aggregateSnapshot"]["read"],
                                   "Written": jsonPayload["status"]["aggregateSnapshot"]["written"],
                                   "Flow Files In": jsonPayload["status"]["aggregateSnapshot"]["flowFilesIn"],
                                   "Flow Files Out": jsonPayload["status"]["aggregateSnapshot"]["flowFilesOut"],
                                   "Task Duration": jsonPayload["status"]["aggregateSnapshot"]["tasksDuration"],
                                   "Job Id": jsonPayload["bulletins"][i]["bulletin"]["id"],
                                   "Log Type": jsonPayload["bulletins"][i]["bulletin"]["level"],
                                   "Message": jsonPayload["bulletins"][i]["bulletin"]["message"],
                                   #"TimeStamp": jsonPayload["bulletins"][i]["bulletin"]["timestamp"],
                                   "Timestamp": timeStamp,
                                   "Processor Properties": json.dumps(jsonPayload["component"]["config"]["properties"])}
                    else:
                        print(jsonPayload["component"]["name"])

                        description=logMessage[jsonPayload["component"]["name"]]
                        type=jsonPayload["status"]["aggregateSnapshot"]["type"]
                        if (type == "GetSFTP") | (type  == "PutSFTP") | (type == "FetchSFTP") | (type == "ListSFTP"):

                            processorProperties=jsonPayload["component"]["config"]["properties"]
                            # print(processorProperties["Hostname"])
                            # exit()
                            print(processorProperties)
                            
                            nifiDBPGDetail.ftpAccountDetails(ftpAccountDetailsTable,jsonPayload["status"]["id"],jsonPayload["status"]["groupId"],
                                                            jsonPayload["status"]["name"], jsonPayload["status"]["aggregateSnapshot"]["type"],
                                                            processorProperties["Hostname"],processorProperties["Username"],processorProperties["Password"],
                                                            processorProperties["Remote Path"],None,timeStamp, None)

                        message = {"Process Group Id": jsonPayload["status"]["groupId"],
                                    "Processor Id": jsonPayload["status"]["id"],
                                    "Customized Component Name": jsonPayload["status"]["name"],
                                    "Component Name": jsonPayload["status"]["aggregateSnapshot"]["type"],
                                    "Status": jsonPayload["status"]["runStatus"],
                                    "Last Refreshed Time": jsonPayload["status"]["statsLastRefreshed"],
                                    "Bytes Read": jsonPayload["status"]["aggregateSnapshot"]["bytesRead"],
                                    "Bytes Written": jsonPayload["status"]["aggregateSnapshot"]["bytesWritten"],
                                    "Read": jsonPayload["status"]["aggregateSnapshot"]["read"],
                                    "Written": jsonPayload["status"]["aggregateSnapshot"]["written"],
                                    "Flow Files In": jsonPayload["status"]["aggregateSnapshot"]["flowFilesIn"],
                                    "Flow Files Out": jsonPayload["status"]["aggregateSnapshot"]["flowFilesOut"],
                                    "Task Duration": jsonPayload["status"]["aggregateSnapshot"]["tasksDuration"],
                                    "Job Id": 1,
                                    "Log Type": "INFO",
                                    "Message": description,
                                       # "TimeStamp": jsonPayload["bulletins"][i]["bulletin"]["timestamp"],
                                    "Timestamp": timeStamp,
                                    "Processor Properties": json.dumps(jsonPayload["component"]["config"]["properties"])}

                    newContent = json.dumps(message, default=myconverter)
                    print(newContent)

                    #postCall(self, elkUrl, indexName, data, id)

                    response = restApi.postCallElk(restApi,elkUrl, elkIndexNameProcessorWrite, newContent, message["Processor Id"])

                    print(response[0],response[1],response[2])

                    if (response[0] == "Created" and response[1] == 201):
                        response = json.loads(response[2])
                        if (response["result"] == "created"):
                            print("Processor is created successfully")

                    elif (response[0] == "OK" and response[1] == 200):
                        print("Process successfully done")

                    else:
                        print("while inserting log into elk process got failed")


                        # "Processor Properties" : j["component"]["config"]["properties"]}
                        # newContent = json.dumps(message, default=myconverter)

                    #id=str(jsonPayload["status"]["id"]) + str("-") + str(jsonPayload["bulletins"][i]["bulletin"]["id"])

                else:
                    print("while inserting log into elk process got failed")
                    exit()

        else:
            print("while inserting log into elk process got failed")
            exit()



