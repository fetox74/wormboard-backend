package com.fetoxdevelopments.wormboard.domain.compositekeys;

import java.io.Serializable;
import java.util.Objects;

public class ZwbAggregateCharId
  implements Serializable
{
  private long date;
  private long characterid;

  @Override
  public boolean equals(Object o)
  {
    if(this == o)
    {
      return true;
    }
    if(o == null || getClass() != o.getClass())
    {
      return false;
    }
    ZwbAggregateCharId that = (ZwbAggregateCharId) o;
    return date == that.date &&
           characterid == that.characterid;
  }

  @Override
  public int hashCode()
  {

    return Objects.hash(date, characterid);
  }
}
