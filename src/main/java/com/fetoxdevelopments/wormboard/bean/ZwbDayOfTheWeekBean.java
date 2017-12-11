package com.fetoxdevelopments.wormboard.bean;

public class ZwbDayOfTheWeekBean
{
  private long[] kills;

  private long[] sumonkills;

  private double[] avgkillsperdayactive;

  private double[] avgonkills;

  private int numdaysactive;

  public ZwbDayOfTheWeekBean(long[] kills, long[] sumonkills, double[] avgkillsperdayactive, double[] avgonkills, int numdaysactive)
  {
    this.kills = kills;
    this.sumonkills = sumonkills;
    this.avgkillsperdayactive = avgkillsperdayactive;
    this.avgonkills = avgonkills;
    this.numdaysactive = numdaysactive;
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

  public int getNumdaysactive()
  {
    return numdaysactive;
  }

  public void setNumdaysactive(int numdaysactive)
  {
    this.numdaysactive = numdaysactive;
  }
}
