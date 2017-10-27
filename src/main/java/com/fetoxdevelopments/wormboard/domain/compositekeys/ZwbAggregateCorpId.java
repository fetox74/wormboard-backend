package com.fetoxdevelopments.wormboard.domain.compositekeys;

import java.io.Serializable;
import java.util.Objects;

public class ZwbAggregateCorpId
  implements Serializable
{
  private long date;
  private long corporationid;

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
    ZwbAggregateCorpId that = (ZwbAggregateCorpId) o;
    return date == that.date &&
           corporationid == that.corporationid;
  }

  @Override
  public int hashCode()
  {

    return Objects.hash(date, corporationid);
  }
}
