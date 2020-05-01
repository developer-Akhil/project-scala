import requests,json


class RestApi:

    def getCallNifi(self, url,processGroupId=None, type=None, uri=None,provenanceType=None):
        r=None
        try:
            headers = {
                'Content-Type': 'application/json',
            }
            if processGroupId == None and type == None and uri == None and provenanceType==None:
                newUrl=url+'/flow/search-results'
                print(newUrl)
                r = requests.get(newUrl,headers=headers)
                #r = requests.get('http://localhost:8080/nifi-api/flow/search-results', headers=headers)
            elif processGroupId != None and type == None and uri==None and provenanceType==None:
                newUrl= url +'/process-groups/' + processGroupId
                r = requests.get(newUrl, headers=headers)
            elif processGroupId != None and type != None and uri==None and provenanceType==None:
                newUrl = url + '/process-groups/' + processGroupId + "/" + type
                r = requests.get(newUrl, headers=headers)
            elif processGroupId == None and type == None and uri != None and provenanceType==None:
                #newUrl = self.url + '/processors/' + uri
                r = requests.get(uri, headers=headers)
            elif processGroupId == None and type == None and uri == None and provenanceType!=None:
                r = requests.get(url, headers=headers)

        except Exception as e:
            print("Error is " +str(e))

        return (r.reason, r.status_code, r.content)

    # def getCallNifi(self, url):
    #     r=None
    #     try:
    #         headers = {
    #             'Content-Type': 'application/json',
    #         }
    #         r = requests.get(url,headers=headers)
    #     except Exception as e:
    #         print("Error is " +str(e))
    #     return (r.reason, r.status_code, r.content)

    def postCallElk(self, url, indexName, data, value):
        r=None

        headers = {
            'Content-Type': 'application/json',
        }

        newElkUrl = url + indexName + "/doc/" + value
        print("data" + data)

        try:
            r = requests.post(newElkUrl, headers=headers, data=data)
            print(r.reason, r.status_code, r.content)
        except Exception as e:
            print("Error is " + str(e))

        return (r.reason, r.status_code, r.content)

    def postCallNifi(self, url, data, value):
        r = None

        headers = {
            'Content-Type': 'application/json',
        }

        newUrl = url + "/" + value
        data = json.dumps(data)

        try:
            r = requests.post(newUrl, headers=headers, data=data)
        except Exception as e:
            print("Error is " + str(e))

        return (r.reason, r.status_code, r.content)
        #response = requests.post('http://bdfelasticdev.rxcorp.com/dqe_rx_pg_audit_dev_write/doc', headers=headers)

    def deleteCallNifi(self,url):
        r = None
        try:
            headers = {
                'Content-Type': 'application/json',
            }
            r = requests.delete(url, headers=headers)
        except Exception as e:
            print("Error is " + str(e))
        return (r.reason, r.status_code, r.content)
