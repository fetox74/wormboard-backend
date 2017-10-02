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


def getFinalHitCorpAndUpdateAttackers(attackers, value):
    for attacker in attackers:
        if "corporation" in attacker:
            knownCorporationDict[attacker["corporation"]["id"]] = attacker["corporation"]["name"]
        if "character" in attacker:
            characterid = attacker["character"]["id"]
            character = attacker["character"]["name"]
            knownCharacterDict[characterid] = character
            updateCharacter(characterid, character, 1, 0, value, 0.0)
    for attacker in attackers:
        if attacker["finalBlow"]:
            if "corporation" in attacker:
                return attacker["corporation"]["id"], attacker["corporation"]["name"]
            else:
                return -1, ""


def getIskLossForCorp(corporationid):
    if corporationid in lossDict:
        return lossDict[corporationid]["isklost"]
    else:
        return 0.0


def getLossesForCorp(corporationid):
    if corporationid in lossDict:
        return lossDict[corporationid]["losses"]
    else:
        return 0


def getAttackersOfCorp(attackers, corporationid):
    result = set()
    for attacker in attackers:
        if "corporation" in attacker and attacker["corporation"]["id"] == corporationid and "character" in attacker:
            result.add(attacker["character"]["id"])
    return result


def addNumberToHourDict(datetimestring, dict, number):
    hour = datetimestring[11:13]
    dict[hour] = dict[hour] + number


def getHourDict():
    result = {}
    for i in range(24):
        result[str(i).zfill(2)] = 0
    return result


def updateCharacter(characterid, character, kills, losses, iskwon, isklost):
    if characterid in characterDict:
        characterDict[characterid]["kills"] = characterDict[characterid]["kills"] + kills
        characterDict[characterid]["losses"] = characterDict[characterid]["losses"] + losses
        characterDict[characterid]["iskwon"] = characterDict[characterid]["iskwon"] + iskwon
        characterDict[characterid]["isklost"] = characterDict[characterid]["isklost"] + isklost
    else:
        characterDict[characterid] = {"character": character, "kills": kills, "losses": losses, "iskwon": iskwon, "isklost": isklost}


def updateDictionaries(killmailCREST, killmailZKB):
    if killmailZKB:
        finalHitCorpId, finalHitCorp = getFinalHitCorpAndUpdateAttackers(killmailCREST["attackers"], killmailZKB["zkb"]["totalValue"])
        victimCorpId = killmailCREST["victim"]["corporation"]["id"]
        victimCorp = killmailCREST["victim"]["corporation"]["name"]
        knownCorporationDict[finalHitCorpId] = victimCorp
        if "character" in killmailCREST["victim"]:
            characterid = killmailCREST["victim"]["character"]["id"]
            character = killmailCREST["victim"]["character"]["name"]
            knownCharacterDict[characterid] = character
            updateCharacter(characterid, character, 0, 1, 0.0, killmailZKB["zkb"]["totalValue"])
        if finalHitCorpId != -1:
            attackersOfFinalHitCorp = getAttackersOfCorp(killmailCREST["attackers"], finalHitCorpId)
            if finalHitCorpId in masterDict:
                masterDict[finalHitCorpId]["kills"] = masterDict[finalHitCorpId]["kills"] + 1
                masterDict[finalHitCorpId]["iskwon"] = masterDict[finalHitCorpId]["iskwon"] + killmailZKB["zkb"]["totalValue"]
                masterDict[finalHitCorpId]["active"] = masterDict[finalHitCorpId]["active"] | attackersOfFinalHitCorp
                masterDict[finalHitCorpId]["sumonkills"] = masterDict[finalHitCorpId]["sumonkills"] + len(attackersOfFinalHitCorp)
            else:
                masterDict[finalHitCorpId] = {"corporation": finalHitCorp, "kills": 1, "iskwon": killmailZKB["zkb"]["totalValue"], "active": attackersOfFinalHitCorp,
                                              "sumonkills": len(attackersOfFinalHitCorp), "killsinhour": getHourDict(), "sumonkillsinhour": getHourDict()}
            addNumberToHourDict(killmailCREST["killTime"], masterDict[finalHitCorpId]["killsinhour"], 1)
            addNumberToHourDict(killmailCREST["killTime"], masterDict[finalHitCorpId]["sumonkillsinhour"], len(attackersOfFinalHitCorp))
        if victimCorpId in lossDict:
            lossDict[victimCorpId]["losses"] = lossDict[victimCorpId]["losses"] + 1
            lossDict[victimCorpId]["isklost"] = lossDict[victimCorpId]["isklost"] + killmailZKB["zkb"]["totalValue"]
        else:
            lossDict[victimCorpId] = {"losses": 1, "isklost": killmailZKB["zkb"]["totalValue"]}
    else:
        print "kill id " + killmailCREST["killID_str"] + " seems not to exist on zKillboard.."


