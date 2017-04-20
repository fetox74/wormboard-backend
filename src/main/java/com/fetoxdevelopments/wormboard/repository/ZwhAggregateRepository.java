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

  @Query("SELECT zwhAggregate"
         + " FROM ZwhAggregateJPA zwhAggregate"
         + " WHERE date >= :dateBegin AND date <= :dateEnd")
  List<ZwhAggregateJPA> findBetweenDates(@Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);
}
