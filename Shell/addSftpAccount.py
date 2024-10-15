#!/usr/bin/env python

import cx_Oracle
import pwd
import string
import random
import os
from getpass import getpass

def generate():
        global username
        username = raw_input("Enter username: ")
        try:
                pwd.getpwnam(username)
                print "Username already exists."
                generate()
        except:
                passwdGenerator()
                return username

def getTimeStamp():
    timeStamp=datetime.datetime.now()
    return timeStamp


def dataConnection(pgId,hostName,accountId,passwd,inPath,outPath,countryCode,tableName):
        try:
		connString="DCA_DATA/DCA_DATA@//cdtsdcpu-scan.rxcorp.com:1521/UDCPAD00"
		dbConnection = cx_Oracle.Connection(connString)
            	cur = dbConnection.cursor()
            	cur.execute("SELECT count(*) FROM " + tableName + " WHERE SFTP_ACCOUNT = '"  + str(accountName) + "' " )
		timeStamp=getTimeStamp()
            	result = cur.fetchone()[0]
            	if result == 0:

                	cur.prepare(" insert into " + tableName + " values ( :pgId,:hostName ,:accountId,:passwd,:inPath,:outPath,countryCode,TO_TIMESTAMP(:timeStamp,'YYYY-MM-DD HH24:MI:SS.FF')) ")
                	cur.execute(None,{'pgId': pgId,'hostName':hostName, 'accountId': accountId,'passwd': passwd,'inPath':inPath,'outPath':outPath ,'countryCode':countryCode,'timeStamp': timeStamp})
                	dbConnection.commit()
		else:
			print("User is already exist " + str(accountName))
		cur.close()
            	dbConnection.close()

        except (IOError ,OSError) as e:
		print("While reading data from database process got failed "+ e)
		exit()

def passwdGenerator():
        global password
        confirm = raw_input("Randomly generate password? (y/n): ")
        if confirm == "y":
                password = ''.join(random.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits) for _ in range(6))
        elif confirm == "n":
                p1 = getpass("Enter password: ")
                p2 = getpass("Confirm password: ")
                if p1 == p2:
                        password = p1
                else:
                        print "Passwords do not match. Try again."
                        passwdGenerator()
        else:
                print "Type y for yes, n for no."
                passwdGenerator()

def addAccount(Username, Password):
	path=str("/data/dcpdev/nifi/sftp/") + str(Username)
        os.system("sudo useradd -d " + path + " -s /usr/libexec/openssh/sftp-server {}".format (Username, Username))
        os.system("sudo chown {}:{} " + path + " ".format(Username,Username))
        os.system("sudo chown root:root " + path )
        os.system("sudo chmod 777 " + path )
        #os.system('echo %s:%s | sudo chpasswd' % (Username, Password))

def main():
	dataConnection("cf16594f-ce61-3fd1-fc07-0ec814fa0794",None,"sftpuser","test@123","/data/dcpdev/nifi/sftp/sftpuser/",None,"IT","DQE_FTP_ACCT")
	exit()
    	generate()
    	addAccount(username, password)
    	print ("User {} has been successfully created with password {}".format(username, password)

if __name__ =='__main__':main()

