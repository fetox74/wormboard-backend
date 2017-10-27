package com.fetoxdevelopments.wormboard.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fetoxdevelopments.wormboard.domain.compositekeys.ZwbAggregateCorpId;

@Entity
@IdClass(ZwbAggregateCorpId.class)
@Table(name = "\"zwbAggregateCorp\"", schema = "public", catalog = "staticdump")
public class ZwbAggregateCorpJPA
{
  @Id
  @Column(name = "\"date\"")
  private long date;

  @Id
  @Column(name = "\"corporationid\"")
  private long corporationid;

  @Basic
  @Column(name = "\"corporation\"")
  private String corporation;

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

  @Basic
  @Column(name = "\"active\"")
  private String active;

  @Basic
  @Column(name = "\"numactive\"")
  private long numactive;

  @Basic
  @Column(name = "\"sumonkills\"")
  private long sumonkills;

  @Basic
  @Column(name = "\"killsinhour00\"")
  private long killsinhour00;

  @Basic
  @Column(name = "\"killsinhour01\"")
  private long killsinhour01;

  @Basic
  @Column(name = "\"killsinhour02\"")
  private long killsinhour02;

  @Basic
  @Column(name = "\"killsinhour03\"")
  private long killsinhour03;

  @Basic
  @Column(name = "\"killsinhour04\"")
  private long killsinhour04;

  @Basic
  @Column(name = "\"killsinhour05\"")
  private long killsinhour05;

  @Basic
  @Column(name = "\"killsinhour06\"")
  private long killsinhour06;

  @Basic
  @Column(name = "\"killsinhour07\"")
  private long killsinhour07;

  @Basic
  @Column(name = "\"killsinhour08\"")
  private long killsinhour08;

  @Basic
  @Column(name = "\"killsinhour09\"")
  private long killsinhour09;

  @Basic
  @Column(name = "\"killsinhour10\"")
  private long killsinhour10;

  @Basic
  @Column(name = "\"killsinhour11\"")
  private long killsinhour11;

  @Basic
  @Column(name = "\"killsinhour12\"")
  private long killsinhour12;

  @Basic
  @Column(name = "\"killsinhour13\"")
  private long killsinhour13;

  @Basic
  @Column(name = "\"killsinhour14\"")
  private long killsinhour14;

  @Basic
  @Column(name = "\"killsinhour15\"")
  private long killsinhour15;

  @Basic
  @Column(name = "\"killsinhour16\"")
  private long killsinhour16;

  @Basic
  @Column(name = "\"killsinhour17\"")
  private long killsinhour17;

  @Basic
  @Column(name = "\"killsinhour18\"")
  private long killsinhour18;

  @Basic
  @Column(name = "\"killsinhour19\"")
  private long killsinhour19;

  @Basic
  @Column(name = "\"killsinhour20\"")
  private long killsinhour20;

  @Basic
  @Column(name = "\"killsinhour21\"")
  private long killsinhour21;

  @Basic
  @Column(name = "\"killsinhour22\"")
  private long killsinhour22;

  @Basic
  @Column(name = "\"killsinhour23\"")
  private long killsinhour23;

  @Basic
  @Column(name = "\"sumonkillsinhour00\"")
  private long sumonkillsinhour00;

  @Basic
  @Column(name = "\"sumonkillsinhour01\"")
  private long sumonkillsinhour01;

  @Basic
  @Column(name = "\"sumonkillsinhour02\"")
  private long sumonkillsinhour02;

  @Basic
  @Column(name = "\"sumonkillsinhour03\"")
  private long sumonkillsinhour03;

  @Basic
  @Column(name = "\"sumonkillsinhour04\"")
  private long sumonkillsinhour04;

  @Basic
  @Column(name = "\"sumonkillsinhour05\"")
  private long sumonkillsinhour05;

  @Basic
  @Column(name = "\"sumonkillsinhour06\"")
  private long sumonkillsinhour06;

  @Basic
  @Column(name = "\"sumonkillsinhour07\"")
  private long sumonkillsinhour07;

  @Basic
  @Column(name = "\"sumonkillsinhour08\"")
  private long sumonkillsinhour08;

  @Basic
  @Column(name = "\"sumonkillsinhour09\"")
  private long sumonkillsinhour09;

  @Basic
  @Column(name = "\"sumonkillsinhour10\"")
  private long sumonkillsinhour10;

