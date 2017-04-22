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
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZwhAggregateWorker
{
  @Autowired
  private ZwhAggregateRepository zwhAggregateRepository;

  public List<ZwhAggregateJPA> getRawStatsForDay(Long date)
  {
    return zwhAggregateRepository.findByDate(date);
  }

  public List<ZwhAggregateBean> getStatsForMonth(Long month)
  {
    List<ZwhAggregateBean> result = new ArrayList<>();

    Map<String, Long> mapCorpKills = new HashMap<>();
    Map<String, Double> mapCorpIsk = new HashMap<>();
    Map<String, Set<String>> mapCorpActive = new HashMap<>();
    Map<String, Long> mapCorpNumActive = new HashMap<>();
    Map<String, Double> mapCorpNetIsk = new HashMap<>();
    Map<String, Long> mapCorpSumOnKills = new HashMap<>();

    List<ZwhAggregateJPA> aggregates = zwhAggregateRepository.findBetweenDates(month * 100, month * 100 + 99);

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
      result.add(new ZwhAggregateBean(month, corporation, mapCorpKills.get(corporation), mapCorpIsk.get(corporation),
                                      Joiner.on(",").join(mapCorpActive.get(corporation)), mapCorpNumActive.get(corporation), mapCorpNetIsk.get(corporation),
                                      mapCorpSumOnKills.get(corporation)));
    }

    return result;
  }
}
