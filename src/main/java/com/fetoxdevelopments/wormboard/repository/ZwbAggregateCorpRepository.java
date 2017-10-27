package com.fetoxdevelopments.wormboard.repository;

import java.util.List;
import java.util.Set;

import com.fetoxdevelopments.wormboard.bean.ZwbAggregateCorpStub;
import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCorpJPA;
import com.fetoxdevelopments.wormboard.domain.compositekeys.ZwbAggregateCorpId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZwbAggregateCorpRepository
  extends CrudRepository<ZwbAggregateCorpJPA, ZwbAggregateCorpId>
{
  @Query("SELECT DISTINCT zwbAggregateCorp.corporation"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp")
  Set<String> findAllCorporationNames();

  @Query("SELECT zwbAggregateCorp"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp"
         + " WHERE date = :date")
  List<ZwbAggregateCorpJPA> findByDate(@Param("date") Long date);

  @Query("SELECT DISTINCT date"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp"
         + " ORDER BY date ASC")
  List<Long> findAllDates();

  @Query("SELECT zwbAggregateCorp"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp"
         + " WHERE date >= :dateBegin AND date <= :dateEnd")
  List<ZwbAggregateCorpJPA> findBetweenDates(@Param("dateBegin") Long dateBegin,
                                             @Param("dateEnd") Long dateEnd);

  @Query("SELECT new com.fetoxdevelopments.wormboard.domain.ZwbAggregateCorpJPA(zwbAggregateCorp.corporationid, zwbAggregateCorp.corporation,"
         + " zwbAggregateCorp.kills, zwbAggregateCorp.losses, zwbAggregateCorp.iskwon, zwbAggregateCorp.isklost, zwbAggregateCorp.active,"
         + " zwbAggregateCorp.numactive, zwbAggregateCorp.sumonkills)"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp"
         + " WHERE date >= :dateBegin AND date <= :dateEnd")
  List<ZwbAggregateCorpJPA> findBetweenDatesLight(@Param("dateBegin") Long dateBegin,
                                                  @Param("dateEnd") Long dateEnd);

  @Query("SELECT zwbAggregateCorp"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp"
         + " WHERE date >= :dateBegin AND date <= :dateEnd"
         + " AND corporationid = :corporationid")
  List<ZwbAggregateCorpJPA> findForCorpBetweenDates(@Param("corporationid") Long corporationid,
                                                    @Param("dateBegin") Long dateBegin,
                                                    @Param("dateEnd") Long dateEnd);

  @Query(value = "SELECT corporationid, corporation, sum(kills) AS kills, sum(losses) AS losses, sum(iskwon) AS iskwon, sum(isklost) AS isklost,"
               + " array_length(uniq(sort(string_to_array(string_agg(CASE WHEN \"active\" <> '' THEN \"active\" ELSE NULL END, ','), ',')\\:\\:integer[])), 1)"
               + " AS numactive, sum(sumonkills) AS sumonkills"
               + " FROM \"zwbAggregateCorp\" WHERE date >= ?1 AND date <= ?2 GROUP BY corporationid, corporation", nativeQuery = true)
  List<Object[]> aggregateBetweenDates(Long dateBegin, Long dateEnd);
}
