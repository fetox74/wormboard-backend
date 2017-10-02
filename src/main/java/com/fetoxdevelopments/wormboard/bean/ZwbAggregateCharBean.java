package com.fetoxdevelopments.wormboard.bean;

public class ZwbAggregateCharBean
{
  private long characterid;

  private String character;

  private long kills;

  private long losses;

  private double kdratio;

  private double kdefficiency;

  private double iskwon;

  private double isklost;

  private double netisk;

  private double iskefficiency;

  public ZwbAggregateCharBean(long characterid, String character, long kills, long losses, double kdratio, double kdefficiency, double iskwon, double isklost,
                              double netisk, double iskefficiency)
  {
    this.characterid = characterid;
    this.character = character;
    this.kills = kills;
    this.losses = losses;
    this.kdratio = kdratio;
    this.kdefficiency = kdefficiency;
    this.iskwon = iskwon;
    this.isklost = isklost;
    this.netisk = netisk;
    this.iskefficiency = iskefficiency;
  }

  public long getCharacterid()
  {
    return characterid;
  }

  public void setCharacterid(long characterid)
  {
    this.characterid = characterid;
  }

  public String getCharacter()
  {
    return character;
  }

  public void setCharacter(String character)
  {
    this.character = character;
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

  public double getKdefficiency()
  {
    return kdefficiency;
  }

  public void setKdefficiency(double kdefficiency)
  {
    this.kdefficiency = kdefficiency;
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

  public double getIskefficiency()
  {
    return iskefficiency;
  }

  public void setIskefficiency(double iskefficiency)
  {
    this.iskefficiency = iskefficiency;
  }
}
