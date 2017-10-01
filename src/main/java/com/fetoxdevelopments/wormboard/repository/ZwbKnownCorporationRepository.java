package com.fetoxdevelopments.wormboard.repository;

import com.fetoxdevelopments.wormboard.domain.ZwbKnownCorporationJPA;
import org.springframework.data.repository.CrudRepository;

public interface ZwbKnownCorporationRepository
  extends CrudRepository<ZwbKnownCorporationJPA, Long>
{
}
