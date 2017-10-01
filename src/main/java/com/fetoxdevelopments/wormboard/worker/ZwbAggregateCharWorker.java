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

  @Autowired
  private ZwbKnownCharacterWorker zwbKnownCharacterWorker;

  public List<ZwbAggregateCharBean> getStatsForActivePLayersOfCorpInTimespan(String corporation, Long dateBegin, Long dateEnd)
  {
    List<ZwbAggregateCharBean> result = new ArrayList<>();

    Map<String, Long> mapCharKills = new HashMap<>();
    Map<String, Long> mapCharLosses = new HashMap<>();
    Map<String, Double> mapCharIskwon = new HashMap<>();
    Map<String, Double> mapCharIsklost = new HashMap<>();

    Set<Long> activeCharsOfCorp = zwbAggregateCorpWorker.getActivePlayerIdsForCorpAndTimespan(corporation, dateBegin, dateEnd);

    Map<String, String> characterIdLookup = zwbKnownCharacterWorker.getCharacterIdLookup(activeCharsOfCorp);

    List<ZwbAggregateCharJPA> aggregates = zwbAggregateCharRepository.findForCharsBetweenDates(characterIdLookup.keySet(), dateBegin, dateEnd);

    for(ZwbAggregateCharJPA aggregate : aggregates)
    {
      String character = aggregate.getCharacter();
      if(mapCharKills.containsKey(character))
      {
        mapCharKills.put(character, mapCharKills.get(character) + aggregate.getKills());
        mapCharLosses.put(character, mapCharLosses.get(character) + aggregate.getLosses());
        mapCharIskwon.put(character, mapCharIskwon.get(character) + aggregate.getIskwon());
        mapCharIsklost.put(character, mapCharIsklost.get(character) + aggregate.getIsklost());
      }
      else
      {
        mapCharKills.put(character, aggregate.getKills());
        mapCharLosses.put(character, aggregate.getLosses());
        mapCharIskwon.put(character, aggregate.getIskwon());
        mapCharIsklost.put(character, aggregate.getIsklost());
      }
    }

    for(String character : mapCharKills.keySet())
    {
      String portraitURL = "https://imageserver.eveonline.com/Character/" + characterIdLookup.get(character) + "_64.jpg";
      long kills = mapCharKills.get(character);
      long losses = mapCharLosses.get(character);
      double iskwon = mapCharIskwon.get(character);
      double isklost = mapCharIsklost.get(character);
      double netisk = iskwon - isklost;
      double kdratio = (double) kills / (double) losses;
      double kdefficiency = (1 - (double) losses / (double) (kills + losses)) * 100.0;
      double iskefficiency = (1 - isklost / (iskwon + isklost)) * 100.0;
      result.add(new ZwbAggregateCharBean(character, portraitURL, kills, losses, kdratio, kdefficiency, iskwon, isklost, netisk, iskefficiency));
    }

    return result;
  }
}
