package com.fetoxdevelopments.wormboard.repository;

import java.util.List;

import com.fetoxdevelopments.wormboard.domain.ZwhAggregateJPA;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZwhAggregateRepository
  extends CrudRepository<ZwhAggregateJPA, Long>
{
  @Query("SELECT zwhAggregate"
         + " FROM ZwhAggregateJPA zwhAggregate"
         + " WHERE date = :date")
  List<ZwhAggregateJPA> findByDate(@Param("date") Long date);

  @Query("SELECT DISTINCT date"
         + " FROM ZwhAggregateJPA zwhAggregate"
         + " ORDER BY date ASC")
  List<Long> findAllDates();

  @Query("SELECT zwhAggregate"
         + " FROM ZwhAggregateJPA zwhAggregate"
         + " WHERE date >= :dateBegin AND date <= :dateEnd")
  List<ZwhAggregateJPA> findBetweenDates(@Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);

  @Query("SELECT zwhAggregate"
         + " FROM ZwhAggregateJPA zwhAggregate"
         + " WHERE date >= :dateBegin AND date <= :dateEnd"
         + " AND corporation = :corporation")
  List<ZwhAggregateJPA> findForCorpBetweenDates(@Param("corporation") String corporation, @Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);
}
