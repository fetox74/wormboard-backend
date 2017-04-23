package com.fetoxdevelopments.wormboard.bean;

import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;

public class ZwhAggregateBean
{
  private String corporation;

  private long kills;

  private double isk;

  private double netisk;

  private long numactive;

  private double avgperkill;

  private double iskperactive;

  private double netiskperactive;

  private double iskperavgonkill;

  private double netiskperavgonkill;

  public ZwhAggregateBean(String corporation, long kills, double isk, double netisk, long numactive, double avgperkill, double iskperactive,
                          double netiskperactive, double iskperavgonkill, double netiskperavgonkill)
  {
    this.corporation = corporation;
    this.kills = kills;
    this.isk = isk;
    this.netisk = netisk;
    this.numactive = numactive;
    this.avgperkill = avgperkill;
    this.iskperactive = iskperactive;
    this.netiskperactive = netiskperactive;
    this.iskperavgonkill = iskperavgonkill;
    this.netiskperavgonkill = netiskperavgonkill;
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

  public double getNetisk()
  {
    return netisk;
  }

  public void setNetisk(double netisk)
  {
    this.netisk = netisk;
  }

  public long getNumactive()
  {
    return numactive;
  }

  public void setNumactive(long numactive)
  {
    this.numactive = numactive;
  }

  public double getAvgperkill()
  {
    return avgperkill;
  }

  public void setAvgperkill(double avgperkill)
  {
    this.avgperkill = avgperkill;
  }

  public double getIskperactive()
  {
    return iskperactive;
  }

  public void setIskperactive(double iskperactive)
  {
    this.iskperactive = iskperactive;
  }

  public double getNetiskperactive()
  {
    return netiskperactive;
  }

  public void setNetiskperactive(double netiskperactive)
  {
    this.netiskperactive = netiskperactive;
  }

  public double getIskperavgonkill()
  {
    return iskperavgonkill;
  }

  public void setIskperavgonkill(double iskperavgonkill)
  {
    this.iskperavgonkill = iskperavgonkill;
  }

  public double getNetiskperavgonkill()
  {
    return netiskperavgonkill;
  }

  public void setNetiskperavgonkill(double netiskperavgonkill)
  {
    this.netiskperavgonkill = netiskperavgonkill;
  }
}
