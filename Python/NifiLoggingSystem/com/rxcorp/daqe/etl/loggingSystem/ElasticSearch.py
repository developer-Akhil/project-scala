from elasticsearch import Elasticsearch,helpers
import json,re as r

es=None
try:
    es = Elasticsearch(["bdfelasticdev.rxcorp.com:80"])
except IOError as io:
    print("error " + str(io))



# dateTimeObj = datetime.now()
#
# print(dateTimeObj)


#es = Elasticsearch([{"host":"bdfelasticdev.rxcorp.com","port":9200}])

indexName = "dqe_rx_pg_audit_dev_write"

#indexName="it9_sou_uat_dcp_audit_read"
index_mapping =  {
        "template": "*ca_taunus_dcp_data_proc_stas_intg*",
        "settings": {
            "index": {
                "number_of_shards": "3",
                "number_of_replicas": "1"
            }
        },
        "mappings": {
            "logs": {
                "properties": {
                    "StackMessage": {
                        "type": "keyword"
                    },
                    "componentMethod": {
                        "type": "keyword"
                    },
                    "componentName": {
                        "type": "keyword"
                    },
                    "eventTime": {
                        "type": "date",
                        "format": "yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "fieldName": {
                        "type": "keyword"
                    },
                    "fileCreationDate": {
                        "type": "date",
                        "format": "ddMMyyyy"
                    },
                    "filename": {
                        "type": "keyword"
                    },
                    "logType": {
                        "type": "keyword"
                    },
                    "message": {
                        "type": "text"
                    },
                    "processId": {
                        "type": "keyword"
                    },
                    "processStep": {
                        "type": "keyword"
                    },
                    "stackMessage": {
                        "type": "keyword"
                    },
                    "stackTrace": {
                        "type": "keyword",
                        "index": False
                    },
                    "warning": {
                        "type": "keyword"
                    },
					"cycleDate": {
					"type":"integer"}
                }
            }
        },
        "aliases": {
            "ca_taunus_dcp_data_proc_stas_intg_write": {},
            "ca_taunus_dcp_data_proc_stas_intg_read": {}
        }
    }

import datetime

# content={"eventTime":datetime.datetime.now(), "logType":"ERROR", "loggerName" :"com.rxcorp.ireland_lrx.logging.KafkaLogger",
#          "stackMessage" : "[Cloudera][JDBC](10500) Invalid operation for forward only ResultSets.", "fileName" : "datatypes.csv",
#          "stackTrace" : "com.cloudera.hiveserver2.exceptions.ExceptionConverter.toSQLException(Unknown Source)",
#          "message" : "While getting the count from table process got failed,please check the stackTrace for more details",
#          "processID" : "d5655fef-2fe2-4789-bcc1-27e2098bb529", "cycleDate" : "20180101"}

content={
	"Processor Group": "0b557330-24d4-39f3-6a0b-f472e833c641",
	"PipeLine Name": "Italy Pipeline testing",
	"Current TimeStamp": "2020-01-31 13:02:52.935984"[:-3],
	"Last Refreshed Time": "13:02:52 IST",
	"Flow Files In": 0,
	"Queued": "3 (66 bytes)",
	"Total Queue Count": "3",
	"Queued Size": "66 bytes",
	"File Processed": 0,
	"flowFilesTransferred": 0,
	"Running Count": 0,
	"StoppedCount": 12,
	"Invalid Count": 0,
	"Disabled Count": 0
}

# content={"eventTime":"2018-07-25 15:38:05.391", "logType":"ERROR", "loggerName" :"com.rxcorp.ireland_lrx.logging.KafkaLogger",
#          "stackMessage" : "[Cloudera][JDBC](10500) Invalid operation for forward only ResultSets.", "fileName" : "datatypes.csv",
#          "stackTrace" : "com.cloudera.hiveserver2.exceptions.ExceptionConverter.toSQLException(Unknown Source)",
#          "message" : "While getting the count from table process got failed,please check the stackTrace for more details",
#          "processID" : "d5655fef-2fe2-4789-bcc1-27e2098bb529", "cycleDate" : "20180101"}

def myconverter(o):
    if isinstance(o, datetime.datetime):
        return o.__str__()

newContent=json.dumps(content, default = myconverter)
print(newContent)
#exit()
#print(json.dumps(d, default=myconverter))

#print(type(json.dumps(content,indent=4, sort_keys=True, default=str)))
#newCon=json.dumps(content,indent=4, sort_keys=True, default=str)
action={
    "_index" : indexName,
    "_type" : "test-type",
    "_id" : 242,
    "_source" : newContent
    }

helpers.bulk(es, newContent, index=indexName,doc_type='test-type', request_timeout=200)

#helpers.bulk(es,action,chunk_size=1000, request_timeout=200)

#es.bulk(newContent ,indexName, doc_type="test-type",request_timeout=30)

exit()
a= es.search(index=indexName,  size=1000)

print(a)

exit()
#es.get()
if not es.indices.exists(indexName):
    res = es.indices.create(index=indexName, body=index_mapping)
    print (res)

else:

    es.index(index=indexName , doc_type="test-type", id=42, body=json.dumps(content))

