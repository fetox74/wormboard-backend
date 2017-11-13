#!/usr/bin/env python
import logging
import os
import re
import urllib2
import gzip
import json
import datetime
from time import sleep
from StringIO import StringIO


def getIPGeoInformation(ip):
    request = urllib2.Request("https://ipapi.co/" + ip + "/json/")
    request.add_header("Accept-encoding", "gzip")
    request.add_header("Cache-Control", "1")
    try:
        response = urllib2.urlopen(request)
    except urllib2.HTTPError as err:
        LOG.error(err)
        return []
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)


FORMAT = '%(asctime)-15s [%(levelname)-8s] %(message)s'
logging.basicConfig(format=FORMAT, level=logging.INFO)
LOG = logging.getLogger(__name__)

PATH = 'C:\\Users\\odittrich\\Desktop\\access_logs\\'
RE_LOGLINE = '([(\d\.)]+) - - \[(.*?)\] "(.*?)" (\d+)'
IPs = {}

LOG.info("processing logfiles..")
for filename in os.listdir(PATH):
    if filename.startswith('localhost_access_log.'):
        LOG.info("parsing " + filename)
        with open(PATH + filename, 'rU') as f:
            for line in f:
                if 'GET /WormBoardREST/getStatsForYear?year=' in line:
                    result = re.match(RE_LOGLINE, line).groups()
                    ip = result[0]
                    timestamp = result[1].split(' ')[0].replace(':', ' ', 1).replace('/', ' ').replace(' ', '. ', 1)
                    request = result[2]
                    httpcode = result[3]
                    # LOG.info(ip + " - " + timestamp)
                    IPs[ip] = {}
        continue
    else:
        continue

LOG.info("obtaining geo information..")
for ip in IPs:
    sleep(0.5)
    LOG.info("geoInfo: %s" % str(getIPGeoInformation(ip)))




