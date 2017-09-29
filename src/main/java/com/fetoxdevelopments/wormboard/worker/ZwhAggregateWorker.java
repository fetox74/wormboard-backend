package com.fetoxdevelopments.wormboard.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fetoxdevelopments.wormboard.bean.ZwhAggregateBean;
import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;
import com.fetoxdevelopments.wormboard.repository.ZwhAggregateRepository;
import com.fetoxdevelopments.wormboard.status.ResponseTime;
import com.google.common.base.Stopwatch;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZwhAggregateWorker
{
  private static final Logger LOG = LogManager.getLogger(ZwhAggregateWorker.class);

  @Autowired
  private ZwhAggregateRepository zwhAggregateRepository;

  @Autowired
  private ResponseTime responseTime;

  public List<ZwhAggregateJPA> getRawStatsForDay(Long date)
  {
    return zwhAggregateRepository.findByDate(date);
  }

  public List<Long> getAllDates()
  {
    return zwhAggregateRepository.findAllDates();
  }

  public List<ZwhAggregateBean> getStatsForTimespan(Long dateBegin, Long dateEnd)
  {
    List<ZwhAggregateBean> result = new ArrayList<>();

    Map<String, Long> mapCorpKills = new HashMap<>();
    Map<String, Long> mapCorpLosses = new HashMap<>();
    Map<String, Double> mapCorpIskwon = new HashMap<>();
    Map<String, Double> mapCorpIsklost = new HashMap<>();
    Map<String, Set<String>> mapCorpActive = new HashMap<>();
    Map<String, Long> mapCorpNumActive = new HashMap<>();
    Map<String, Long> mapCorpSumOnKills = new HashMap<>();

    Stopwatch dbStopwatch = Stopwatch.createStarted();
    List<ZwhAggregateJPA> aggregates = zwhAggregateRepository.findBetweenDates(dateBegin, dateEnd);
    dbStopwatch.stop();
    LOG.info("DB access in " + dbStopwatch.toString());

    Stopwatch aggStopwatch = Stopwatch.createStarted();
    for(ZwhAggregateJPA aggregate : aggregates)
    {
      String corporation = aggregate.getCorporation();
      if(mapCorpKills.containsKey(corporation))
      {
        mapCorpKills.put(corporation, mapCorpKills.get(corporation) + aggregate.getKills());
        mapCorpLosses.put(corporation, mapCorpLosses.get(corporation) + aggregate.getLosses());
        mapCorpIskwon.put(corporation, mapCorpIskwon.get(corporation) + aggregate.getIskwon());
        mapCorpIsklost.put(corporation, mapCorpIsklost.get(corporation) + aggregate.getIsklost());
        Set<String> active = mapCorpActive.get(corporation);
        active.addAll(new HashSet<>(Arrays.asList(aggregate.getActive().split(","))));
        mapCorpNumActive.put(corporation, (long) active.size());
        mapCorpSumOnKills.put(corporation, mapCorpSumOnKills.get(corporation) + aggregate.getSumonkills());
      }
      else
      {
        mapCorpKills.put(corporation, aggregate.getKills());
        mapCorpLosses.put(corporation, aggregate.getLosses());
        mapCorpIskwon.put(corporation, aggregate.getIskwon());
        mapCorpIsklost.put(corporation, aggregate.getIsklost());
        mapCorpActive.put(corporation, new HashSet<>(Arrays.asList(aggregate.getActive().split(","))));
        mapCorpNumActive.put(corporation, aggregate.getNumactive());
        mapCorpSumOnKills.put(corporation, aggregate.getSumonkills());
      }
    }

    for(String corporation : mapCorpKills.keySet())
    {
      long numactive = mapCorpNumActive.get(corporation);
      long kills = mapCorpKills.get(corporation);
      long losses = mapCorpLosses.get(corporation);
      double avgperkill = (double) mapCorpSumOnKills.get(corporation) / kills;

      if(numactive > 0 && avgperkill > 0.0 && !corporation.equals("Vigilant Tyrannos"))
      {
        double iskwon = mapCorpIskwon.get(corporation);
        double isklost = mapCorpIsklost.get(corporation);
        double netisk = iskwon - isklost;
        double kdratio = (double) kills / (double) losses;
        result.add(new ZwhAggregateBean(corporation, kills, losses, kdratio, iskwon, isklost, netisk, numactive, avgperkill, iskwon / (double) numactive,
                                        netisk / (double) numactive, iskwon / avgperkill, netisk / avgperkill));
      }
    }
    aggStopwatch.stop();
    LOG.info("Aggregated in " + aggStopwatch.toString());

    responseTime.addNewRequest((double)dbStopwatch.elapsed(TimeUnit.MILLISECONDS), (double)aggStopwatch.elapsed(TimeUnit.MILLISECONDS));

    return result;
  }
}
