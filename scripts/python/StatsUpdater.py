#!/usr/bin/env python
import re
import urllib2, gzip, json
from StringIO import StringIO
from multiprocessing.pool import ThreadPool
from itertools import islice
import psycopg2


def partition(data, SIZE=100):
    it = iter(data)
    for i in xrange(0, len(data), SIZE):
        yield dict((k, data[k]) for k in islice(it, SIZE))


def getKillmailHashes(date):
    request = urllib2.Request("https://zkillboard.com/api/history/" + date + "/")
    request.add_header("Accept-encoding", "gzip")
    request.add_header("Cache-Control", "1")
    try:
        response = urllib2.urlopen(request)
    except urllib2.HTTPError as err:
        print err.headers
        return []
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)


def getCREST(tupleIdHash):
    # print "%s: %s" % (tupleIdHash[0], tupleIdHash[1])
    request = urllib2.Request("https://crest-tq.eveonline.com/killmails/" + tupleIdHash[0] + "/" + tupleIdHash[1] + "/")
    request.add_header("Accept-encoding", "gzip")
    request.add_header("Cache-Control", "1")
    try:
        response = urllib2.urlopen(request)
    except urllib2.HTTPError as err:
        print err.headers
        return []
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)


def getZKB(id):
    request = urllib2.Request("https://zkillboard.com/api/killID/" + id + "/")
    request.add_header("Accept-encoding", "gzip")
    request.add_header("Cache-Control", "1")
    try:
        response = urllib2.urlopen(request)
    except urllib2.HTTPError as err:
        print err.headers
        return None
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()

    result = json.loads(data)
    if len(result) > 0:
        return result[0]
    else:
        return None


def getFinalHitCorp(attackers):
    for attacker in attackers:
        if attacker["finalBlow"]:
            if "corporation" in attacker:
                return attacker["corporation"]["name"]
            else:
                return ""


def getIskLossForCorp(corp):
    if corp in lossDict:
        return lossDict[corp]
    else:
        return 0.0


def getAttackersOfCorp(attackers, corporation):
    result = set()
    for attacker in attackers:
        if "corporation" in attacker and attacker["corporation"]["name"] == corporation and "character" in attacker:
            result.add(attacker["character"]["id"])
    return result


def updateMasterDict(killmailCREST, killmailZKB):
    if killmailZKB:
        finalHitCorp = getFinalHitCorp(killmailCREST["attackers"])
        victimCorp = killmailCREST["victim"]["corporation"]["name"]
        if finalHitCorp != "":
            attackersOfFinalHitCorp = getAttackersOfCorp(killmailCREST["attackers"], finalHitCorp)
            if finalHitCorp in masterDict:
                masterDict[finalHitCorp]["kills"] = masterDict[finalHitCorp]["kills"] + 1
                masterDict[finalHitCorp]["isk"] = masterDict[finalHitCorp]["isk"] + killmailZKB["zkb"]["totalValue"]
                masterDict[finalHitCorp]["active"] = masterDict[finalHitCorp]["active"] | attackersOfFinalHitCorp
                masterDict[finalHitCorp]["sumonkills"] = masterDict[finalHitCorp]["sumonkills"] + len(attackersOfFinalHitCorp)
            else:
                masterDict[finalHitCorp] = {"kills": 1, "isk": killmailZKB["zkb"]["totalValue"], "active": attackersOfFinalHitCorp, "sumonkills": len(attackersOfFinalHitCorp)}
        if victimCorp in lossDict:
            lossDict[victimCorp] = lossDict[victimCorp] + killmailZKB["zkb"]["totalValue"]
        else:
            lossDict[victimCorp] = killmailZKB["zkb"]["totalValue"]
    else:
        print "kill id " + killmailCREST["killID_str"] + " seems not to exist on zKillboard.."

def queryAggregateAlreadyInDB(cur, date, corp):
    cur.execute('SELECT * FROM "zwhAggregate" WHERE "date" = ' + date + ' AND "corporation" = ' "'" + corp + "'")
    if len(cur.fetchall()) > 0:
        return True
    else:
        return False


def updateDB(cur, date):
    for key, value in masterDict.items():
        cur.execute('INSERT INTO "zwhAggregate" ("date", "corporation", "kills", "isk", "active", "numactive", "netisk", "sumonkills") VALUES (%i, %s, %i, %f, %s, %i, %f, %i)' % (int(date), "'" + key.replace("'", "''") + "'", value["kills"], value["isk"], "'" + ",".join(map(str, value["active"])) + "'", len(value["active"]), value["isk"] - getIskLossForCorp(key), value["sumonkills"]))
    conn.commit()


DATES = ["20170522"]
reJMail = re.compile("J[0-9]{6}")

try:
    conn = psycopg2.connect("dbname='staticdump' user='postgres' host='localhost' password='bollox'")
except:
    print "Unable to connect to the database"
    exit(-1)
cur = conn.cursor()

for date in DATES:
    counter = 0
    jMailCounter = 0
    dictKillmailIdHash = getKillmailHashes(date)
    masterDict = {}
    lossDict = {}

    print "processing " + date
    chunks = partition(dictKillmailIdHash)
    for chunk in chunks:
        pool = ThreadPool(100)
        results = pool.map(getCREST, chunk.items())
        pool.close()
        pool.join()

        for killmailCREST in results:
            if killmailCREST != [] and (reJMail.match(killmailCREST["solarSystem"]["name"]) or killmailCREST["solarSystem"]["name"] == "J1226-0"):
                updateMasterDict(killmailCREST, getZKB(killmailCREST["killID_str"]))
                jMailCounter += 1
            elif not killmailCREST: # 20160824 has the problematic first Keepstar kill that does not appear on CREST, this (and the above killmailCREST != []) is a temporary fix..
                print("[] error...")
            counter += 1

        print "total kills: %i" % counter
        print "total WH kills: %i" % jMailCounter

    updateDB(cur, date)

conn.close()



