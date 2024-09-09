import cx_Oracle


class nifiDBPGDetails:
    def __init__(self,connString):
        self.connString = connString
        # self.dbConnection = cx_Oracle.Connection(self.connString)
        # self.cur = self.dbConnection.cursor()

    def pgDetails(self ,tableName ,pgId ,pgName ,timeStamp,status):
        try:

            dbConnection = cx_Oracle.Connection(self.connString)
            cur = dbConnection.cursor()
            #2020-02-02 08:39:39.211
            cur.prepare(" insert into " + tableName + " values ( :pgId ,:pgName,TO_TIMESTAMP(:timeStamp,'YYYY-MM-DD HH24:MI:SS.FF'),:status) ")
            cur.execute(None, {'pgId': pgId,'pgName': pgName,'timeStamp':timeStamp,'status':status})

            dbConnection.commit()
            cur.close()
            dbConnection.close()
        except (IOError ,OSError) as e:

            print("While reading data from database process got failed "+ e)
            exit()

    def processorDetails(self,tableName,processorId,url,pgId,name,processorType,processorProperty,timeStamp,lastModifiedTime):
        try:
            dbConnection = cx_Oracle.Connection(self.connString)
            cur = dbConnection.cursor()
            cur.execute("SELECT count(*) FROM " + tableName + " WHERE  PROCESSOR_ID = '"  + str(processorId) + "' " )

            result = cur.fetchone()[0]
            if result == 0:

                cur.prepare(" insert into " + tableName + " values ( :processorId,:uri ,:pgId,:name,:processorType,:processorProperty,TO_TIMESTAMP(:timeStamp,'YYYY-MM-DD HH24:MI:SS.FF'),:lastModifiyTime ) ")
                cur.execute(None, {'processorId': processorId,'uri':url, 'pgId': pgId,'name': name,'processorType':processorType,'processorProperty':processorProperty ,'timeStamp': timeStamp,'lastModifiyTime':lastModifiedTime})
                dbConnection.commit()
            else:
                cur.execute(str("SELECT count(*) FROM " + tableName + " WHERE  PROCESSOR_ID = '" + str(processorId) + "' AND COMPONENT_NAME <> '" + str(name) + " '"))
                result = cur.fetchone()
                if result == 0:
                    cur.prepare(" UPDATE " + tableName + " SET COMPONENT_NAME = :name,PROCESSOR_PROPERTIES=:processorProperty,LAST_MODIFIED_TS=TO_TIMESTAMP(:timeStamp,'YYYY-MM-DD HH24:MI:SS.FF')) WHERE"
                                                     " PROCESSOR_ID =:processorId " )
                    cur.execute(None, { 'name': name, 'processorProperty':processorProperty,'timeStamp': timeStamp,'processorId': processorId})
                    dbConnection.commit()

            cur.close()
            dbConnection.close()
        except (IOError ,OSError) as e:

            print("While reading data from database process got failed "+ e)
            exit()

    def ftpAccountDetails(self, tableName, processorId, pgId, componentName, actualComponentName,
                                              sftpHost, sftpAccount, sftpPassword, remotePath,countryCode, timeStamp,
                                              lastModifiedTime):

        try:
            dbConnection = cx_Oracle.Connection(self.connString)
            cur = dbConnection.cursor()
            cur.execute("SELECT COUNT(*) FROM " + tableName + " WHERE  PROCESSOR_ID = '" + str(processorId) + "' AND  PG_ID = '" + str(pgId ) +
                        "' AND SFTP_ACCOUNT= '" + str(sftpAccount) + " '")

            print('processorId : ' + str(processorId), 'pgId: ' + str(pgId), 'componentName: ' + str(componentName),'actualComponentName : ' + str(actualComponentName),
                                   'sftpHost: ' + str(sftpHost),'sftpAccount: ' + str(sftpAccount),'sftpPassword: ' + str(sftpPassword),'remotePath: ' + str(remotePath),
                  'countryCode: ' + str(countryCode),'timeStamp: ' + str(timeStamp),'lastModifiyTime: ' + str(lastModifiedTime))
            result = cur.fetchone()[0]
            if result == 0:
                cur.prepare(
                    " insert into " + tableName + " values ( :processorId ,:pgId,:componentName,:actualComponentName,:sftpHost,:sftpAccount,:sftpPassword,:remotePath,:countryCode,"
                                                  "TO_TIMESTAMP(:timeStamp,'YYYY-MM-DD HH24:MI:SS.FF'),:lastModifiyTime ) ")

                cur.execute(None, {'processorId': processorId, 'pgId': pgId, 'componentName': componentName,'actualComponentName': actualComponentName,
                                   'sftpHost': sftpHost,'sftpAccount':sftpAccount,'sftpPassword': sftpPassword,'remotePath':remotePath,'countryCode':countryCode,'timeStamp':timeStamp,
                                   'lastModifiyTime': lastModifiedTime})
                dbConnection.commit()

            else:
                cur.execute("SELECT COUNT(*) FROM " + tableName + " WHERE  PROCESSOR_ID = '" + str(processorId) + "' AND  PG_ID = '"
                            + str(pgId) + "' AND SFTP_ACCOUNT= '" + str(sftpAccount) + " ' AND REMOTE_PATH <> '" + str(remotePath) + " ' AND COUNTRY_CODE<> '" +str(countryCode)
                            + " ' AND SFTP_HOST <> '" +str(sftpHost))
                result = cur.fetchone()[0]
                if result == 1:
                    cur.prepare(
                        " UPDATE " + tableName + " SET REMOTE_PATH = :remotePath,COUNTRY_CODE=:countryCode,SFTP_HOST=sftpHost:,"
                                                 "LAST_MODIFIED_TS=TO_TIMESTAMP(:timeStamp,'YYYY-MM-DD HH24:MI:SS.FF')) WHERE"
                                                 " PROCESSOR_ID =:processorId AND PG_ID=:pgId AND SFTP_ACCOUNT=:sftpAccount")
                    cur.execute(None, {'remotePath': remotePath, 'countryCode': countryCode,'sftpHost':sftpHost, 'timeStamp': timeStamp,
                                       'processorId': processorId,'pgId':pgId,'sftpAccount':sftpAccount})
                    dbConnection.commit()

            cur.close()
            dbConnection.close()

        except (IOError, OSError) as e:

            print("While reading data from database process got failed " + e)
            exit()