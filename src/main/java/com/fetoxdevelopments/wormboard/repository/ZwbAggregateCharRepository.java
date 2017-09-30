package com.fetoxdevelopments.wormboard.repository;

import java.util.List;

import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCharJPA;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ZwbAggregateCharRepository
  extends CrudRepository<ZwbAggregateCharJPA, Long>
{
  @Query("SELECT zwbAggregateChar"
         + " FROM ZwbAggregateCharJPA zwbAggregateChar"
         + " WHERE date = :date")
  List<ZwbAggregateCharJPA> findByDate(@Param("date") Long date);

  @Query("SELECT DISTINCT date"
         + " FROM ZwbAggregateCharJPA zwbAggregateChar"
         + " ORDER BY date ASC")
  List<Long> findAllDates();

  @Query("SELECT zwbAggregateChar"
         + " FROM ZwbAggregateCharJPA zwbAggregateChar"
         + " WHERE date >= :dateBegin AND date <= :dateEnd")
  List<ZwbAggregateCharJPA> findBetweenDates(@Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);

  @Query("SELECT zwbAggregateChar"
         + " FROM ZwbAggregateCharJPA zwbAggregateChar"
         + " WHERE date >= :dateBegin AND date <= :dateEnd"
         + " AND character = :character")
  List<ZwbAggregateCharJPA> findForCharBetweenDates(@Param("character") String character, @Param("dateBegin") Long dateBegin,
                                                    @Param("dateEnd") Long dateEnd);
}
