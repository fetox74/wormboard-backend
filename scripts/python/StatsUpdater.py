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
    request.add_header("Accept-Encoding", "gzip")
    request.add_header("Cache-Control", "1")
    request.add_header("User-Agent", "http://fetox-developments.com/wormboard/ Maintainer: fetox74 EMail: odittrich@gmx.de")
    try:
        response = urllib2.urlopen(request)
    except urllib2.HTTPError as err:
        print err
        return []
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)


def getCREST(tupleIdHash):
    request = urllib2.Request("https://crest-tq.eveonline.com/killmails/" + tupleIdHash[0] + "/" + tupleIdHash[1] + "/")
    request.add_header("Accept-Encoding", "gzip")
    request.add_header("Cache-Control", "1")
    try:
        response = urllib2.urlopen(request)
    except urllib2.HTTPError as err:
        print err
        return []
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)


def getZKB(id, solarSystemId):
    if id in mapIdKillmail:
        return mapIdKillmail[id]

    for page in range(1, 11):
        request = urllib2.Request("https://zkillboard.com/api/no-items/no-attackers/solarSystemID/" + str(solarSystemId) + "/startTime/" + str(date) + "0000/endTime/" + str(date) + "2400/page/" + str(page) + "/")
        request.add_header("Accept-Encoding", "gzip")
        request.add_header("Cache-Control", "1")
        request.add_header("User-Agent", "http://fetox-developments.com/wormboard/ Maintainer: fetox74 EMail: odittrich@gmx.de")
        try:
            response = urllib2.urlopen(request)
        except urllib2.HTTPError as err:
            print err
            return None
        if response.info().get("Content-Encoding") == "gzip":
            buf = StringIO(response.read())
            f = gzip.GzipFile(fileobj=buf)
            data = f.read()
        else:
            data = response.read()

        killmails = json.loads(data)
        if len(killmails) > 0:
            for killmail in killmails:
                mapIdKillmail[killmail["killmail_id"]] = killmail["zkb"]
        else:
            break

    if id in mapIdKillmail:
        return mapIdKillmail[id]
    else:
        return getSingleKillmailZKB(id)


def getSingleKillmailZKB(id):
    request = urllib2.Request("https://zkillboard.com/api/no-items/no-attackers/killID/" + str(id) + "/")
    request.add_header("Accept-Encoding", "gzip")
    request.add_header("Cache-Control", "1")
    request.add_header("User-Agent", "http://fetox-developments.com/wormboard/ Maintainer: fetox74 EMail: odittrich@gmx.de")
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
        return result[0]["zkb"]
    else:
        return None


def getFinalHitCorpAndUpdateAttackers(attackers, value):
    for attacker in attackers:
        if "character" in attacker:
            characterid = attacker["character"]["id"]
            character = attacker["character"]["name"]
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


def createHourDict():
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
        finalHitCorpId, finalHitCorp = getFinalHitCorpAndUpdateAttackers(killmailCREST["attackers"], killmailZKB["totalValue"])
        victimCorpId = killmailCREST["victim"]["corporation"]["id"]
        victimCorp = killmailCREST["victim"]["corporation"]["name"]
        if "character" in killmailCREST["victim"]:
            characterid = killmailCREST["victim"]["character"]["id"]
            character = killmailCREST["victim"]["character"]["name"]
            updateCharacter(characterid, character, 0, 1, 0.0, killmailZKB["totalValue"])
        if finalHitCorpId != -1:
            attackersOfFinalHitCorp = getAttackersOfCorp(killmailCREST["attackers"], finalHitCorpId)
            if finalHitCorpId in masterDict:
                masterDict[finalHitCorpId]["kills"] = masterDict[finalHitCorpId]["kills"] + 1
                masterDict[finalHitCorpId]["iskwon"] = masterDict[finalHitCorpId]["iskwon"] + killmailZKB["totalValue"]
                masterDict[finalHitCorpId]["active"] = masterDict[finalHitCorpId]["active"] | attackersOfFinalHitCorp
                masterDict[finalHitCorpId]["sumonkills"] = masterDict[finalHitCorpId]["sumonkills"] + len(attackersOfFinalHitCorp)
            else:
                masterDict[finalHitCorpId] = {"corporation": finalHitCorp, "kills": 1, "iskwon": killmailZKB["totalValue"], "active": attackersOfFinalHitCorp,
                                              "sumonkills": len(attackersOfFinalHitCorp), "killsinhour": createHourDict(), "sumonkillsinhour": createHourDict()}
            addNumberToHourDict(killmailCREST["killTime"], masterDict[finalHitCorpId]["killsinhour"], 1)
            addNumberToHourDict(killmailCREST["killTime"], masterDict[finalHitCorpId]["sumonkillsinhour"], len(attackersOfFinalHitCorp))
            if victimCorpId not in masterDict:
                masterDict[victimCorpId] = {"corporation": victimCorp, "kills": 0, "iskwon": 0.0, "active": set(),
                                            "sumonkills": 0, "killsinhour": createHourDict(), "sumonkillsinhour": createHourDict()}
            if victimCorpId in lossDict:
                lossDict[victimCorpId]["losses"] = lossDict[victimCorpId]["losses"] + 1
                lossDict[victimCorpId]["isklost"] = lossDict[victimCorpId]["isklost"] + killmailZKB["totalValue"]
            else:
                lossDict[victimCorpId] = {"losses": 1, "isklost": killmailZKB["totalValue"]}
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

    for key, value in characterDict.items():
        cur.execute('INSERT INTO "zwbAggregateChar" ("date", "characterid", "character", "kills", "losses", "iskwon", "isklost") VALUES (%i, %i, %s, %i, %i, %f, %f)' %
                    (int(date), key, "'" + value["character"].replace("'", "''") + "'", value["kills"], value["losses"], value["iskwon"], value["isklost"]))
    conn.commit()


