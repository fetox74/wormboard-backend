package com.fetoxdevelopments.wormboard.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.fetoxdevelopments.wormboard.bean.ZwbAggregateCharBean;
import com.fetoxdevelopments.wormboard.bean.ZwbAggregateCorpBean;
import com.fetoxdevelopments.wormboard.bean.ZwbHourlyAggregateCorpBean;
import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCharJPA;
import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCorpJPA;
import com.fetoxdevelopments.wormboard.repository.ZwbAggregateCharRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZwbAggregateCharWorker
{
  private static final Logger LOG = LogManager.getLogger(ZwbAggregateCharWorker.class);

  @Autowired
  private ZwbAggregateCharRepository zwbAggregateCharRepository;

  @Autowired
  private ZwbAggregateCorpWorker zwbAggregateCorpWorker;

  public List<ZwbAggregateCharBean> getStatsForActivePLayersOfCorpInTimespan(Long corporationid, Long dateBegin, Long dateEnd)
  {
    List<ZwbAggregateCharBean> result = new ArrayList<>();

    Map<Long, String> mapCharName = new HashMap<>();
    Map<Long, Long> mapCharKills = new HashMap<>();
    Map<Long, Long> mapCharLosses = new HashMap<>();
    Map<Long, Double> mapCharIskwon = new HashMap<>();
    Map<Long, Double> mapCharIsklost = new HashMap<>();

    Set<Long> activeCharsOfCorp = zwbAggregateCorpWorker.getActivePlayerIdsForCorpAndTimespan(corporationid, dateBegin, dateEnd);

    List<ZwbAggregateCharJPA> aggregates = zwbAggregateCharRepository.findForCharsBetweenDates(activeCharsOfCorp, dateBegin, dateEnd);

    for(ZwbAggregateCharJPA aggregate : aggregates)
    {
      Long characterid = aggregate.getCharacterid();
      if(mapCharKills.containsKey(characterid))
      {
        mapCharKills.put(characterid, mapCharKills.get(characterid) + aggregate.getKills());
        mapCharLosses.put(characterid, mapCharLosses.get(characterid) + aggregate.getLosses());
        mapCharIskwon.put(characterid, mapCharIskwon.get(characterid) + aggregate.getIskwon());
        mapCharIsklost.put(characterid, mapCharIsklost.get(characterid) + aggregate.getIsklost());
      }
      else
      {
        mapCharName.put(characterid, aggregate.getCharacter());
        mapCharKills.put(characterid, aggregate.getKills());
        mapCharLosses.put(characterid, aggregate.getLosses());
        mapCharIskwon.put(characterid, aggregate.getIskwon());
        mapCharIsklost.put(characterid, aggregate.getIsklost());
      }
    }

    for(Long characterid : mapCharKills.keySet())
    {
      String character = mapCharName.get(characterid);
      long kills = mapCharKills.get(characterid);
      long losses = mapCharLosses.get(characterid);
      double iskwon = mapCharIskwon.get(characterid);
      double isklost = mapCharIsklost.get(characterid);
      double netisk = iskwon - isklost;
      double kdratio = (double) kills / (double) losses;
      double kdefficiency = (1 - (double) losses / (double) (kills + losses)) * 100.0;
      double iskefficiency = (1 - isklost / (iskwon + isklost)) * 100.0;
      result.add(new ZwbAggregateCharBean(characterid, character, kills, losses, kdratio, kdefficiency, iskwon, isklost, netisk, iskefficiency));
    }

    return result;
  }
}
