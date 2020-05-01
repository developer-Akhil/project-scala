import json
class NifiProcessGroup:

    def getPGIds(jsonPayload):
        pgID=[]
        try:
            if(jsonPayload[0] == "OK" and jsonPayload[1]==200):
                for i in json.loads(jsonPayload[2])["searchResultsDTO"]["processorResults"]:
                    if i["groupId"] not in pgID:
                        pgID.append(i["groupId"])
        except Exception as e:
            print("Error is " + str(e))

        return pgID


    def getPGDetails(jsonPayload,timeStamp):
        message=None
        #timeStamp = datetime.datetime.now()
        try:
            if(jsonPayload[0] == "OK" and jsonPayload[1]==200):
                newJsonPayload=json.loads(jsonPayload[2])
                message={"Processor Group Id":json.dumps(newJsonPayload["status"]["id"]).replace("\"",""),
                "Processor Group Name":json.dumps(newJsonPayload["status"]["name"]).replace("\"",""),
                "Current Timestamp":  timeStamp,
                "Last Refreshed Time":json.dumps(newJsonPayload["status"]["statsLastRefreshed"]).replace("\"",""),
                "Flow Files In":json.loads(json.dumps(newJsonPayload["status"]["aggregateSnapshot"]))["flowFilesIn"],
                "Queued":json.loads(json.dumps(newJsonPayload["status"]["aggregateSnapshot"]))["queued"],
                "Total Queue Count":json.loads(json.dumps(newJsonPayload["status"]["aggregateSnapshot"]))["queuedCount"],
                "Queued Size":json.loads(json.dumps(newJsonPayload["status"]["aggregateSnapshot"]))["queuedSize"],
                "File Processed":json.loads(json.dumps(newJsonPayload["status"]["aggregateSnapshot"]))["flowFilesOut"],
                "Flow Files Transferred":json.loads(json.dumps(newJsonPayload["status"]["aggregateSnapshot"]))["flowFilesTransferred"],
                "Running Count":json.loads(json.dumps(newJsonPayload["runningCount"])),
                "Stopped Count":json.loads(json.dumps(newJsonPayload["stoppedCount"])),
                "Invalid Count":json.loads(json.dumps(newJsonPayload["invalidCount"])),
                "Disabled Count":json.loads(json.dumps(newJsonPayload["disabledCount"]))}

        except Exception as e:
            print("Error is " + str(e))
            exit()
        return message
