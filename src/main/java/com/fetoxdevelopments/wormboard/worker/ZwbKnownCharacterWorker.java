package com.fetoxdevelopments.wormboard.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fetoxdevelopments.wormboard.domain.ZwbKnownCharacterJPA;
import com.fetoxdevelopments.wormboard.repository.ZwbKnownCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZwbKnownCharacterWorker
{
  @Autowired
  private ZwbKnownCharacterRepository zwbKnownCharacterRepository;

  public Map<String, String> getCharacterIdLookup(Set<Long> ids)
  {
    Set<ZwbKnownCharacterJPA> knownCharacterJPAS = zwbKnownCharacterRepository.findByCharacterIds(ids);

    return knownCharacterJPAS.stream().collect(Collectors.toMap(k -> k.getName(), v -> Long.toString(v.getId())));
  }
}
