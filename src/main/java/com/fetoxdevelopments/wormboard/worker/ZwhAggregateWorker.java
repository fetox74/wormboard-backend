package com.fetoxdevelopments.wormboard.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fetoxdevelopments.wormboard.bean.ZwhAggregateBean;
import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;
import com.fetoxdevelopments.wormboard.repository.ZwhAggregateRepository;
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

  public List<ZwhAggregateJPA> getRawStatsForDay(Long date)
  {
    return zwhAggregateRepository.findByDate(date);
  }

  public List<ZwhAggregateBean> getStatsForTimespan(Long dateBegin, Long dateEnd)
  {
    List<ZwhAggregateBean> result = new ArrayList<>();

    Map<String, Long> mapCorpKills = new HashMap<>();
    Map<String, Double> mapCorpIsk = new HashMap<>();
    Map<String, Set<String>> mapCorpActive = new HashMap<>();
    Map<String, Long> mapCorpNumActive = new HashMap<>();
    Map<String, Double> mapCorpNetIsk = new HashMap<>();
    Map<String, Long> mapCorpSumOnKills = new HashMap<>();

    Stopwatch dbStopwatch = Stopwatch.createStarted();
    List<ZwhAggregateJPA> aggregates = zwhAggregateRepository.findBetweenDates(dateBegin, dateEnd);
    LOG.info("DB access in " + dbStopwatch.toString());

    Stopwatch aggStopwatch = Stopwatch.createStarted();
    for(ZwhAggregateJPA aggregate : aggregates)
    {
      String corporation = aggregate.getCorporation();
      if(mapCorpKills.containsKey(corporation))
      {
        mapCorpKills.put(corporation, mapCorpKills.get(corporation) + aggregate.getKills());
        mapCorpIsk.put(corporation, mapCorpIsk.get(corporation) + aggregate.getIsk());
        Set<String> active = mapCorpActive.get(corporation);
        active.addAll(new HashSet<>(Arrays.asList(aggregate.getActive().split(","))));
        //mapCorpActive.put(corporation, active);
        mapCorpNumActive.put(corporation, (long) active.size());
        mapCorpNetIsk.put(corporation, mapCorpNetIsk.get(corporation) + aggregate.getNetisk());
        mapCorpSumOnKills.put(corporation, mapCorpSumOnKills.get(corporation) + aggregate.getSumonkills());
      }
      else
      {
        mapCorpKills.put(corporation, aggregate.getKills());
        mapCorpIsk.put(corporation, aggregate.getIsk());
        mapCorpActive.put(corporation, new HashSet<>(Arrays.asList(aggregate.getActive().split(","))));
        mapCorpNumActive.put(corporation, aggregate.getNumactive());
        mapCorpNetIsk.put(corporation, aggregate.getNetisk());
        mapCorpSumOnKills.put(corporation, aggregate.getSumonkills());
      }
    }

    for(String corporation : mapCorpKills.keySet())
    {
      long numactive = mapCorpNumActive.get(corporation);
      long kills = mapCorpKills.get(corporation);
      double avgperkill = (double) mapCorpSumOnKills.get(corporation) / kills;

      if(numactive > 0 && avgperkill > 0.0 && !corporation.equals("Vigilant Tyrannos"))
      {
        double isk = mapCorpIsk.get(corporation);
        double netisk = mapCorpNetIsk.get(corporation);
        result.add(new ZwhAggregateBean(corporation, kills, isk, netisk, numactive, avgperkill,isk / (double) numactive,
                                        netisk / (double) numactive, isk / avgperkill, netisk / avgperkill));
      }
    }
    LOG.info("Aggregated in " + aggStopwatch.toString());

    return result;
  }
}