  @Basic
  @Column(name = "\"sumonkillsinhour11\"")
  private long sumonkillsinhour11;

  @Basic
  @Column(name = "\"sumonkillsinhour12\"")
  private long sumonkillsinhour12;

  @Basic
  @Column(name = "\"sumonkillsinhour13\"")
  private long sumonkillsinhour13;

  @Basic
  @Column(name = "\"sumonkillsinhour14\"")
  private long sumonkillsinhour14;

  @Basic
  @Column(name = "\"sumonkillsinhour15\"")
  private long sumonkillsinhour15;

  @Basic
  @Column(name = "\"sumonkillsinhour16\"")
  private long sumonkillsinhour16;

  @Basic
  @Column(name = "\"sumonkillsinhour17\"")
  private long sumonkillsinhour17;

  @Basic
  @Column(name = "\"sumonkillsinhour18\"")
  private long sumonkillsinhour18;

  @Basic
  @Column(name = "\"sumonkillsinhour19\"")
  private long sumonkillsinhour19;

  @Basic
  @Column(name = "\"sumonkillsinhour20\"")
  private long sumonkillsinhour20;

  @Basic
  @Column(name = "\"sumonkillsinhour21\"")
  private long sumonkillsinhour21;

  @Basic
  @Column(name = "\"sumonkillsinhour22\"")
  private long sumonkillsinhour22;

  @Basic
  @Column(name = "\"sumonkillsinhour23\"")
  private long sumonkillsinhour23;

  public ZwbAggregateCorpJPA()
  {
  }

  public ZwbAggregateCorpJPA(long corporationid, String corporation, long kills, long losses, double iskwon, double isklost, String active, long numactive,
                             long sumonkills)
  {
    this.corporationid = corporationid;
    this.corporation = corporation;
    this.kills = kills;
    this.losses = losses;
    this.iskwon = iskwon;
    this.isklost = isklost;
    this.active = active;
    this.numactive = numactive;
    this.sumonkills = sumonkills;
  }

  public ZwbAggregateCorpJPA(long corporationid, String corporation, long kills, long losses, double iskwon, double isklost, long numactive, long sumonkills)
  {
    this.corporationid = corporationid;
    this.corporation = corporation;
    this.kills = kills;
    this.losses = losses;
    this.iskwon = iskwon;
    this.isklost = isklost;
    this.numactive = numactive;
    this.sumonkills = sumonkills;
  }

  public long getDate()
  {
    return date;
  }

