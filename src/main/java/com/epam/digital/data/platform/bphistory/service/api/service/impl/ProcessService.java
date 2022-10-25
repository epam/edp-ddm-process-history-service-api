/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.bphistory.service.api.service.impl;

import com.epam.digital.data.platform.bphistory.service.api.mapper.ProcessTaskMapper;
import com.epam.digital.data.platform.bphistory.service.api.model.CountResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessInstanceStatus;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessTaskRepository;
import com.epam.digital.data.platform.bphistory.service.api.service.GenericService;
import com.epam.digital.data.platform.bphistory.service.api.service.JwtInfoProvider;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.security.SystemRole;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ProcessService extends GenericService<ProcessResponse> {

  private static final Set<String> UNFINISHED_PROCESS_STATES = Set.of(
      ProcessInstanceStatus.ACTIVE.name(), ProcessInstanceStatus.SUSPENDED.name());

  private final ProcessTaskRepository repository;
  private final ProcessTaskMapper mapper;

  public ProcessService(ProcessTaskRepository repository,
      JwtInfoProvider jwtInfoProvider, ProcessTaskMapper mapper) {
    super(jwtInfoProvider);
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  protected List<ProcessResponse> getItems(JwtClaimsDto userClaims, Pageable request) {
    log.info("Selecting user unfinished process-instances");
    var userKeycloakId = userClaims.getPreferredUsername();
    var processInstances = repository.findAllByStartUserIdAndStateInAndSuperProcessInstanceIdIsNull(
        userKeycloakId, UNFINISHED_PROCESS_STATES, request);
    log.trace("Found {} process instances", processInstances.size());

    var userSystemRole = getUserSystemRole(userClaims);

    log.info("Found {} process-instances for user", processInstances.size());
    return mapper.toModelList(processInstances, userSystemRole);
  }

  public CountResponse count(SecurityContext securityContext) {
    log.info("Counting active process instances");
    var userClaims = jwtInfoProvider.getUserClaims(securityContext.getAccessToken());
    var userKeycloakId = userClaims.getPreferredUsername();
    var count = repository.countByStartUserIdAndStateIn(userKeycloakId, UNFINISHED_PROCESS_STATES);
    log.info("Counted {} active process instances", count);
    return CountResponse.builder().count(count).build();
  }

  private SystemRole getUserSystemRole(JwtClaimsDto userClaims) {
    return userClaims.getRoles().contains(SystemRole.OFFICER.getName()) ?
        SystemRole.OFFICER : SystemRole.CITIZEN;
  }
}
