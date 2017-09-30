package com.fetoxdevelopments.wormboard.bean;

public class ZwbAggregateCharBean
{
  private String character;

  private String portraiturl;

  private long kills;

  private long losses;

  private double kdratio;

  private double kdefficiency;

  private double iskwon;

  private double isklost;

  private double netisk;

  private double iskefficiency;

  public ZwbAggregateCharBean(String character, String portraiturl, long kills, long losses, double kdratio, double kdefficiency, double iskwon, double isklost,
                              double netisk, double iskefficiency)
  {
    this.character = character;
    this.portraiturl = portraiturl;
    this.kills = kills;
    this.losses = losses;
    this.kdratio = kdratio;
    this.kdefficiency = kdefficiency;
    this.iskwon = iskwon;
    this.isklost = isklost;
    this.netisk = netisk;
    this.iskefficiency = iskefficiency;
  }

  public String getCharacter()
  {
    return character;
  }

  public void setCharacter(String character)
  {
    this.character = character;
  }

  public String getPortraiturl()
  {
    return portraiturl;
  }

  public void setPortraiturl(String portraiturl)
  {
    this.portraiturl = portraiturl;
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