  public void setDate(long date)
  {
    this.date = date;
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

  public long getSumonkills()
  {
    return sumonkills;
  }

  public void setSumonkills(long sumonkills)
  {
    this.sumonkills = sumonkills;
  }

  public long getKillsinhour00()
  {
    return killsinhour00;
  }

  public void setKillsinhour00(long killsinhour00)
  {
    this.killsinhour00 = killsinhour00;
  }

  public long getKillsinhour01()
  {
    return killsinhour01;
  }

  public void setKillsinhour01(long killsinhour01)
  {
    this.killsinhour01 = killsinhour01;
  }

  public long getKillsinhour02()
  {
    return killsinhour02;
  }

  public void setKillsinhour02(long killsinhour02)
  {
    this.killsinhour02 = killsinhour02;
  }

  public long getKillsinhour03()
  {
    return killsinhour03;
  }

  public void setKillsinhour03(long killsinhour03)
  {
    this.killsinhour03 = killsinhour03;
  }

  public long getKillsinhour04()
  {
    return killsinhour04;
  }

  public void setKillsinhour04(long killsinhour04)
  {
    this.killsinhour04 = killsinhour04;
  }

  public long getKillsinhour05()
  {
    return killsinhour05;
  }

  public void setKillsinhour05(long killsinhour05)
  {
    this.killsinhour05 = killsinhour05;
  }

  public long getKillsinhour06()
  {
    return killsinhour06;
  }

  public void setKillsinhour06(long killsinhour06)
  {
    this.killsinhour06 = killsinhour06;
  }

  public long getKillsinhour07()
  {
    return killsinhour07;
  }

  public void setKillsinhour07(long killsinhour07)
  {
    this.killsinhour07 = killsinhour07;
  }

  public long getKillsinhour08()
  {
    return killsinhour08;
  }

  public void setKillsinhour08(long killsinhour08)
  {
    this.killsinhour08 = killsinhour08;
  }

  public long getKillsinhour09()
  {
    return killsinhour09;
  }

  public void setKillsinhour09(long killsinhour09)
  {
    this.killsinhour09 = killsinhour09;
  }

  public long getKillsinhour10()
  {
    return killsinhour10;
  }

  public void setKillsinhour10(long killsinhour10)
  {
    this.killsinhour10 = killsinhour10;
  }

  public long getKillsinhour11()
  {
    return killsinhour11;
  }

  public void setKillsinhour11(long killsinhour11)
  {
    this.killsinhour11 = killsinhour11;
  }

  public long getKillsinhour12()
  {
    return killsinhour12;
  }

  public void setKillsinhour12(long killsinhour12)
  {
    this.killsinhour12 = killsinhour12;
  }

  public long getKillsinhour13()
  {
    return killsinhour13;
  }

  public void setKillsinhour13(long killsinhour13)
  {
    this.killsinhour13 = killsinhour13;
  }

  public long getKillsinhour14()
  {
    return killsinhour14;
  }

  public void setKillsinhour14(long killsinhour14)
  {
    this.killsinhour14 = killsinhour14;
  }

  public long getKillsinhour15()
  {
    return killsinhour15;
  }

  public void setKillsinhour15(long killsinhour15)
  {
    this.killsinhour15 = killsinhour15;
  }

  public long getKillsinhour16()
  {
    return killsinhour16;
  }

  public void setKillsinhour16(long killsinhour16)
  {
    this.killsinhour16 = killsinhour16;
  }

  public long getKillsinhour17()
  {
    return killsinhour17;
  }

  public void setKillsinhour17(long killsinhour17)
  {
    this.killsinhour17 = killsinhour17;
  }

  public long getKillsinhour18()
  {
    return killsinhour18;
  }

  public void setKillsinhour18(long killsinhour18)
  {
    this.killsinhour18 = killsinhour18;
  }

  public long getKillsinhour19()
  {
    return killsinhour19;
  }

  public void setKillsinhour19(long killsinhour19)
  {
    this.killsinhour19 = killsinhour19;
  }

  public long getKillsinhour20()
  {
    return killsinhour20;
  }

  public void setKillsinhour20(long killsinhour20)
  {
    this.killsinhour20 = killsinhour20;
  }

  public long getKillsinhour21()
  {
    return killsinhour21;
  }

  public void setKillsinhour21(long killsinhour21)
  {
    this.killsinhour21 = killsinhour21;
  }

  public long getKillsinhour22()
  {
    return killsinhour22;
  }

  public void setKillsinhour22(long killsinhour22)
  {
    this.killsinhour22 = killsinhour22;
  }

  public long getKillsinhour23()
  {
    return killsinhour23;
  }

  public void setKillsinhour23(long killsinhour23)
  {
    this.killsinhour23 = killsinhour23;
  }

  public long getSumonkillsinhour00()
  {
    return sumonkillsinhour00;
  }

  public void setSumonkillsinhour00(long sumonkillsinhour00)
  {
    this.sumonkillsinhour00 = sumonkillsinhour00;
  }

  public long getSumonkillsinhour01()
  {
    return sumonkillsinhour01;
  }

  public void setSumonkillsinhour01(long sumonkillsinhour01)
  {
    this.sumonkillsinhour01 = sumonkillsinhour01;
  }

  public long getSumonkillsinhour02()
  {
    return sumonkillsinhour02;
  }

  public void setSumonkillsinhour02(long sumonkillsinhour02)
  {
    this.sumonkillsinhour02 = sumonkillsinhour02;
  }

  public long getSumonkillsinhour03()
  {
    return sumonkillsinhour03;
  }

  public void setSumonkillsinhour03(long sumonkillsinhour03)
  {
    this.sumonkillsinhour03 = sumonkillsinhour03;
  }

  public long getSumonkillsinhour04()
  {
    return sumonkillsinhour04;
  }

  public void setSumonkillsinhour04(long sumonkillsinhour04)
  {
    this.sumonkillsinhour04 = sumonkillsinhour04;
  }

  public long getSumonkillsinhour05()
  {
    return sumonkillsinhour05;
  }

  public void setSumonkillsinhour05(long sumonkillsinhour05)
  {
    this.sumonkillsinhour05 = sumonkillsinhour05;
  }

  public long getSumonkillsinhour06()
  {
    return sumonkillsinhour06;
  }

  public void setSumonkillsinhour06(long sumonkillsinhour06)
  {
    this.sumonkillsinhour06 = sumonkillsinhour06;
  }

  public long getSumonkillsinhour07()
  {
    return sumonkillsinhour07;
  }

  public void setSumonkillsinhour07(long sumonkillsinhour07)
  {
    this.sumonkillsinhour07 = sumonkillsinhour07;
  }

  public long getSumonkillsinhour08()
  {
    return sumonkillsinhour08;
  }

  public void setSumonkillsinhour08(long sumonkillsinhour08)
  {
    this.sumonkillsinhour08 = sumonkillsinhour08;
  }

  public long getSumonkillsinhour09()
  {
    return sumonkillsinhour09;
  }

  public void setSumonkillsinhour09(long sumonkillsinhour09)
  {
    this.sumonkillsinhour09 = sumonkillsinhour09;
  }

  public long getSumonkillsinhour10()
  {
    return sumonkillsinhour10;
  }

  public void setSumonkillsinhour10(long sumonkillsinhour10)
  {
    this.sumonkillsinhour10 = sumonkillsinhour10;
  }

  public long getSumonkillsinhour11()
  {
    return sumonkillsinhour11;
  }

  public void setSumonkillsinhour11(long sumonkillsinhour11)
  {
    this.sumonkillsinhour11 = sumonkillsinhour11;
  }

  public long getSumonkillsinhour12()
  {
    return sumonkillsinhour12;
  }

  public void setSumonkillsinhour12(long sumonkillsinhour12)
  {
    this.sumonkillsinhour12 = sumonkillsinhour12;
  }

  public long getSumonkillsinhour13()
  {
    return sumonkillsinhour13;
  }

  public void setSumonkillsinhour13(long sumonkillsinhour13)
  {
    this.sumonkillsinhour13 = sumonkillsinhour13;
  }

  public long getSumonkillsinhour14()
  {
    return sumonkillsinhour14;
  }

  public void setSumonkillsinhour14(long sumonkillsinhour14)
  {
    this.sumonkillsinhour14 = sumonkillsinhour14;
  }

  public long getSumonkillsinhour15()
  {
    return sumonkillsinhour15;
  }

  public void setSumonkillsinhour15(long sumonkillsinhour15)
  {
    this.sumonkillsinhour15 = sumonkillsinhour15;
  }

  public long getSumonkillsinhour16()
  {
    return sumonkillsinhour16;
  }

  public void setSumonkillsinhour16(long sumonkillsinhour16)
  {
    this.sumonkillsinhour16 = sumonkillsinhour16;
  }

  public long getSumonkillsinhour17()
  {
    return sumonkillsinhour17;
  }

  public void setSumonkillsinhour17(long sumonkillsinhour17)
  {
    this.sumonkillsinhour17 = sumonkillsinhour17;
  }

  public long getSumonkillsinhour18()
  {
    return sumonkillsinhour18;
  }

  public void setSumonkillsinhour18(long sumonkillsinhour18)
  {
    this.sumonkillsinhour18 = sumonkillsinhour18;
  }

  public long getSumonkillsinhour19()
  {
    return sumonkillsinhour19;
  }

  public void setSumonkillsinhour19(long sumonkillsinhour19)
  {
    this.sumonkillsinhour19 = sumonkillsinhour19;
  }

  public long getSumonkillsinhour20()
  {
    return sumonkillsinhour20;
  }

  public void setSumonkillsinhour20(long sumonkillsinhour20)
  {
    this.sumonkillsinhour20 = sumonkillsinhour20;
  }

  public long getSumonkillsinhour21()
  {
    return sumonkillsinhour21;
  }

  public void setSumonkillsinhour21(long sumonkillsinhour21)
  {
    this.sumonkillsinhour21 = sumonkillsinhour21;
  }

  public long getSumonkillsinhour22()
  {
    return sumonkillsinhour22;
  }

  public void setSumonkillsinhour22(long sumonkillsinhour22)
  {
    this.sumonkillsinhour22 = sumonkillsinhour22;
  }

  public long getSumonkillsinhour23()
  {
    return sumonkillsinhour23;
  }

  public void setSumonkillsinhour23(long sumonkillsinhour23)
  {
    this.sumonkillsinhour23 = sumonkillsinhour23;
  }
}
