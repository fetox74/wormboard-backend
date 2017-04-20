package com.fetoxdevelopments.wormboard.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fetoxdevelopments.wormboard.bean.ZwhAggregateBean;
import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;
import com.fetoxdevelopments.wormboard.repository.ZwhAggregateRepository;
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

  @RequestMapping("/getStatsForMonth")
  public List<ZwhAggregateBean> getStatsForMonth(@RequestParam(value = "month", defaultValue = "") Long month)
  {
    return zwhAggregateWorker.getStatsForMonth(month);
  }
}