package com.fetoxdevelopments.wormboard.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fetoxdevelopments.wormboard.domain.compositekeys.ZwhAggregateCharId;

@Entity
@IdClass(ZwhAggregateCharId.class)
@Table(name = "\"zwbAggregateChar\"", schema = "public", catalog = "staticdump")
public class ZwbAggregateCharJPA
{
  @Id
  @Column(name = "\"date\"")
  private long date;

  @Id
  @Column(name = "\"character\"")
  private String character;

  @Basic
  @Column(name = "\"kills\"")
  private long kills;

  @Basic
  @Column(name = "\"losses\"")
  private long losses;

  @Basic
  @Column(name = "\"iskwon\"")
  private double iskwon;

  @Basic
  @Column(name = "\"isklost\"")
  private double isklost;

  public long getDate()
  {
    return date;
  }

  public void setDate(long date)
  {
    this.date = date;
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
}
