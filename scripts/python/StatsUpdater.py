#!/usr/bin/env python
import gzip
import json
import re
import urllib2
import psycopg2
import time

from StringIO import StringIO
from multiprocessing.pool import ThreadPool
from itertools import islice
from functools import wraps


# See LICENSE.md in /scripts/python folder
def retry(ExceptionToCheck, tries=4, delay=3, backoff=2, logger=None):
    """Retry calling the decorated function using an exponential backoff.

    http://www.saltycrane.com/blog/2009/11/trying-out-retry-decorator-python/
    original from: http://wiki.python.org/moin/PythonDecoratorLibrary#Retry

    :param ExceptionToCheck: the exception to check. may be a tuple of
        exceptions to check
    :type ExceptionToCheck: Exception or tuple
    :param tries: number of times to try (not retry) before giving up
    :type tries: int
    :param delay: initial delay between retries in seconds
    :type delay: int
    :param backoff: backoff multiplier e.g. value of 2 will double the delay
        each retry
    :type backoff: int
    :param logger: logger to use. If None, print
    :type logger: logging.Logger instance
    """
    def deco_retry(f):

        @wraps(f)
        def f_retry(*args, **kwargs):
            mtries, mdelay = tries, delay
            while mtries > 1:
                try:
                    return f(*args, **kwargs)
                except ExceptionToCheck, e:
                    msg = "%s, Retrying in %d seconds..." % (str(e), mdelay)
                    if logger:
                        logger.warning(msg)
                    else:
                        print msg
                    time.sleep(mdelay)
                    mtries -= 1
                    mdelay *= backoff
            return f(*args, **kwargs)

        return f_retry  # true decorator

    return deco_retry


@retry(urllib2.URLError, tries=4, delay=3, backoff=2)
def urlopen_with_retry(request):
    return urllib2.urlopen(request)


def partition(data, SIZE=100):
    it = iter(data)
    for i in xrange(0, len(data), SIZE):
        yield dict((k, data[k]) for k in islice(it, SIZE))


def getSolarSystemIdNameDict():
    result = {}
    cur.execute('SELECT * FROM "mapSolarSystems"')
    solarSystems = cur.fetchall()
    for solarSystem in solarSystems:
        result[solarSystem[2]] = solarSystem[3]
    return result


def getCharacterIdNameDict():
    result = {}
    cur.execute('SELECT DISTINCT characterid, character FROM "zwbAggregateChar"')
    characters = cur.fetchall()
    for character in characters:
        result[character[0]] = character[1]
    return result


def getCorporationIdNameDict():
    result = {}
    cur.execute('SELECT DISTINCT corporationid, corporation FROM "zwbAggregateCorp"')
    corporations = cur.fetchall()
    for corporation in corporations:
        result[corporation[0]] = corporation[1]
    return result


def getCharacterNameESI(characterId):
    request = urllib2.Request("https://esi.evetech.net/latest/characters/names/?character_ids=" + str(characterId) + "&datasource=tranquility")
    request.add_header("Accept-Encoding", "gzip")
    request.add_header("Cache-Control", "1")
    response = urllib2.urlopen(request)
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)[0]["character_name"]


def getCorporationNameESI(corporationId):
    request = urllib2.Request("https://esi.evetech.net/latest/corporations/names/?corporation_ids=" + str(corporationId) + "&datasource=tranquility")
    request.add_header("Accept-Encoding", "gzip")
    request.add_header("Cache-Control", "1")
    response = urllib2.urlopen(request)
    if response.info().get("Content-Encoding") == "gzip":
        buf = StringIO(response.read())
        f = gzip.GzipFile(fileobj=buf)
        data = f.read()
    else:
        data = response.read()
    return json.loads(data)[0]["corporation_name"]


def getCharacterNameForId(charId):
    characterId = int(charId)
    if characterId not in dictCharacterIdName:
        dictCharacterIdName[characterId] = getCharacterNameESI(characterId)
    return dictCharacterIdName[characterId]


def getCorporationNameForId(corpId):
    corporationId = int(corpId)
    if corporationId not in dictCorporationIdName:
        dictCorporationIdName[corporationId] = getCorporationNameESI(corporationId)
    return dictCorporationIdName[corporationId]


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


def getESI(tupleIdHash):
    request = urllib2.Request("https://esi.tech.ccp.is/latest/killmails/" + tupleIdHash[0] + "/" + tupleIdHash[1] + "/?datasource=tranquility")
    request.add_header("Accept-Encoding", "gzip")
    request.add_header("Cache-Control", "1")
    try:
        response = urlopen_with_retry(request)
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

    # todo: this should actually only be done if the solar system has not been read for the current date already (add set of solarsystemid's, make sure to reset for next date)
    for page in range(1, 11):
        request = urllib2.Request(
            "https://zkillboard.com/api/no-items/no-attackers/solarSystemID/" + str(solarSystemId) + "/startTime/" + str(date) + "0000/endTime/" + str(date) +
            "2400/page/" + str(page) + "/")
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
        if "character_id" in attacker:
            characterid = attacker["character_id"]
            character = getCharacterNameForId(characterid)
            updateCharacter(characterid, character, 1, 0, value, 0.0)
    for attacker in attackers:
        if attacker["final_blow"]:
            if "corporation_id" in attacker:
                corporationid = attacker["corporation_id"]
                return corporationid, getCorporationNameForId(corporationid)
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
        if "corporation_id" in attacker and attacker["corporation_id"] == corporationid and "character_id" in attacker:
            result.add(attacker["character_id"])
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


