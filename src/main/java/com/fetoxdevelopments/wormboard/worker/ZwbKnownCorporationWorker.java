package com.fetoxdevelopments.wormboard.worker;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fetoxdevelopments.wormboard.domain.ZwbKnownCharacterJPA;
import com.fetoxdevelopments.wormboard.domain.ZwbKnownCorporationJPA;
import com.fetoxdevelopments.wormboard.repository.ZwbKnownCorporationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZwbKnownCorporationWorker
{
  @Autowired
  private ZwbKnownCorporationRepository zwbKnownCorporationRepository;

  public Set<String> getAllKnownCorporationNames()
  {
    return zwbKnownCorporationRepository.findAllCorporationNames();
  }
}
