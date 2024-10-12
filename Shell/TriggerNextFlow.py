#!/usr/local/bin/jython
import os
import sys
sys.path.append('/usr/local/lib/python3.6/site-packages/')
sys.path.append('/usr/lib64/python3.6/json/')

#/Users/achand/Development/nifi/
#/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/site-packages/
#import cx_Oracle
import requests
import json
#import logger as log

#sys.path.append('/Users/achand/Development/nifi/commons-io-2.6.jar')
#sys.path.append('/Users/achand/Development/nifi/nifi-api-1.9.1.jar')
#sys.path.append('/Users/achand/Development/nifi/nifi-scripting-processors-1.9.2.jar')
#sys.path.append('/Users/achand/Development/nifi/jar/zxJDBC.jar')

from org.apache.commons.io import IOUtils
from org.apache.nifi.processor.io import InputStreamCallback
from org.apache.nifi.processors.script import ExecuteScript 
#from com.ziclix.python.sql import zxJDBC


sys.path.append('/Users/achand/Development/nifi/')

class triggerNextFlow(InputStreamCallback):
	def jsonPayload(self,processGroupId, state):
	    	jsonPayloadVal = {
            	"id":processGroupId,
            	"state":state
    			}
    	    	jsonPayloadVal=json.dumps(jsonPayloadVal)
  
    		return jsonPayloadVal

	def runPutCall(self,url,processGroupId, body):
    		url1=  str(url)+str("/flow/process-groups/")+str(processGroupId)
		print(url1)
		headers = {
	        'Content-Type': 'application/json'
    		}
    		r = requests.put(url1,headers=headers, data=body)
    		print(url1)
    			
    		print(r.reason, r.status_code, r.content)
    		return (r.reason, r.status_code, r.content.decode("utf-8"))
#	def dbConnecttion(self,connString,dbUserName,dbPassword,driver,processorGroupName,tableName):
#		processorGroupId=None
#		try:
#			conn=zxJDBC.connect(connString, dbUserName, dbPassword, driver)
#			cur = conn.cursor()
#			cur.execute("SELECT PG_ID FROM " + tableName + " WHERE  UPPER(PROCESSOR_ID) = '"  + str(processorGroupName).upper() + "' " )
#	                processorGroupId = cur.fetchone()[0]
		
#			cur.close()
#    			conn.close()
#		except (IOError, OSError) as e:

#                        print("While reading data from database process got failed " + e)
#                        exit()
#                return processorGroupId
		
	
#	       	processorGroupId=None
#        	try:
#            		dbConnection = cx_Oracle.Connection(connString)
#            		cur = dbConnection.cursor()
#            		cur.execute("SELECT PG_ID FROM " + tableName + " WHERE  UPPER(PROCESSOR_ID) = '"  + str(processorGroupName).upper() + "' " )
#            		processorGroupId = cur.fetchone()[0]
#            		cur.close()
#            		dbConnection.close()

#        	except (IOError, OSError) as e:

#            		print("While reading data from database process got failed " + e)
#            		exit()
#        	return processorGroupId	
	
	def readJsonFile(self,fileName):
   		#with open(os.path.abspath(fileName)) as f:
   		with open(fileName) as f:
        		data = json.load(f)
   	 	return data	
	
flowfile = session.get()
#flowfile="Test"
if(flowfile != None):

	nextFlow=triggerNextFlow()

	propertiesVariable=nextFlow.readJsonFile("/data/dcpdev/nifi/logsystem/NifiLoggingSystem/com/rxcorp/daqe/etl/config/resource/NifiProperites.json")
    	nifiUrl=propertiesVariable["nifi"]["nifiurl"]
    	elkUrl=propertiesVariable["elk"]["elkUrl"]
    	#connString=propertiesVariable["oracle"]["dbjdbcConn"]

	connString=propertiesVariable["oracle"]["dbconnection"]
	dbUserName=propertiesVariable["oracle"]["dbusername"]
    	dbPassword=propertiesVariable["oracle"]["dbpassword"]
    	driver=propertiesVariable["oracle"]["driver"]

    	pgDetailsTableName = propertiesVariable["oracle"]["pgdetailstablename"]
    	#processorDetailsTableName = propertiesVariable["oracle"]["processortablename"]

    	#processGroupId='bee62419-016f-1000-f131-5c589af8faff'
    	#processGroupId = processGroupId.getValue()
    	#print(processGroupId)
    	url='http://localhost:8080/nifi-api/flow/process-groups/'
    	state='RUNNING'
    	# filename=flowfile.getAttribute('filename')
    	# path=flowfile.getAttribute('path')
    	# print("path is " + str(path))
    	# path=flowfile.getAttribute('absolute.path')
    	# print("absolute path is " + str(path))
    	# print("Hello Put test ")

	#processGroupId= flowfile.getAttribute('processGroupId')
	processGroupID= processGroupId.getValue()
	print(processGroupID)
	#processGroupId= nextFlow.dbConnecttion(connString,dbUserName,dbPassword,driver,processGroupName,pgDetailsTableName)
    	jsonPayLoad= nextFlow.jsonPayload(processGroupID,state)
    	responseVal= nextFlow.runPutCall(nifiUrl,processGroupId,jsonPayLoad)

    	
    	if responseVal[0] == "OK" and responseVal[1] == 200:
        	print("Process executed successfully")
    	else:
        	print("Process got failed while trigger the application")
        #	exit()
    	#changeMode(filename,897)
    	session.transfer(flowfile, ExecuteScript.REL_SUCCESS)