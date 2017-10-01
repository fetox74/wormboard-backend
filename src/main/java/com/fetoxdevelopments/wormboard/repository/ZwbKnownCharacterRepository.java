package com.fetoxdevelopments.wormboard.repository;

import java.util.Set;

import com.fetoxdevelopments.wormboard.domain.ZwbKnownCharacterJPA;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ZwbKnownCharacterRepository
  extends CrudRepository<ZwbKnownCharacterJPA, Long>
{
  @Query("SELECT zwbKnownCharacterJPA"
         + " FROM ZwbKnownCharacterJPA zwbKnownCharacterJPA"
         + " WHERE id IN :ids")
  Set<ZwbKnownCharacterJPA> findByCharacterIds(@Param("ids") Set<Long> ids);
}
