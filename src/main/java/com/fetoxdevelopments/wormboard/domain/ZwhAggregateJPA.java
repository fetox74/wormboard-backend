package com.fetoxdevelopments.wormboard.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(ZwhAggregateId.class)
@Table(name = "\"zwhAggregate\"", schema = "public", catalog = "staticdump")
public class ZwhAggregateJPA
{
  @Id
  @Column(name = "\"date\"")
  private long date;

  @Id
  @Column(name = "\"corporation\"")
  private String corporation;

  @Basic
  @Column(name = "\"kills\"")
  private long kills;

  @Basic
  @Column(name = "\"isk\"")
  private double isk;

  @Basic
  @Column(name = "\"active\"")
  private String active;

  @Basic
  @Column(name = "\"numactive\"")
  private long numactive;

  @Basic
  @Column(name = "\"netisk\"")
  private double netisk;

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
