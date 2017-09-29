package com.fetoxdevelopments.wormboard.bean;

/* Data being transported to frontend. The stuff that is directly used in the data grid is precalculated, the timezone info however is not, as it is only shown
   on demand and for a single corp, so it can be done in frontend without performance issues (I have no idea whether additional traffic or the actual
   calculation have more performance impact).
 */

public class ZwhAggregateBean
{
  private String corporation;

  private long kills;

  private long losses;

  private double kdratio;

  private double iskwon;

  private double isklost;

  private double netisk;

  private long numactive;

  private double avgperkill;

  private double iskperactive;

  private double netiskperactive;

  private double iskperavgonkill;

  private double netiskperavgonkill;

  public ZwhAggregateBean(String corporation, long kills, long losses, double kdratio, double iskwon, double isklost, double netisk, long numactive,
                          double avgperkill, double iskperactive, double netiskperactive, double iskperavgonkill, double netiskperavgonkill)
  {
    this.corporation = corporation;
    this.kills = kills;
    this.losses = losses;
    this.kdratio = kdratio;
    this.iskwon = iskwon;
    this.isklost = isklost;
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

  public long getLosses()
  {
    return losses;
  }

  public void setLosses(long losses)
  {
    this.losses = losses;
  }

  public double getKdratio()
  {
    return kdratio;
  }

  public void setKdratio(double kdratio)
  {
    this.kdratio = kdratio;
  }

  public double getIskwon()
  {
    return iskwon;
  }

  public void setIskwon(double iskwon)
  {
    this.iskwon = iskwon;
  }

  public double getIsklost()
  {
    return isklost;
  }

  public void setIsklost(double isklost)
  {
    this.isklost = isklost;
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
