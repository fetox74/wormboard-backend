package com.fetoxdevelopments.wormboard.repository;

import java.util.List;
import java.util.Set;

import com.fetoxdevelopments.wormboard.domain.ZwbAggregateCharJPA;
import com.fetoxdevelopments.wormboard.domain.compositekeys.ZwbAggregateCharId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ZwbAggregateCharRepository
  extends CrudRepository<ZwbAggregateCharJPA, ZwbAggregateCharId>
{
  @Query("SELECT zwbAggregateChar"
         + " FROM ZwbAggregateCharJPA zwbAggregateChar"
         + " WHERE date >= :dateBegin AND date <= :dateEnd")
  List<ZwbAggregateCharJPA> findBetweenDates(@Param("dateBegin") Long dateBegin, @Param("dateEnd") Long dateEnd);

  @Query("SELECT zwbAggregateChar"
         + " FROM ZwbAggregateCharJPA zwbAggregateChar"
         + " WHERE date >= :dateBegin AND date <= :dateEnd"
         + " AND characterid IN :characterids")
  List<ZwbAggregateCharJPA> findForCharsBetweenDates(@Param("characterids") Set<Long> characterids, @Param("dateBegin") Long dateBegin,
                                                     @Param("dateEnd") Long dateEnd);
}
