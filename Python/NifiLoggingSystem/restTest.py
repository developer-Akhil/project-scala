import json,os


def readJsonFile(fileName):
    with open(os.path.abspath(fileName)) as f:
        data = json.load(f)
    return data

j=readJsonFile("/Users/achand/Documents/Project/BU/NifiLoggingSystem/com/rxcorp/daqe/etl/config/resource/jsontest.json")


# print(j["id"])
# print(j["bulletins"][0]["bulletin"]["level"],j["bulletins"][0]["bulletin"]["message"],j["bulletins"][0]["bulletin"]["timestamp"])
# print(j["bulletins"][1]["bulletin"]["level"],j["bulletins"][0]["bulletin"]["message"],j["bulletins"][0]["bulletin"]["timestamp"])
# print(j["bulletins"][2]["bulletin"]["level"],j["bulletins"][0]["bulletin"]["message"],j["bulletins"][0]["bulletin"]["timestamp"])
# print(j["bulletins"][3]["bulletin"]["level"],j["bulletins"][0]["bulletin"]["message"],j["bulletins"][0]["bulletin"]["timestamp"])

# print(json.dumps(j["component"]["config"]["properties"]))
#
# exit()
for i in range(1,len(j["bulletins"])):

    message= {"Processor Group Id":j["status"]["groupId"],
              "Processor Id":j["status"]["id"],
              "Customized Component Name":j["status"]["name"],
              "Component Name":j["status"]["aggregateSnapshot"]["type"],
              "Status":j["status"]["runStatus"],
              "Last Refreshed Time":j["status"]["statsLastRefreshed"],
              "Bytes Read":j["status"]["aggregateSnapshot"]["bytesRead"],
              "Bytes Written":j["status"]["aggregateSnapshot"]["bytesWritten"],
              "Read":j["status"]["aggregateSnapshot"]["read"],
              "Written":j["status"]["aggregateSnapshot"]["written"],
              "Flow Files In":j["status"]["aggregateSnapshot"]["flowFilesIn"],
              "Flow Files Out":j["status"]["aggregateSnapshot"]["flowFilesOut"],
              "Task Duration":j["status"]["aggregateSnapshot"]["tasksDuration"],
              "Job Id":j["bulletins"][i]["bulletin"]["id"],
              "Log Type" : j["bulletins"][i]["bulletin"]["level"],
              "Message ":  j["bulletins"][i]["bulletin"]["message"],
              "TimeStamp" :j["bulletins"][i]["bulletin"]["timestamp"],
              "Processor Properties" : json.dumps(j["component"]["config"]["properties"])}
              #"Processor Properties" : j["component"]["config"]["properties"]}
    #newContent = json.dumps(message, default=myconverter)
    newContent = json.dumps(message)
    print(newContent)

# print(j["status"])
# print(j["status"]["groupId"])
# print(j["status"]["id"])
# print(j["status"]["name"])
# print(j["status"]["aggregateSnapshot"]["type"])
# print(j["status"]["runStatus"])
# print(j["status"]["statsLastRefreshed"])
# print(j["status"]["aggregateSnapshot"]["runStatus"])
# print(j["status"]["aggregateSnapshot"]["executionNode"])
# print(j["status"]["aggregateSnapshot"]["bytesRead"])
# print(j["status"]["aggregateSnapshot"]["bytesWritten"])
# print(j["status"]["aggregateSnapshot"]["read"])
# print(j["status"]["aggregateSnapshot"]["written"])
# print(j["status"]["aggregateSnapshot"]["flowFilesIn"])
# print(j["status"]["aggregateSnapshot"]["flowFilesOut"])
# print(j["status"]["aggregateSnapshot"]["tasksDuration"])




# print(j["processors"][0]["uri"])
#
# print(j["processors"][0]["id"])
#
# print(j["processors"][0]["component"]["parentGroupId"])
#
# print(j["processors"][0]["component"]["name"])
#
# print(j["processors"][0]["component"]["type"])
# #print(j["processors"][0]["name"])
#
# print(j["processors"][0])
#
# exit()
# print(j["processors"][2])
# print(j["processors"][3])
# print(j["processors"][4])
# print(j["processors"][5])
# print(j["processors"][6])
# print(j["processors"][7])
# print(j["processors"][8])
# print(j["processors"][9])
# print(j["processors"][10])
# print(j["processors"][11])

