package com.fetoxdevelopments.wormboard.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fetoxdevelopments.wormboard.bean.ZwbAggregateCorpBean;
import com.fetoxdevelopments.wormboard.bean.ZwbHourlyAggregateCorpBean;
import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCorpJPA;
import com.fetoxdevelopments.wormboard.repository.ZwbAggregateCorpRepository;
import com.fetoxdevelopments.wormboard.status.ResponseTime;
import com.google.common.base.Stopwatch;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZwbAggregateCorpWorker
{
  private static final Logger LOG = LogManager.getLogger(ZwbAggregateCorpWorker.class);

  @Autowired
  private ZwbAggregateCorpRepository zwbAggregateCorpRepository;

  @Autowired
  private ResponseTime responseTime;

  public List<ZwbAggregateCorpJPA> getRawStatsForDay(Long date)
  {
    return zwbAggregateCorpRepository.findByDate(date);
  }

  public List<Long> getAllDates()
  {
    return zwbAggregateCorpRepository.findAllDates();
  }

  public List<ZwbAggregateCorpBean> getStatsForTimespan(Long dateBegin, Long dateEnd)
  {
    List<ZwbAggregateCorpBean> result = new ArrayList<>();

    Map<String, Long> mapCorpKills = new HashMap<>();
    Map<String, Long> mapCorpLosses = new HashMap<>();
    Map<String, Double> mapCorpIskwon = new HashMap<>();
    Map<String, Double> mapCorpIsklost = new HashMap<>();
    Map<String, Set<String>> mapCorpActive = new HashMap<>();
    Map<String, Long> mapCorpNumActive = new HashMap<>();
    Map<String, Long> mapCorpSumOnKills = new HashMap<>();

    Stopwatch dbStopwatch = Stopwatch.createStarted();
    List<ZwbAggregateCorpJPA> aggregates = zwbAggregateCorpRepository.findBetweenDates(dateBegin, dateEnd);
    dbStopwatch.stop();
    LOG.info("DB access in " + dbStopwatch.toString());

    Stopwatch aggStopwatch = Stopwatch.createStarted();
    for(ZwbAggregateCorpJPA aggregate : aggregates)
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
        double kdefficiency = (1 - (double) losses / (double) (kills + losses)) * 100.0;
        double iskefficiency = (1 - isklost / (iskwon + isklost)) * 100.0;
        result.add(new ZwbAggregateCorpBean(corporation, kills, losses, kdratio, kdefficiency, iskwon, isklost, netisk, iskefficiency, numactive, avgperkill,
                                        iskwon / (double) numactive, netisk / (double) numactive, iskwon / avgperkill, netisk / avgperkill));
      }
    }
    aggStopwatch.stop();
    LOG.info("Aggregated in " + aggStopwatch.toString());

    responseTime.addNewRequest((double)dbStopwatch.elapsed(TimeUnit.MILLISECONDS), (double)aggStopwatch.elapsed(TimeUnit.MILLISECONDS));

    return result;
  }

  public ZwbHourlyAggregateCorpBean getHourlyStatsForCorpAndTimespan(String corporation, Long dateBegin, Long dateEnd)
  {
    long[] kills = new long[24];
    long[] sumonkills = new long[24];
    double[] avgkillsperday = new double[24];
    double[] avgonkills = new double[24];
    int daysCounted = 0;

    List<ZwbAggregateCorpJPA> aggregates = zwbAggregateCorpRepository.findForCorpBetweenDates(corporation, dateBegin, dateEnd);

    for(ZwbAggregateCorpJPA aggregate : aggregates)
    {
      kills[0] += aggregate.getKillsinhour00();
      sumonkills[0] += aggregate.getSumonkillsinhour00();
      kills[1] += aggregate.getKillsinhour01();
      sumonkills[1] += aggregate.getSumonkillsinhour01();
      kills[2] += aggregate.getKillsinhour02();
      sumonkills[2] += aggregate.getSumonkillsinhour02();
      kills[3] += aggregate.getKillsinhour03();
      sumonkills[3] += aggregate.getSumonkillsinhour03();
      kills[4] += aggregate.getKillsinhour04();
      sumonkills[4] += aggregate.getSumonkillsinhour04();
      kills[5] += aggregate.getKillsinhour05();
      sumonkills[5] += aggregate.getSumonkillsinhour05();
      kills[6] += aggregate.getKillsinhour06();
      sumonkills[6] += aggregate.getSumonkillsinhour06();
      kills[7] += aggregate.getKillsinhour07();
      sumonkills[7] += aggregate.getSumonkillsinhour07();
      kills[8] += aggregate.getKillsinhour08();
      sumonkills[8] += aggregate.getSumonkillsinhour08();
      kills[9] += aggregate.getKillsinhour09();
      sumonkills[9] += aggregate.getSumonkillsinhour09();
      kills[10] += aggregate.getKillsinhour10();
      sumonkills[10] += aggregate.getSumonkillsinhour10();
      kills[11] += aggregate.getKillsinhour11();
      sumonkills[11] += aggregate.getSumonkillsinhour11();
      kills[12] += aggregate.getKillsinhour12();
      sumonkills[12] += aggregate.getSumonkillsinhour12();
      kills[13] += aggregate.getKillsinhour13();
      sumonkills[13] += aggregate.getSumonkillsinhour13();
      kills[14] += aggregate.getKillsinhour14();
      sumonkills[14] += aggregate.getSumonkillsinhour14();
      kills[15] += aggregate.getKillsinhour15();
      sumonkills[15] += aggregate.getSumonkillsinhour15();
      kills[16] += aggregate.getKillsinhour16();
      sumonkills[16] += aggregate.getSumonkillsinhour16();
      kills[17] += aggregate.getKillsinhour17();
      sumonkills[17] += aggregate.getSumonkillsinhour17();
      kills[18] += aggregate.getKillsinhour18();
      sumonkills[18] += aggregate.getSumonkillsinhour18();
      kills[19] += aggregate.getKillsinhour19();
      sumonkills[19] += aggregate.getSumonkillsinhour19();
      kills[20] += aggregate.getKillsinhour20();
      sumonkills[20] += aggregate.getSumonkillsinhour20();
      kills[21] += aggregate.getKillsinhour21();
      sumonkills[21] += aggregate.getSumonkillsinhour21();
      kills[22] += aggregate.getKillsinhour22();
      sumonkills[22] += aggregate.getSumonkillsinhour22();
      kills[23] += aggregate.getKillsinhour23();
      sumonkills[23] += aggregate.getSumonkillsinhour23();
      daysCounted++;
    }

    final int days = daysCounted;

    IntStream.range(0, 24)
      .forEach(i -> {avgonkills[i] = kills[i] == 0 ? 0.0 : (double) sumonkills[i] / (double) kills[i];
                     avgkillsperday[i] = days == 0 ? 0.0 : (double) kills[i] / (double) days;});

    return new ZwbHourlyAggregateCorpBean(kills, sumonkills, avgkillsperday, avgonkills);
  }

  public Set<Long> getActivePlayerIdsForCorpAndTimespan(String corporation, Long dateBegin, Long dateEnd)
  {
    Set<Long> result = new HashSet<>();
    List<ZwbAggregateCorpJPA> aggregates = zwbAggregateCorpRepository.findForCorpBetweenDates(corporation, dateBegin, dateEnd);

    for(ZwbAggregateCorpJPA aggregate : aggregates)
    {
      result.addAll(Arrays.stream(aggregate.getActive().split(",")).map(e -> Long.parseLong(e)).collect(Collectors.toSet()));
    }
    return result;
  }
}