def queryAggregateAlreadyInDB(cur, date, corp):
    cur.execute('SELECT * FROM "zwbAggregateCorp" WHERE "date" = ' + date + ' AND "corporation" = ' "'" + corp + "'")
    if len(cur.fetchall()) > 0:
        return True
    else:
        return False


def updateDB(cur, date):
    cur.execute('DELETE FROM "zwbAggregateCorp" WHERE "date" = %i' % int(date))
    cur.execute('DELETE FROM "zwbAggregateChar" WHERE "date" = %i' % int(date))
    for key, value in masterDict.items():
        cur.execute(
            '''INSERT INTO "zwbAggregateCorp" ("date", "corporationid", "corporation", "kills", "losses", "iskwon", "isklost", "active", "numactive", "sumonkills", 
            "killsinhour00", "killsinhour01", "killsinhour02", "killsinhour03", "killsinhour04", "killsinhour05", "killsinhour06", "killsinhour07", 
            "killsinhour08", "killsinhour09", "killsinhour10", "killsinhour11", "killsinhour12", "killsinhour13", "killsinhour14", "killsinhour15", 
            "killsinhour16", "killsinhour17", "killsinhour18", "killsinhour19", "killsinhour20", "killsinhour21", "killsinhour22", "killsinhour23", 
            "sumonkillsinhour00", "sumonkillsinhour01", "sumonkillsinhour02", "sumonkillsinhour03", "sumonkillsinhour04", "sumonkillsinhour05", 
            "sumonkillsinhour06", "sumonkillsinhour07", "sumonkillsinhour08", "sumonkillsinhour09", "sumonkillsinhour10", "sumonkillsinhour11", 
            "sumonkillsinhour12", "sumonkillsinhour13", "sumonkillsinhour14", "sumonkillsinhour15", "sumonkillsinhour16", "sumonkillsinhour17", 
            "sumonkillsinhour18", "sumonkillsinhour19", "sumonkillsinhour20", "sumonkillsinhour21", "sumonkillsinhour22", "sumonkillsinhour23") 
            VALUES (%i, %i, %s, %i, %i, %f, %f, %s, %i, %i, 
            %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, 
            %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i, %i)''' % (
            int(date),
            key,
            "'" + value["corporation"].replace("'", "''") + "'",
            value["kills"],
            getLossesForCorp(key),
            value["iskwon"],
            getIskLossForCorp(key),
            "'" + ",".join(map(str, value["active"])) + "'",
            len(value["active"]),
            value["sumonkills"],
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

    for key, value in knownCharacterDict.items():
        cur.execute('INSERT INTO "zwbKnownCharacter" ("id", "name") SELECT %i, %s WHERE NOT EXISTS (SELECT 1 FROM "zwbKnownCharacter" WHERE id = %i)' %
                    (key, "'" + value.replace("'", "''") + "'", key))

    for key, value in knownCorporationDict.items():
        cur.execute('INSERT INTO "zwbKnownCorporation" ("id", "name") SELECT %i, %s WHERE NOT EXISTS (SELECT 1 FROM "zwbKnownCorporation" WHERE id = %i)' %
                    (key, "'" + value.replace("'", "''") + "'", key))

    for key, value in characterDict.items():
        cur.execute('INSERT INTO "zwbAggregateChar" ("date", "characterid", "character", "kills", "losses", "iskwon", "isklost") VALUES (%i, %i, %s, %i, %i, %f, %f)' %
                    (int(date), key, "'" + value["character"].replace("'", "''") + "'", value["kills"], value["losses"], value["iskwon"], value["isklost"]))
    conn.commit()


DATES = ["20170101", "20170102", "20170103", "20170104", "20170105", "20170106", "20170107", "20170108", "20170109", "20170110"]
#DATES = ["20170111", "20170112", "20170113", "20170114", "20170115", "20170116", "20170117", "20170118", "20170119", "20170120"]
#DATES = ["20170121", "20170122", "20170123", "20170124", "20170125", "20170126", "20170127", "20170128", "20170129", "20170130", "20170131"]
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
    characterDict = {}
    lossDict = {}
    knownCharacterDict = {}
    knownCorporationDict = {}

    print "processing " + date
    chunks = partition(dictKillmailIdHash)
    for chunk in chunks:
        pool = ThreadPool(100)
        results = pool.map(getCREST, chunk.items())
        pool.close()
        pool.join()

        for killmailCREST in results:
            if killmailCREST != [] and (reJMail.match(killmailCREST["solarSystem"]["name"]) or killmailCREST["solarSystem"]["name"] == "J1226-0"):
                updateDictionaries(killmailCREST, getZKB(killmailCREST["killID_str"]))
                jMailCounter += 1
            elif not killmailCREST:  # 20160824 has the problematic first Keepstar kill that does not appear on CREST, this (and the above killmailCREST != []) is a temporary fix..
                print("[] error...")
            counter += 1

        print "total kills: %i" % counter
        print "total WH kills: %i" % jMailCounter

    updateDB(cur, date)

conn.close()
