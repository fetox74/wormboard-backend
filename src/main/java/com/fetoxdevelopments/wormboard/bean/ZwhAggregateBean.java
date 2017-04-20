package com.fetoxdevelopments.wormboard.bean;

import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;

public class ZwhAggregateBean
{
  private long date;

  private String corporation;

  private long kills;

  private double isk;

  private String active;

  private long numactive;

  private double netisk;

  public ZwhAggregateBean(long date, String corporation, long kills, double isk, String active, long numactive, double netisk)
  {
    this.date = date;
    this.corporation = corporation;
    this.kills = kills;
    this.isk = isk;
    this.active = active;
    this.numactive = numactive;
    this.netisk = netisk;
  }

  public ZwhAggregateBean(ZwhAggregateJPA zwhAggregateJPA)
  {
    this.date = zwhAggregateJPA.getDate();
    this.corporation = zwhAggregateJPA.getCorporation();
    this.kills = zwhAggregateJPA.getKills();
    this.isk = zwhAggregateJPA.getIsk();
    this.active = zwhAggregateJPA.getActive();
    this.numactive = zwhAggregateJPA.getNumactive();
    this.netisk = zwhAggregateJPA.getNetisk();
  }

  public long getDate()
  {
    return date;
  }

  public void setDate(long date)
  {
    this.date = date;
  }

  public String getCorporation()
  {
    return corporation;
  }

  public void setCorporation(String corporation)
  {
    this.corporation = corporation;
  }

  public long getKills()
  {
    return kills;
  }

  public void setKills(long kills)
  {
    this.kills = kills;
  }

  public double getIsk()
  {
    return isk;
  }

  public void setIsk(double isk)
  {
    this.isk = isk;
  }

  public String getActive()
  {
    return active;
  }

  public void setActive(String active)
  {
    this.active = active;
  }

  public long getNumactive()
  {
    return numactive;
  }

  public void setNumactive(long numactive)
  {
    this.numactive = numactive;
  }

  public double getNetisk()
  {
    return netisk;
  }

  public void setNetisk(double netisk)
  {
    this.netisk = netisk;
  }
}
