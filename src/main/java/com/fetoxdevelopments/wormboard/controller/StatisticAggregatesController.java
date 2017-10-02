package com.fetoxdevelopments.wormboard.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fetoxdevelopments.wormboard.bean.ServerStatusBean;
import com.fetoxdevelopments.wormboard.bean.ZwbAggregateCharBean;
import com.fetoxdevelopments.wormboard.bean.ZwbAggregateCorpBean;
import com.fetoxdevelopments.wormboard.bean.ZwbHourlyAggregateCorpBean;
import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCorpJPA;
import com.fetoxdevelopments.wormboard.status.ResponseTime;
import com.fetoxdevelopments.wormboard.worker.ZwbAggregateCharWorker;
import com.fetoxdevelopments.wormboard.worker.ZwbAggregateCorpWorker;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticAggregatesController
{
  @Autowired
  private ZwbAggregateCorpWorker zwbAggregateCorpWorker;

  @Autowired
  private ZwbAggregateCharWorker zwbAggregateCharWorker;

  @Autowired
  private ResponseTime responseTime;

  @RequestMapping("/getRawStatsForDay")
  public List<ZwbAggregateCorpJPA> getRawStatsForDay(@RequestParam(value = "date", defaultValue = "") Long date)
  {
    return zwbAggregateCorpWorker.getRawStatsForDay(date);
  }

  @RequestMapping("/getServerStatus")
  public ServerStatusBean getServerStatus()
  {
    String statusMsg;
    Set<String> allMonth = new LinkedHashSet<>();
    List<Long> allDates = zwbAggregateCorpWorker.getAllDates();

    String latestProcessedDate = allDates.get(allDates.size() - 1).toString();

    for(Long date : allDates)
    {
      String dateAsString = date.toString();
      allMonth.add(dateAsString.substring(0, 6));

      // todo: find missing days
    }

    latestProcessedDate = Joiner.on("-").join(latestProcessedDate.substring(0, 4), latestProcessedDate.substring(4, 6), latestProcessedDate.substring(6, 8));
    statusMsg = "data until " + latestProcessedDate + ", 0 days missing, ødb: " + String.format("%.2f", responseTime.getDbMillis()) +
                " ms, øagg: " + String.format("%.2f", responseTime.getAggMillis()) + " ms";

    return new ServerStatusBean(new ArrayList<>(allMonth), statusMsg);
  }

  @RequestMapping("/getStatsForMonth")
  public List<ZwbAggregateCorpBean> getStatsForMonth(@RequestParam(value = "month", defaultValue = "") Long month)
  {
    return zwbAggregateCorpWorker.getStatsForTimespan(month * 100, month * 100 + 99);
  }

  @RequestMapping("/getStatsForQuarter")
  public List<ZwbAggregateCorpBean> getStatsForQuarter(@RequestParam(value = "quarter", defaultValue = "") Long quarter)
  {
    return zwbAggregateCorpWorker.getStatsForTimespan(quarter * 100, (quarter + 2) * 100 + 99);
  }

  @RequestMapping("/getStatsForYear")
  public List<ZwbAggregateCorpBean> getStatsForYear(@RequestParam(value = "year", defaultValue = "") Long year)
  {
    return zwbAggregateCorpWorker.getStatsForTimespan(year * 10000, year * 10000 + 9999);
  }

  @RequestMapping("/getStatsForLast90Days")
  public List<ZwbAggregateCorpBean> getStatsForLast90Days()
  {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate ninetyDaysAgo = today.minusDays(90);
    Long yesterdayAsLong = yesterday.getYear() * 10000L + yesterday.getMonth().getValue() * 100L + yesterday.getDayOfMonth();
    Long ninetyDaysAgoAsLong = ninetyDaysAgo.getYear() * 10000L + ninetyDaysAgo.getMonth().getValue() * 100L + ninetyDaysAgo.getDayOfMonth();
    return zwbAggregateCorpWorker.getStatsForTimespan(ninetyDaysAgoAsLong, yesterdayAsLong);
  }

  @RequestMapping("/getHourlyCorpStatsForMonth")
  public ZwbHourlyAggregateCorpBean getHourlyCorpStatsForMonth(@RequestParam(value = "corporationid") Long corporationid,
                                                               @RequestParam(value = "month", defaultValue = "") Long month)
  {
    return zwbAggregateCorpWorker.getHourlyStatsForCorpAndTimespan(corporationid, month * 100, month * 100 + 99);
  }

  @RequestMapping("/getHourlyCorpStatsForQuarter")
  public ZwbHourlyAggregateCorpBean getHourlyCorpStatsForQuarter(@RequestParam(value = "corporationid") Long corporationid,
                                                                 @RequestParam(value = "quarter", defaultValue = "") Long quarter)
  {
    return zwbAggregateCorpWorker.getHourlyStatsForCorpAndTimespan(corporationid, quarter * 100, (quarter + 2) * 100 + 99);
  }

  @RequestMapping("/getHourlyCorpStatsForYear")
  public ZwbHourlyAggregateCorpBean getHourlyCorpStatsForYear(@RequestParam(value = "corporationid") Long corporationid,
                                                              @RequestParam(value = "year", defaultValue = "") Long year)
  {
    return zwbAggregateCorpWorker.getHourlyStatsForCorpAndTimespan(corporationid, year * 10000, year * 10000 + 9999);
  }

  @RequestMapping("/getHourlyCorpStatsForLast90Days")
  public ZwbHourlyAggregateCorpBean getHourlyCorpStatsForLast90Days(@RequestParam(value = "corporationid") Long corporationid)
  {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate ninetyDaysAgo = today.minusDays(90);
    Long yesterdayAsLong = yesterday.getYear() * 10000L + yesterday.getMonth().getValue() * 100L + yesterday.getDayOfMonth();
    Long ninetyDaysAgoAsLong = ninetyDaysAgo.getYear() * 10000L + ninetyDaysAgo.getMonth().getValue() * 100L + ninetyDaysAgo.getDayOfMonth();
    return zwbAggregateCorpWorker.getHourlyStatsForCorpAndTimespan(corporationid, ninetyDaysAgoAsLong, yesterdayAsLong);
  }

  @RequestMapping("/getCorpActivePlayerStatsForMonth")
  public List<ZwbAggregateCharBean> getCorpActivePlayerStatsForMonth(@RequestParam(value = "corporationid") Long corporationid,
                                                                     @RequestParam(value = "month", defaultValue = "") Long month)
  {
    return zwbAggregateCharWorker.getStatsForActivePLayersOfCorpInTimespan(corporationid, month * 100, month * 100 + 99);
  }

  @RequestMapping("/getCorpActivePlayerStatsForQuarter")
  public List<ZwbAggregateCharBean> getCorpActivePlayerStatsForQuarter(@RequestParam(value = "corporationid") Long corporationid,
                                                                       @RequestParam(value = "quarter", defaultValue = "") Long quarter)
  {
    return zwbAggregateCharWorker.getStatsForActivePLayersOfCorpInTimespan(corporationid, quarter * 100, (quarter + 2) * 100 + 99);
  }

  @RequestMapping("/getCorpActivePlayerStatsForYear")
  public List<ZwbAggregateCharBean> getCorpActivePlayerStatsForYear(@RequestParam(value = "corporationid") Long corporationid,
                                                                    @RequestParam(value = "year", defaultValue = "") Long year)
  {
    return zwbAggregateCharWorker.getStatsForActivePLayersOfCorpInTimespan(corporationid, year * 10000, year * 10000 + 9999);
  }

  @RequestMapping("/getCorpActivePlayerStatsForLast90Days")
  public List<ZwbAggregateCharBean> getCorpActivePlayerStatsForLast90Days(@RequestParam(value = "corporationid") Long corporationid)
  {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate ninetyDaysAgo = today.minusDays(90);
    Long yesterdayAsLong = yesterday.getYear() * 10000L + yesterday.getMonth().getValue() * 100L + yesterday.getDayOfMonth();
    Long ninetyDaysAgoAsLong = ninetyDaysAgo.getYear() * 10000L + ninetyDaysAgo.getMonth().getValue() * 100L + ninetyDaysAgo.getDayOfMonth();
    return zwbAggregateCharWorker.getStatsForActivePLayersOfCorpInTimespan(corporationid, ninetyDaysAgoAsLong, yesterdayAsLong);
  }
}