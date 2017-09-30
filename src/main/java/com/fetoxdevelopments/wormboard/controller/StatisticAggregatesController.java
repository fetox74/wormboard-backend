package com.fetoxdevelopments.wormboard.controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fetoxdevelopments.wormboard.bean.ServerStatusBean;
import com.fetoxdevelopments.wormboard.bean.ZwhAggregateBean;
import com.fetoxdevelopments.wormboard.bean.ZwhHourlyAggregateBean;
import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;
import com.fetoxdevelopments.wormboard.status.ResponseTime;
import com.fetoxdevelopments.wormboard.worker.ZwhAggregateWorker;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticAggregatesController
{
  @Autowired
  private ZwhAggregateWorker zwhAggregateWorker;

  @Autowired
  private ResponseTime responseTime;

  @RequestMapping("/getRawStatsForDay")
  public List<ZwhAggregateJPA> getRawStatsForDay(@RequestParam(value = "date", defaultValue = "") Long date)
  {
    return zwhAggregateWorker.getRawStatsForDay(date);
  }

  @RequestMapping("/getServerStatus")
  public ServerStatusBean getServerStatus()
  {
    String statusMsg;
    Set<String> allMonth = new LinkedHashSet<>();
    List<Long> allDates = zwhAggregateWorker.getAllDates();

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
  public List<ZwhAggregateBean> getStatsForMonth(@RequestParam(value = "month", defaultValue = "") Long month)
  {
    return zwhAggregateWorker.getStatsForTimespan(month * 100, month * 100 + 99);
  }

  @RequestMapping("/getStatsForQuarter")
  public List<ZwhAggregateBean> getStatsForQuarter(@RequestParam(value = "quarter", defaultValue = "") Long quarter)
  {
    return zwhAggregateWorker.getStatsForTimespan(quarter * 100, (quarter + 2) * 100 + 99);
  }

  @RequestMapping("/getStatsForYear")
  public List<ZwhAggregateBean> getStatsForYear(@RequestParam(value = "year", defaultValue = "") Long year)
  {
    return zwhAggregateWorker.getStatsForTimespan(year * 10000, year * 10000 + 9999);
  }

  @RequestMapping("/getStatsForLast90Days")
  public List<ZwhAggregateBean> getStatsForLast90Days()
  {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate ninetyDaysAgo = today.minusDays(90);
    Long yesterdayAsLong = yesterday.getYear() * 10000L + yesterday.getMonth().getValue() * 100L + yesterday.getDayOfMonth();
    Long ninetyDaysAgoAsLong = ninetyDaysAgo.getYear() * 10000L + ninetyDaysAgo.getMonth().getValue() * 100L + ninetyDaysAgo.getDayOfMonth();
    return zwhAggregateWorker.getStatsForTimespan(ninetyDaysAgoAsLong, yesterdayAsLong);
  }

  @RequestMapping("/getHourlyCorpStatsForMonth")
  public ZwhHourlyAggregateBean getHourlyCorpStatsForMonth(@RequestParam(value = "corporation", defaultValue = "") String corporation,
                                                           @RequestParam(value = "month", defaultValue = "") Long month)
  {
    return zwhAggregateWorker.getHourlyStatsForCorpAndTimespan(corporation, month * 100, month * 100 + 99);
  }

  @RequestMapping("/getHourlyCorpStatsForQuarter")
  public ZwhHourlyAggregateBean getHourlyCorpStatsForQuarter(@RequestParam(value = "corporation", defaultValue = "") String corporation,
                                                             @RequestParam(value = "quarter", defaultValue = "") Long quarter)
  {
    return zwhAggregateWorker.getHourlyStatsForCorpAndTimespan(corporation, quarter * 100, (quarter + 2) * 100 + 99);
  }

  @RequestMapping("/getHourlyCorpStatsForYear")
  public ZwhHourlyAggregateBean getHourlyCorpStatsForYear(@RequestParam(value = "corporation", defaultValue = "") String corporation,
                                                          @RequestParam(value = "year", defaultValue = "") Long year)
  {
    return zwhAggregateWorker.getHourlyStatsForCorpAndTimespan(corporation, year * 10000, year * 10000 + 9999);
  }

  @RequestMapping("/getHourlyCorpStatsForLast90Days")
  public ZwhHourlyAggregateBean getHourlyCorpStatsForLast90Days(@RequestParam(value = "corporation", defaultValue = "") String corporation)
  {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate ninetyDaysAgo = today.minusDays(90);
    Long yesterdayAsLong = yesterday.getYear() * 10000L + yesterday.getMonth().getValue() * 100L + yesterday.getDayOfMonth();
    Long ninetyDaysAgoAsLong = ninetyDaysAgo.getYear() * 10000L + ninetyDaysAgo.getMonth().getValue() * 100L + ninetyDaysAgo.getDayOfMonth();
    return zwhAggregateWorker.getHourlyStatsForCorpAndTimespan(corporation, ninetyDaysAgoAsLong, yesterdayAsLong);
  }
}