DATES = ["20170101", "20170102", "20170103", "20170104", "20170105", "20170106", "20170107", "20170108", "20170109", "20170110", "20170111", "20170112", "20170113", "20170114", "20170115", "20170116", "20170117", "20170118", "20170119", "20170120", "20170121", "20170122", "20170123", "20170124", "20170125", "20170126", "20170127", "20170128", "20170129", "20170130", "20170131",
         "20170201", "20170202", "20170203", "20170204", "20170205", "20170206", "20170207", "20170208", "20170209", "20170210", "20170211", "20170212", "20170213", "20170214", "20170215", "20170216", "20170217", "20170218", "20170219", "20170220", "20170221", "20170222", "20170223", "20170224", "20170225", "20170226", "20170227", "20170228",
         "20170301", "20170302", "20170303", "20170304", "20170305", "20170306", "20170307", "20170308", "20170309", "20170310", "20170311", "20170312", "20170313", "20170314", "20170315", "20170316", "20170317", "20170318", "20170319", "20170320", "20170321", "20170322", "20170323", "20170324", "20170325", "20170326", "20170327", "20170328", "20170329", "20170330", "20170331",
         "20170401", "20170402", "20170403", "20170404", "20170405", "20170406", "20170407", "20170408", "20170409", "20170410", "20170411", "20170412", "20170413", "20170414", "20170415", "20170416", "20170417", "20170418", "20170419", "20170420", "20170421", "20170422", "20170423", "20170424", "20170425", "20170426", "20170427", "20170428", "20170429", "20170430",
         "20170501", "20170502", "20170503", "20170504", "20170505", "20170506", "20170507", "20170508", "20170509", "20170510", "20170511", "20170512", "20170513", "20170514", "20170515", "20170516", "20170517", "20170518", "20170519", "20170520", "20170521", "20170522", "20170523", "20170524", "20170525", "20170526", "20170527", "20170528", "20170529", "20170530", "20170531",
         "20170601", "20170602", "20170603", "20170604", "20170605", "20170606", "20170607", "20170608", "20170609", "20170610", "20170611", "20170612", "20170613", "20170614", "20170615", "20170616", "20170617", "20170618", "20170619", "20170620", "20170621", "20170622", "20170623", "20170624", "20170625", "20170626", "20170627", "20170628", "20170629", "20170630",
         "20170701", "20170702", "20170703", "20170704", "20170705", "20170706", "20170707", "20170708", "20170709", "20170710", "20170711", "20170712", "20170713", "20170714", "20170715", "20170716", "20170717", "20170718", "20170719", "20170720", "20170721", "20170722", "20170723", "20170724", "20170725", "20170726", "20170727", "20170728", "20170729", "20170730", "20170731",
         "20170801", "20170802", "20170803", "20170804", "20170805", "20170806", "20170807", "20170808", "20170809", "20170810", "20170811", "20170812", "20170813", "20170814", "20170815", "20170816", "20170817", "20170818", "20170819", "20170820", "20170821", "20170822", "20170823", "20170824", "20170825", "20170826", "20170827", "20170828", "20170829", "20170830", "20170831",
         "20170901", "20170902", "20170903", "20170904", "20170905", "20170906", "20170907", "20170908", "20170909", "20170910", "20170911", "20170912", "20170913", "20170914", "20170915", "20170916", "20170917", "20170918", "20170919", "20170920", "20170921", "20170922", "20170923", "20170924", "20170925", "20170926", "20170927", "20170928", "20170929", "20170930",
         "20171001", "20171002", "20171003", "20171004", "20171005", "20171006", "20171007", "20171008", "20171009", "20171010", "20171011", "20171012", "20171013", "20171014", "20171015", "20171016", "20171017", "20171018", "20171019", "20171020", "20171021", "20171022", "20171023", "20171024", "20171025", "20171026", "20171027", "20171028", "20171029", "20171030", "20171031",
         "20171101", "20171102", "20171103", "20171104", "20171105", "20171106", "20171107", "20171108", "20171109", "20171110", "20171111", "20171112", "20171113", "20171114", "20171115", "20171116", "20171117", "20171118", "20171119", "20171120", "20171121", "20171122", "20171123", "20171124", "20171125", "20171126", "20171127", "20171128", "20171129", "20171130",
         "20171201", "20171202", "20171203", "20171204", "20171205", "20171206", "20171207", "20171208", "20171209", "20171210", "20171211", "20171212", "20171213", "20171214", "20171215", "20171216", "20171217", "20171218", "20171219", "20171220", "20171221", "20171222", "20171223", "20171224", "20171225", "20171226", "20171227", "20171228", "20171229", "20171230", "20171231"]
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
    mapIdKillmail = {}

    print "processing " + date
    chunks = partition(dictKillmailIdHash)
    for chunk in chunks:
        pool = ThreadPool(100)
        results = pool.map(getCREST, chunk.items())
        pool.close()
        pool.join()

        for killmailCREST in results:
            if killmailCREST != [] and (reJMail.match(killmailCREST["solarSystem"]["name"] or killmailCREST["solarSystem"]["name"] == "J1226-0")):
                updateDictionaries(killmailCREST, getZKB(killmailCREST["killID"], killmailCREST["solarSystem"]["id"]))
                jMailCounter += 1
            elif not killmailCREST:  # 20160824 has the problematic first Keepstar kill that does not appear on CREST, this (and the above killmailCREST != []) is a temporary fix..
                print("[] error...")
            counter += 1

        print "total kills: %i" % counter
        print "total WH kills: %i" % jMailCounter

    updateDB(cur, date)

conn.close()
