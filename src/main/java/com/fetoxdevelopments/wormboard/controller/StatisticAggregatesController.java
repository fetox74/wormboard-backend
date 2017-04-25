package com.fetoxdevelopments.wormboard.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fetoxdevelopments.wormboard.bean.ServerStatusBean;
import com.fetoxdevelopments.wormboard.bean.ZwhAggregateBean;
import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;
import com.fetoxdevelopments.wormboard.worker.ZwhAggregateWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticAggregatesController
{
  @Autowired
  private ZwhAggregateWorker zwhAggregateWorker;

  @RequestMapping("/getRawStatsForDay")
  public List<ZwhAggregateJPA> getRawStatsForDay(@RequestParam(value = "date", defaultValue = "") Long date)
  {
    return zwhAggregateWorker.getRawStatsForDay(date);
  }

  @RequestMapping("/getServerStatus")
  public ServerStatusBean getServerStatus()
  {
    Set<String> allMonth = new LinkedHashSet<>();
    List<Long> allDates = zwhAggregateWorker.getAllDates();

    String latestProcessedDate = allDates.get(allDates.size() - 1).toString();

    for(Long date : allDates)
    {
      String dateAsString = date.toString();
      allMonth.add(dateAsString.substring(0, 6));

      // todo: find missing days
    }

    return new ServerStatusBean(new ArrayList<>(allMonth), "killmails processed until 2017-04-21, 0 days missing, &oslash;db: 182.2 ms, &oslash;agg: 35.35 ms");
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