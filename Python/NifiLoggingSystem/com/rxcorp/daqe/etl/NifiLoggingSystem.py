import json
import requests
import sys
sys.path.append('/data/dcpdev/nifi/logsystem/NifiLoggingSystem/')
from com.rxcorp.daqe.etl.nifiProcesses.NifiProcessGroup import *
from com.rxcorp.daqe.etl.nifiProcesses.NifiProcessorLog import *
from com.rxcorp.daqe.etl.nifiProcesses.NifiProcessor import *
from com.rxcorp.daqe.etl.loggingSystem.LogProducer import *
from com.rxcorp.daqe.etl.dbProcessing.nifiDBPGDetails import *
from com.rxcorp.daqe.etl.rest.RestApi import *
import os,datetime

# url="http://localhost:8080/nifi-api"
# brokerUri = "kafka-0-broker.dev-dcp-kafka02.cdt.dcos:9625,kafka-1-broker.dev-dcp-kafka02.cdt.dcos:96x25, kafka-2-broker.dev-dcp-kafka02.cdt.dcos:9625,kafka-3-broker.dev-dcp-kafka02.cdt.dcos:9625, kafka-4-broker.dev-dcp-kafka02.cdt.dcos:9625"
# topic = "devl-dcp-catest-evt"
# indexName="dqe_rx_pg_audit_dev_write"
# elkUrl="http://bdfelasticdev.rxcorp.com/"

#logProducer=LogProducer(topic,brokerUri)

def readJsonFile(fileName):
    with open(os.path.abspath(fileName)) as f:
        data = json.load(f)
    return data


def getTimeStamp():
    timeStamp=datetime.datetime.now()
    return timeStamp



def myconverter(o):
    if isinstance(o, datetime.datetime):
        return o.__str__()


if __name__ == "__main__":
    #propertiesVariable=readJsonFile("NifiLoggingSystem/com/rxcorp/daqe/etl/config/resource/NifiProperites.json")
    propertiesVariable=readJsonFile("/data/dcpdev/nifi/logsystem/NifiLoggingSystem/com/rxcorp/daqe/etl/config/resource/NifiProperites.json")
    nifiUrl=propertiesVariable["nifi"]["nifiurl"]
    elkUrl=propertiesVariable["elk"]["elkUrl"]
    elkIndexNamePGWrite=propertiesVariable["elk"]["indexnamepgwrite"]
    elkIndexNameProcessorWrite = propertiesVariable["elk"]["indexnameprocessorwrite"]
    elkIndexNameProcessorLogWrite = propertiesVariable["elk"]["indexnameprocessorlogwrite"]
    connString=propertiesVariable["oracle"]["dbjdbcConn"]
    pgDetailsTableName = propertiesVariable["oracle"]["pgdetailstablename"]
    processorDetailsTableName = propertiesVariable["oracle"]["processortablename"]
    ftpAccountDetailsTable = propertiesVariable["oracle"]["ftpaccountdetails"]
    logMessage=propertiesVariable["logmessage"]
    provenanceJsonData=propertiesVariable["provenance"]["provenancedata"]
    restApi = RestApi
    nifiProcessGroup = NifiProcessGroup
    fullPropertiesJsonPayLoad=restApi.getCallNifi(restApi,nifiUrl,None,None,None,None)

    nifiDBPGDetail=nifiDBPGDetails(connString)
    nifiProcessor = NifiProcessor
    nifiProcessorLog = NifiProcessorLog

    #making entry related to configuration and processor into db and elk respectively
    for i in nifiProcessGroup.getPGIds(fullPropertiesJsonPayLoad):
        fullPropertiesJsonPayLoad=restApi.getCallNifi(restApi,nifiUrl,i,None,None,None)

        timeStamp=str(getTimeStamp())[:-3]
        message=nifiProcessGroup.getPGDetails(fullPropertiesJsonPayLoad, timeStamp)
        newContent = json.dumps(message, default=myconverter)
        response=restApi.postCallElk(restApi,elkUrl, elkIndexNamePGWrite, newContent, message["Processor Group Id"])

        if(response[0] == "Created" and response[1] == 201 ):
            response = json.loads(response[2])
            if (response["result"] == "created"):
                nifiDBPGDetail.pgDetails(pgDetailsTableName ,message["Processor Group Id"] ,message["Processor Group Name"] ,timeStamp,"ACTIVE")

        elif (response[0]== "OK" and response[1] == 200):
            print("Process successfully done")

        else:
            print("while inserting log into elk process got failed")

        response=restApi.getCallNifi(restApi,nifiUrl,i, "processors",None,None)

        #making entry related to configuration and processor into db and elk respectively
        processorDetails=nifiProcessor.getProcessDetails(nifiProcessor, response, timeStamp, processorDetailsTableName, nifiDBPGDetail, restApi, elkUrl, elkIndexNameProcessorWrite,logMessage,myconverter,ftpAccountDetailsTable,nifiUrl)

    provenanceUrl = nifiProcessorLog.getProvenanceUrl(nifiProcessorLog, nifiUrl, provenanceJsonData)

    provenanceDataObj = nifiProcessorLog.getProvenanceData(nifiProcessorLog, provenanceUrl)
    print("provenanceDataObj" + str(provenanceDataObj))

    nifiProcessorLog.nifiLogProducer(nifiProcessorLog, provenanceDataObj, logMessage, myconverter, elkUrl,elkIndexNameProcessorLogWrite)
    response=restApi.deleteCallNifi(nifiProcessorLog,provenanceUrl)

    if (response[0] == "OK" and response[1] == 200):
        print("Data provenance release successfully")
        #print(restApi.deleteCallNifi(restApi, provenanceUrl))
    else:
        print("While doing deleting")
    exit()

