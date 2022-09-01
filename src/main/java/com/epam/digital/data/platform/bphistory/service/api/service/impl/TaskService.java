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

import com.epam.digital.data.platform.bphistory.service.api.mapper.HistoryTaskMapper;
import com.epam.digital.data.platform.bphistory.service.api.model.HistoryTaskResponse;
import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.TaskRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcess;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryTask;
import com.epam.digital.data.platform.bphistory.service.api.service.GenericService;
import com.epam.digital.data.platform.bphistory.service.api.service.JwtInfoProvider;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskService extends GenericService<HistoryTaskResponse> {

  private final TaskRepository repository;
  private final ProcessRepository processRepository;
  private final HistoryTaskMapper mapper;

  public TaskService(TaskRepository repository, ProcessRepository processRepository,
      JwtInfoProvider jwtInfoProvider, HistoryTaskMapper mapper) {
    super(jwtInfoProvider);
    this.repository = repository;
    this.processRepository = processRepository;
    this.mapper = mapper;
  }

  @Override
  protected List<HistoryTaskResponse> getItems(JwtClaimsDto userClaims, Pageable request) {
    log.info("Selecting user finished tasks");
    var userKeycloakId = userClaims.getPreferredUsername();

    var bpmHistoryTasks = repository.findAllByAssigneeAndEndTimeIsNotNull(userKeycloakId, request);
    log.trace("Found {} tasks", bpmHistoryTasks.size());

    var businessKeys = getProcessInstanceBusinessKeys(bpmHistoryTasks);
    log.trace("Found {} process instance business keys", businessKeys.size());

    log.info("Found {} finished tasks for user", bpmHistoryTasks.size());
    return mapper.toResponseList(bpmHistoryTasks, businessKeys);
  }

  private Map<String, String> getProcessInstanceBusinessKeys(List<BpmHistoryTask> bpmHistoryTasks) {
    var processInstanceIds = bpmHistoryTasks.stream()
        .map(BpmHistoryTask::getRootProcessInstanceId)
        .collect(Collectors.toSet());

    return processRepository.findAllByProcessInstanceIdIn(processInstanceIds).stream()
        .filter(bpmHistoryProcess -> Objects.nonNull(bpmHistoryProcess.getBusinessKey()))
        .collect(Collectors.toMap(BpmHistoryProcess::getProcessInstanceId,
            BpmHistoryProcess::getBusinessKey));
  }
}