def updateDictionaries(killmailESI, killmailZKB):
    if killmailZKB:
        finalHitCorpId, finalHitCorp = getFinalHitCorpAndUpdateAttackers(killmailESI["attackers"], killmailZKB["totalValue"])
        victimCorpId = killmailESI["victim"]["corporation_id"]
        victimCorp = getCorporationNameForId(victimCorpId)
        if "character_id" in killmailESI["victim"]:
            characterid = killmailESI["victim"]["character_id"]
            character = getCharacterNameForId(characterid)
            updateCharacter(characterid, character, 0, 1, 0.0, killmailZKB["totalValue"])
        if finalHitCorpId != -1:
            attackersOfFinalHitCorp = getAttackersOfCorp(killmailESI["attackers"], finalHitCorpId)
            if finalHitCorpId in masterDict:
                masterDict[finalHitCorpId]["kills"] = masterDict[finalHitCorpId]["kills"] + 1
                masterDict[finalHitCorpId]["iskwon"] = masterDict[finalHitCorpId]["iskwon"] + killmailZKB["totalValue"]
                masterDict[finalHitCorpId]["active"] = masterDict[finalHitCorpId]["active"] | attackersOfFinalHitCorp
                masterDict[finalHitCorpId]["sumonkills"] = masterDict[finalHitCorpId]["sumonkills"] + len(attackersOfFinalHitCorp)
            else:
                masterDict[finalHitCorpId] = {"corporation": finalHitCorp, "kills": 1, "iskwon": killmailZKB["totalValue"], "active": attackersOfFinalHitCorp,
                                              "sumonkills": len(attackersOfFinalHitCorp), "killsinhour": createHourDict(), "sumonkillsinhour": createHourDict()}
            addNumberToHourDict(killmailESI["killmail_time"], masterDict[finalHitCorpId]["killsinhour"], 1)
            addNumberToHourDict(killmailESI["killmail_time"], masterDict[finalHitCorpId]["sumonkillsinhour"], len(attackersOfFinalHitCorp))
            if victimCorpId not in masterDict:
                masterDict[victimCorpId] = {"corporation": victimCorp, "kills": 0, "iskwon": 0.0, "active": set(),
                                            "sumonkills": 0, "killsinhour": createHourDict(), "sumonkillsinhour": createHourDict()}
            if victimCorpId in lossDict:
                lossDict[victimCorpId]["losses"] = lossDict[victimCorpId]["losses"] + 1
                lossDict[victimCorpId]["isklost"] = lossDict[victimCorpId]["isklost"] + killmailZKB["totalValue"]
            else:
                lossDict[victimCorpId] = {"losses": 1, "isklost": killmailZKB["totalValue"]}
    else:
        print "kill id " + str(killmailESI["killmail_id"]) + " seems not to exist on zKillboard.."


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
        cur.execute(
            'INSERT INTO "zwbAggregateChar" ("date", "characterid", "character", "kills", "losses", "iskwon", "isklost") VALUES (%i, %i, %s, %i, %i, %f, %f)' %
            (int(date), key, "'" + value["character"].replace("'", "''") + "'", value["kills"], value["losses"], value["iskwon"], value["isklost"]))
    conn.commit()


