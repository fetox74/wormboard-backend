package com.fetoxdevelopments.wormboard.repository;

import java.util.Set;

import com.fetoxdevelopments.wormboard.domain.ZwbKnownCorporationJPA;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ZwbKnownCorporationRepository
  extends CrudRepository<ZwbKnownCorporationJPA, Long>
{
  @Query("SELECT zwbKnownCorporationJPA.name"
         + " FROM ZwbKnownCorporationJPA zwbKnownCorporationJPA")
  Set<String> findAllCorporationNames();
}
