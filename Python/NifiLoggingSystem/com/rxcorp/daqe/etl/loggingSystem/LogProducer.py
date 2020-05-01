import logging,sys
import json
import logging
import sys

from kafka import KafkaProducer


class LogProducer:
    """Class to instantiate the kafka logging facility."""

    def __init__(self, hostlist, topic):
        self.producer = KafkaProducer(bootstrap_servers=hostlist,
                                      #value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                                      linger_ms=10,api_version=(0,10))
        self.topic = topic

    def initializeProducer(self):
        _producer = None
        try:
            _producer = self.producer
        except Exception as ex:
            print('Exception while connecting Kafka')
            print(str(ex))
        finally:
            return _producer

    def sendKafkaMsg(self, message):
        #print(type(message))
        jd = json.dumps(message).encode("utf-8")
        print(jd)
        try:
         #   self.producer.send(self.topic, jd)
            self.producer.send(self.topic, jd)
            self.producer.flush(timeout=100)
            self.producer.close()
        except Exception as ex:
            print('Exception while sending message to Kafka')
            print(str(ex))
            sys.exit()

    def close(self):
        self.producer.close()

brokerUri = "kafka-0-broker.dev-dcp-kafka02.cdt.dcos:9625,kafka-1-broker.dev-dcp-kafka02.cdt.dcos:9625, kafka-2-broker.dev-dcp-kafka02.cdt.dcos:9625,kafka-3-broker.dev-dcp-kafka02.cdt.dcos:9625, kafka-4-broker.dev-dcp-kafka02.cdt.dcos:9625"
topic = "devl-dcp-catest-evt"
message={"Processor Group": "bee62419-016f-1000-f131-5c589af8faff", "PipeLine Name": "DCA_log", "Last Refreshed Time": "12:58:08 IST", "Flow Files In": 0, "Queued": "0 (0 bytes)", "Total Queue Count": "0", "Queued Size": "0 bytes", "File Processed": 0, "flowFilesTransferred": 0, "Running Count": 0, "StoppedCount": 3, "Invalid Count": 0, "Disabled Count": 0}
lp=LogProducer(brokerUri,topic)
lp.initializeProducer()
lp.sendKafkaMsg(message)

# logger = logging.getLogger("")
# logger.setLevel(logging.DEBUG)
# logger.addHandler(kh)
# logger.info("The %s boxing wizards jump %s", 5, "quickly")
# logger.debug("The quick brown %s jumps over the lazy %s", "fox",  "dog")
# try:
#     import math
#     math.exp(1000)
# except:
#     logger.exception("Problem with %s", "math.exp")


# import json
#
# class loggingSystem:
#
#     def __init__(self, topic, brokerUri):
#         self.topic=topic
#         self.brokerUri=brokerUri
#
#     def produceLog(self):
#         _producer = None
#         try:
#             _producer = KafkaProducer(bootstrap_servers=self.brokerUri, api_version=(0, 10))
#
#         except Exception as ex:
#             print('Exception while connecting Kafka')
#             print(str(ex))
#         finally:
#             return _producer
#
#
#     def sendKafkaMsg(self, producer,message):
#         #jd = json.dumps(message).encode('utf-8')
#         #print(jd)
#         try:
#          #   producer.send(self.topic, jd)
#             producer.send(self.topic, message)
#             producer.flush()
#         except Exception as ex:
#             print('Exception while sending message to Kafka')
#             print(str(ex))
#             exit()
#
#
#     def consumeLog(self, groupName):
#         _consume = None
#         try:
#             _consume = KafkaConsumer(self.topic, group_id=groupName)
#         except Exception as ex:
#             print('Exception while connecting Kafka')
#             print(str(ex))
#
#         finally:
#             return _consume