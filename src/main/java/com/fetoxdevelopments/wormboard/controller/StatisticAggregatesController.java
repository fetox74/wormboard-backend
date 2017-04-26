package com.fetoxdevelopments.wormboard.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fetoxdevelopments.wormboard.bean.ServerStatusBean;
import com.fetoxdevelopments.wormboard.bean.ZwhAggregateBean;
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

  @RequestMapping("/getStatsForYear")
  public List<ZwhAggregateBean> getStatsForYear(@RequestParam(value = "year", defaultValue = "") Long year)
  {
    return zwhAggregateWorker.getStatsForTimespan(year * 10000, year * 10000 + 9999);
  }
}