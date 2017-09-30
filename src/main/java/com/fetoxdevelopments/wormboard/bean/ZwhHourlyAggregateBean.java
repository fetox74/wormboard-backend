package com.fetoxdevelopments.wormboard.bean;

public class ZwhHourlyAggregateBean
{
  private long[] kills;

  private long[] sumonkills;

  private double[] avgkillsperdayactive;

  private double[] avgonkills;

  public ZwhHourlyAggregateBean(long[] kills, long[] sumonkills, double[] avgkillsperdayactive, double[] avgonkills)
  {
    this.kills = kills;
    this.sumonkills = sumonkills;
    this.avgkillsperdayactive = avgkillsperdayactive;
    this.avgonkills = avgonkills;
  }

  public long[] getKills()
  {
    return kills;
  }

  public void setKills(long[] kills)
  {
    this.kills = kills;
  }

  public long[] getSumonkills()
  {
    return sumonkills;
  }

  public void setSumonkills(long[] sumonkills)
  {
    this.sumonkills = sumonkills;
  }

  public double[] getAvgkillsperdayactive()
  {
    return avgkillsperdayactive;
  }

  public void setAvgkillsperdayactive(double[] avgkillsperdayactive)
  {
    this.avgkillsperdayactive = avgkillsperdayactive;
  }

  public double[] getAvgonkills()
  {
    return avgonkills;
  }

  public void setAvgonkills(double[] avgonkills)
  {
    this.avgonkills = avgonkills;
  }
}
