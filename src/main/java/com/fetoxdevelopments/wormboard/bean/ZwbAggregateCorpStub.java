package com.fetoxdevelopments.wormboard.bean;

public class ZwbAggregateCorpStub
{
  private long corporationid;

  private String corporation;

  private long kills;

  private long losses;

  private double iskwon;

  private double isklost;

  private long numactive;

  private long sumonkill;

  public ZwbAggregateCorpStub(long corporationid, String corporation, long kills, long losses, double iskwon, double isklost, long numactive, long sumonkill)
  {
    this.corporationid = corporationid;
    this.corporation = corporation;
    this.kills = kills;
    this.losses = losses;
    this.iskwon = iskwon;
    this.isklost = isklost;
    this.numactive = numactive;
    this.sumonkill = sumonkill;
  }

  public long getCorporationid()
  {
    return corporationid;
  }

  public void setCorporationid(long corporationid)
  {
    this.corporationid = corporationid;
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

  public long getNumactive()
  {
    return numactive;
  }

  public void setNumactive(long numactive)
  {
    this.numactive = numactive;
  }

  public long getSumonkill()
  {
    return sumonkill;
  }

  public void setSumonkill(long sumonkill)
  {
    this.sumonkill = sumonkill;
  }
}
