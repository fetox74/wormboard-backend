package com.fetoxdevelopments.wormboard.repository;

import java.util.List;

import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCorpJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZwbAggregateCorpRepository
  extends CrudRepository<ZwbAggregateCorpJPA, Long>
{
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
  List<ZwbAggregateCorpJPA> findBetweenDates(@Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);

  @Query("SELECT zwbAggregateCorp"
         + " FROM ZwbAggregateCorpJPA zwbAggregateCorp"
         + " WHERE date >= :dateBegin AND date <= :dateEnd"
         + " AND corporation = :corporation")
  List<ZwbAggregateCorpJPA> findForCorpBetweenDates(@Param("corporation") String corporation, @Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);
}
