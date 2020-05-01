

# -*- coding: utf-8 -*-
"""Module to test out logging to kafka."""

import json
import logging

from com.rxcorp.daqe.etl.loggingSystem import LogProducer
from kafka import KafkaProducer

kh1=LogProducer

def run_it(logger=None):
    """Run the actual connections."""

    logger = logging.getLogger(__name__)
    # enable the debug logger if you want to see ALL of the lines
    #logging.basicConfig(level=logging.DEBUG)
    logger.setLevel(logging.DEBUG)
    brokerUri = "kafka-0-broker.dev-dcp-kafka02.cdt.dcos:9625,kafka-1-broker.dev-dcp-kafka02.cdt.dcos:9625, kafka-2-broker.dev-dcp-kafka02.cdt.dcos:9625,kafka-3-broker.dev-dcp-kafka02.cdt.dcos:9625, kafka-4-broker.dev-dcp-kafka02.cdt.dcos:9625",
    topic = "devl-dcp-catest-evt"

    kh = kh1.KafkaHandler(brokerUri, topic)
    logger.addHandler(kh)

    logger.info("I'm a little logger, short and stout")
    logger.debug("Don't tase me bro!")


if __name__ == "__main__":
    run_it()