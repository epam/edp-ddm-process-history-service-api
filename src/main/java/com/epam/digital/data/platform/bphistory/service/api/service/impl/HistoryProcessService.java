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

import com.epam.digital.data.platform.bphistory.service.api.mapper.HistoryProcessMapper;
import com.epam.digital.data.platform.bphistory.service.api.model.HistoryProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessInstanceStatus;
import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.service.GenericService;
import com.epam.digital.data.platform.bphistory.service.api.service.JwtInfoProvider;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HistoryProcessService extends GenericService<HistoryProcessResponse> {

  private static final Set<String> FINISHED_PROCESS_STATES = Set.of(
      ProcessInstanceStatus.COMPLETED.name(), ProcessInstanceStatus.EXTERNALLY_TERMINATED.name());

  private final ProcessRepository repository;
  private final HistoryProcessMapper mapper;

  public HistoryProcessService(ProcessRepository repository, JwtInfoProvider jwtInfoProvider,
      HistoryProcessMapper mapper) {
    super(jwtInfoProvider);
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  protected List<HistoryProcessResponse> getItems(JwtClaimsDto userClaims, Pageable request) {
    var userKeycloakId = userClaims.getPreferredUsername();
    var result = repository.findAllByStartUserIdAndStateInAndSuperProcessInstanceIdIsNull(
        userKeycloakId, FINISHED_PROCESS_STATES, request);
    return mapper.toModelList(result);
  }
}
