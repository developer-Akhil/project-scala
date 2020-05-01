import json,os,datetime
from com.rxcorp.daqe.etl.rest.RestApi import *

class NifiProcessorLog:

    def getProvenanceUrl(self, url, provenanceData):
            # postCall(self, elkUrl, indexName, data, id)
        type = "provenance"
        response = None
        r = RestApi.postCallNifi(RestApi, url, provenanceData, type)
        if ((r[0] == "Created") & (r[1] == 201)):
            response = json.loads(r[2])['provenance']['uri']
        else:
            print("While post call process got failed")

        return response

    def getProvenanceData(self, url):
        response=None
        type = "provenance"
        r = RestApi.getCallNifi(RestApi, url,None,None,None,type)
        if ((r[0] == "Created") & (r[1] == 201)):
            response = json.loads(r[2])
        if ((r[0] == "OK") & (r[1] == 200)):

            if not json.loads(r[2])["provenance"]["results"]["provenanceEvents"]:
                print("List is empty")
                exit()

            else:
                response = json.loads(r[2])

        else:
            print("While post call process got failed")

        return response

    def datetimeConverstion(self,timeString):
        dt=datetime.datetime.strptime(str(timeString),"%m/%d/%Y %H:%M:%S.%f UTC")
        newDT=str(dt)[:-3]
        return newDT

    # provenanceUrl = getProvenanceUrl(sel, provenanceData)
    #
    # print(restApi.getCallNifi(restApi, provenanceUrl))

        # curl -i -X POST -H 'Content-Type: application/json' http://localhost:8080/nifi-api/provenance -d '{"provenance":{"request":{"maxResults":1000}}}'
        #
        # curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/nifi-api/provenance/cd6bf484-0170-1000-bd98-0d6cf52cc224

    def nifiLogProducer(self,provenanceDataObj, logMessage, myconverter, elkUrl, elkIndexNameProcessorLogWrite):
        #for i in reversed(readJsonFile(fileName)['provenance']['results']['provenanceEvents']):
        for i in provenanceDataObj['provenance']['results']['provenanceEvents']:
         # for i in provenanceDataObj['provenance']['results']['provenanceEvents']:
            if 'transitUri' in list(i.keys()):
                fileName=None
                for j in i['attributes']:
                    if j['name'] == 'filename':
                        fileName=j['value']

                message={
                    "Component Type": i['componentType'],
                    "Component Name": i['componentName'],
                    "Event Time": NifiProcessorLog.datetimeConverstion(NifiProcessorLog,i['eventTime']),
                    "Filename": fileName,
                    "File Siz": i['fileSize'],
                    "Path": i['transitUri'],
                    "Message": logMessage[i['componentName']],
                    "Source Type": i['eventType'],
                    "Processor Id": i['componentId'],
                    "Process Group Id": i['groupId'],
                    "Flowfile UUID": i['flowFileUuid'],
                    "Connection Id" : i['sourceConnectionIdentifier']}


                if "enc" in str(message["Filename"]):
                    fileName = str(message["Filename"]).replace(".enc", "_N")
                newContent = json.dumps(message, default=myconverter)
                print(newContent)
                response = RestApi.postCallElk(RestApi, elkUrl, elkIndexNameProcessorLogWrite, newContent, str(message["Processor Id"]) + str("_") + fileName)

                print(response[0], response[1], response[2])

                if (response[0] == "Created" and response[1] == 201):
                    response = json.loads(response[2])
                    if (response["result"] == "created"):
                        print("Processor is created successfully")

                elif (response[0] == "OK" and response[1] == 200):
                    print("Process successfully done")

                else:
                    print("while inserting log into elk process got failed")
            else:
                fileName = None
                for j in i['attributes']:
                    if j['name'] == 'filename':
                        fileName=j['value']

                message = {
                    "Component Type": i['componentType'],
                    "Component Name": i['componentName'],
                    "Event Time": NifiProcessorLog.datetimeConverstion(NifiProcessorLog,i['eventTime']),
                    "Filename": fileName,
                    "File Siz": i['fileSize'],
                    "Path": "None",
                    "Message": logMessage[i['componentName']],
                    "Source Type": i['eventType'],
                    "Processor Id": i['componentId'],
                    "Process Group Id": i['groupId'],
                    "Flowfile UUID": i['flowFileUuid'],
                    "Connection Id": i['sourceConnectionIdentifier']}

                newContent = json.dumps(message, default=myconverter)
                if "enc" in str(message["Filename"]):
                    fileName = str(message["Filename"]).replace(".enc", "_N")

                response = RestApi.postCallElk(RestApi, elkUrl, elkIndexNameProcessorLogWrite, newContent, str(message["Processor Id"]) + str("_") + fileName)

                #response = restApi.postCall(elkUrl, indexnameprocessorwrite, newContent,str(message["Processor Id"]))

                print(response[0], response[1], response[2])

                if (response[0] == "Created" and response[1] == 201):
                    response = json.loads(response[2])
                    if (response["result"] == "created"):
                        print("Processor is created successfully")

                elif (response[0] == "OK" and response[1] == 200):
                    print("Process successfully done")

                else:
                    print("while inserting log into elk process got failed")



#{"provenance":{"id":"dd944f42-0170-1000-5429-f8368f5825c5","uri":"http://localhost:8080/nifi-api/provenance/dd944f42-0170-1000-5429-f8368f5825c5","submissionTime":"03/15/2020 15:14:09.283 IST","expiration":"03/15/2020 15:44:09.283 IST","percentCompleted":100,"finished":true,"request":{"searchTerms":{},"maxResults":1000},"results":{"provenanceEvents":[],"total":"0","totalCount":0,"generated":"15:28:04 IST","timeOffset":19800000}}}
