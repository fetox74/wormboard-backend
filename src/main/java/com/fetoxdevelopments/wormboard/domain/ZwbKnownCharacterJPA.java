package com.fetoxdevelopments.wormboard.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"zwbKnownCharacter\"", schema = "public", catalog = "staticdump")
public class ZwbKnownCharacterJPA
{
  @Id
  @Column(name = "\"id\"")
  private long id;

  @Basic
  @Column(name = "\"name\"")
  private String name;

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}