DATES = ["20180101", "20180102", "20180103", "20180104", "20180105", "20180106", "20180107", "20180108", "20180109", "20180110", "20180111", "20180112", "20180113", "20180114", "20180115", "20180116", "20180117", "20180118", "20180119", "20180120", "20180121", "20180122", "20180123", "20180124", "20180125", "20180126", "20180127", "20180128", "20180129", "20180130", "20180131",
         "20180201", "20180202", "20180203", "20180204", "20180205", "20180206", "20180207", "20180208", "20180209", "20180210", "20180211", "20180212", "20180213", "20180214", "20180215", "20180216", "20180217", "20180218", "20180219", "20180220", "20180221", "20180222", "20180223", "20180224", "20180225", "20180226", "20180227", "20180228",
         "20180301", "20180302", "20180303", "20180304", "20180305", "20180306", "20180307", "20180308", "20180309", "20180310", "20180311", "20180312", "20180313", "20180314", "20180315", "20180316", "20180317", "20180318", "20180319", "20180320", "20180321", "20180322", "20180323", "20180324", "20180325", "20180326", "20180327", "20180328", "20180329", "20180330", "20180331",
         "20180401", "20180402", "20180403", "20180404", "20180405", "20180406", "20180407", "20180408", "20180409", "20180410", "20180411", "20180412", "20180413", "20180414", "20180415", "20180416", "20180417", "20180418", "20180419", "20180420", "20180421", "20180422", "20180423", "20180424", "20180425", "20180426", "20180427", "20180428", "20180429", "20180430",
         "20180501", "20180502", "20180503", "20180504", "20180505", "20180506", "20180507", "20180508", "20180509", "20180510", "20180511", "20180512", "20180513", "20180514", "20180515", "20180516", "20180517", "20180518", "20180519", "20180520", "20180521", "20180522", "20180523", "20180524", "20180525", "20180526", "20180527", "20180528", "20180529", "20180530", "20180531",
         "20180601", "20180602", "20180603", "20180604", "20180605", "20180606", "20180607", "20180608", "20180609", "20180610", "20180611", "20180612", "20180613", "20180614", "20180615", "20180616", "20180617", "20180618", "20180619", "20180620", "20180621", "20180622", "20180623", "20180624", "20180625", "20180626", "20180627", "20180628", "20180629", "20180630",
         "20180701", "20180702", "20180703", "20180704", "20180705", "20180706", "20180707", "20180708", "20180709", "20180710", "20180711", "20180712", "20180713", "20180714", "20180715", "20180716", "20180717", "20180718", "20180719", "20180720", "20180721", "20180722", "20180723", "20180724", "20180725", "20180726", "20180727", "20180728", "20180729", "20180730", "20180731",
         "20180801", "20180802", "20180803", "20180804", "20180805", "20180806", "20180807", "20180808", "20180809", "20180810", "20180811", "20180812", "20180813", "20180814", "20180815", "20180816", "20180817", "20180818", "20180819", "20180820", "20180821", "20180822", "20180823", "20180824", "20180825", "20180826", "20180827", "20180828", "20180829", "20180830", "20180831",
         "20180901", "20180902", "20180903", "20180904", "20180905", "20180906", "20180907", "20180908", "20180909", "20180910", "20180911", "20180912", "20180913", "20180914", "20180915", "20180916", "20180917", "20180918", "20180919", "20180920", "20180921", "20180922", "20180923", "20180924", "20180925", "20180926", "20180927", "20180928", "20180929", "20180930",
         "20181001", "20181002", "20181003", "20181004", "20181005", "20181006", "20181007", "20181008", "20181009", "20181010", "20181011", "20181012", "20181013", "20181014", "20181015", "20181016", "20181017", "20181018", "20181019", "20181020", "20181021", "20181022", "20181023", "20181024", "20181025", "20181026", "20181027", "20181028", "20181029", "20181030", "20181031",
         "20181101", "20181102", "20181103", "20181104", "20181105", "20181106", "20181107", "20181108", "20181109", "20181110", "20181111", "20181112", "20181113", "20181114", "20181115", "20181116", "20181117", "20181118", "20181119", "20181120", "20181121", "20181122", "20181123", "20181124", "20181125", "20181126", "20181127", "20181128", "20181129", "20181130",
         "20181201", "20181202", "20181203", "20181204", "20181205", "20181206", "20181207", "20181208", "20181209", "20181210", "20181211", "20181212", "20181213", "20181214", "20181215", "20181216", "20181217", "20181218", "20181219", "20181220", "20181221", "20181222", "20181223", "20181224", "20181225", "20181226", "20181227", "20181228", "20181229", "20181230", "20181231"]
reJMail = re.compile("J[0-9]{6}")

try:
    conn = psycopg2.connect("dbname='staticdump' user='postgres' host='localhost' password='bollox'")
except:
    print "Unable to connect to the database"
    exit(-1)
cur = conn.cursor()

dictSolarSystemIdName = getSolarSystemIdNameDict()
dictCharacterIdName = getCharacterIdNameDict()
dictCorporationIdName = getCorporationIdNameDict()

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
        results = pool.map(getESI, chunk.items())
        pool.close()
        pool.join()

        for killmailESI in results:
            if killmailESI != [] and (reJMail.match(dictSolarSystemIdName[killmailESI["solar_system_id"]] or dictSolarSystemIdName[killmailESI["solar_system_id"]] == "J1226-0")):
                updateDictionaries(killmailESI, getZKB(killmailESI["killmail_id"], killmailESI["solar_system_id"]))
                jMailCounter += 1
            elif not killmailESI:  # 20160824 has the problematic first Keepstar kill that does not appear on CREST (ESI unchecked), this (and the above killmailESI != []) is a temporary fix..
                print("[] error...")
            counter += 1

        print "total kills: %i" % counter
        print "total WH kills: %i" % jMailCounter

    updateDB(cur, date)

conn.close()
