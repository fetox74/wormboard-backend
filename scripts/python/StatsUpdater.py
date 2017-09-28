#!/usr/bin/env python
import gzip
import json
import re
import urllib2
from StringIO import StringIO
from multiprocessing.pool import ThreadPool

import psycopg2
from itertools import islice


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
        return lossDict[corp]["iskLost"]
    else:
        return 0.0


def getLossesForCorp(corp):
    if corp in lossDict:
        return lossDict[corp]["losses"]
    else:
        return 0


def getAttackersOfCorp(attackers, corporation):
    result = set()
    for attacker in attackers:
        if "corporation" in attacker and attacker["corporation"]["name"] == corporation and "character" in attacker:
            result.add(attacker["character"]["id"])
    return result


def getHourOfKill(datetime):
    return "00"


def updateMasterDict(killmailCREST, killmailZKB):
    if killmailZKB:
        finalHitCorp = getFinalHitCorp(killmailCREST["attackers"])
        victimCorp = killmailCREST["victim"]["corporation"]["name"]
        if finalHitCorp != "":
            attackersOfFinalHitCorp = getAttackersOfCorp(killmailCREST["attackers"], finalHitCorp)
            if finalHitCorp in masterDict:
                masterDict[finalHitCorp]["kills"] = masterDict[finalHitCorp]["kills"] + 1
                masterDict[finalHitCorp]["iskwon"] = masterDict[finalHitCorp]["iskwon"] + killmailZKB["zkb"]["totalValue"]
                masterDict[finalHitCorp]["active"] = masterDict[finalHitCorp]["active"] | attackersOfFinalHitCorp
                masterDict[finalHitCorp]["sumonkills"] = masterDict[finalHitCorp]["sumonkills"] + len(attackersOfFinalHitCorp)
            else:
                masterDict[finalHitCorp] = {"kills": 1, "iskwon": killmailZKB["zkb"]["totalValue"], "active": attackersOfFinalHitCorp,
                                            "sumOnKills": len(attackersOfFinalHitCorp)}
        if victimCorp in lossDict:
            lossDict[victimCorp]["losses"] = lossDict[victimCorp]["losses"] + 1
            lossDict[victimCorp]["isklost"] = lossDict[victimCorp]["isklost"] + killmailZKB["zkb"]["totalValue"]
        else:
            lossDict[victimCorp] = {"losses": 1, "isklost": killmailZKB["zkb"]["totalValue"]}
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
        cur.execute(
            '''INSERT INTO "zwhAggregate" ("date", "corporation", "kills", "losses", "iskwon", "isklost", "active", "numactive", "sumonkills", 
            "killsinhour00", "killsinhour01", "killsinhour02", "killsinhour03", "killsinhour04", "killsinhour05", "killsinhour06", "killsinhour07", 
            "killsinhour08", "killsinhour09", "killsinhour10", "killsinhour11", "killsinhour12", "killsinhour13", "killsinhour14", "killsinhour15", 
            "killsinhour16", "killsinhour17", "killsinhour18", "killsinhour19", "killsinhour20", "killsinhour21", "killsinhour22", "killsinhour23", 
            "sumonkillsinhour00", "sumonkillsinhour01", "sumonkillsinhour02", "sumonkillsinhour03", "sumonkillsinhour04", "sumonkillsinhour05", 
            "sumonkillsinhour06", "sumonkillsinhour07", "sumonkillsinhour08", "sumonkillsinhour09", "sumonkillsinhour10", "sumonkillsinhour11", 
            "sumonkillsinhour12", "sumonkillsinhour13", "sumonkillsinhour14", "sumonkillsinhour15", "sumonkillsinhour16", "sumonkillsinhour17", 
            "sumonkillsinhour18", "sumonkillsinhour19", "sumonkillsinhour20", "sumonkillsinhour21", "sumonkillsinhour22", "sumonkillsinhour23") 
            VALUES (%i, %s, %i, %i, %f, %f, %s, %i, %i, 
            %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, 
            %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i)''' % (
            int(date),
            "'" + key.replace("'", "''") + "'",
            value["kills"],
            getLossesForCorp(key),
            value["iskWon"],
            getIskLossForCorp(key),
            "'" + ",".join(map(str, value["active"])) + "'",
            len(value["active"]),
            value["sumOnKills"],
            value["killsinhour"]["00"],
            value["killsinhour"]["01"],
            value["killsinhour"]["02"],
            value["killsinhour"]["03"],
            value["killsinhour"]["04"],
            value["killsinhour"]["05"],
            value["killsinhour"]["06"],
            value["killsinhour"]["07"],
            value["killsinhour"]["08"],
            value["killsinhour"]["09"],
            value["killsinhour"]["10"],
            value["killsinhour"]["11"],
            value["killsinhour"]["12"],
            value["killsinhour"]["13"],
            value["killsinhour"]["14"],
            value["killsinhour"]["15"],
            value["killsinhour"]["16"],
            value["killsinhour"]["17"],
            value["killsinhour"]["18"],
            value["killsinhour"]["19"],
            value["killsinhour"]["20"],
            value["killsinhour"]["21"],
            value["killsinhour"]["22"],
            value["killsinhour"]["23"],
            value["sumonkillsinhour"]["00"],
            value["sumonkillsinhour"]["01"],
            value["sumonkillsinhour"]["02"],
            value["sumonkillsinhour"]["03"],
            value["sumonkillsinhour"]["04"],
            value["sumonkillsinhour"]["05"],
            value["sumonkillsinhour"]["06"],
            value["sumonkillsinhour"]["07"],
            value["sumonkillsinhour"]["08"],
            value["sumonkillsinhour"]["09"],
            value["sumonkillsinhour"]["10"],
            value["sumonkillsinhour"]["11"],
            value["sumonkillsinhour"]["12"],
            value["sumonkillsinhour"]["13"],
            value["sumonkillsinhour"]["14"],
            value["sumonkillsinhour"]["15"],
            value["sumonkillsinhour"]["16"],
            value["sumonkillsinhour"]["17"],
            value["sumonkillsinhour"]["18"],
            value["sumonkillsinhour"]["19"],
            value["sumonkillsinhour"]["20"],
            value["sumonkillsinhour"]["21"],
            value["sumonkillsinhour"]["22"],
            value["sumonkillsinhour"]["23"]))
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
            elif not killmailCREST:  # 20160824 has the problematic first Keepstar kill that does not appear on CREST, this (and the above killmailCREST != []) is a temporary fix..
                print("[] error...")
            counter += 1

        print "total kills: %i" % counter
        print "total WH kills: %i" % jMailCounter

    updateDB(cur, date)

conn.close()
