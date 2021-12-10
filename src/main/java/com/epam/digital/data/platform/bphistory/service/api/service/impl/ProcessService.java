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

import com.epam.digital.data.platform.bphistory.service.api.mapper.ProcessMapper;
import com.epam.digital.data.platform.bphistory.service.api.model.CountResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessInstanceStatus;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.TaskRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcess;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryTask;
import com.epam.digital.data.platform.bphistory.service.api.service.GenericService;
import com.epam.digital.data.platform.bphistory.service.api.service.JwtInfoProvider;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.security.SystemRole;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessService extends GenericService<ProcessResponse> {

  private static final Set<String> UNFINISHED_PROCESS_STATES = Set.of(
      ProcessInstanceStatus.ACTIVE.name(), ProcessInstanceStatus.SUSPENDED.name());

  private final ProcessRepository repository;
  private final TaskRepository taskRepository;
  private final ProcessMapper mapper;

  public ProcessService(ProcessRepository repository, TaskRepository taskRepository,
      JwtInfoProvider jwtInfoProvider, ProcessMapper mapper) {
    super(jwtInfoProvider);
    this.repository = repository;
    this.taskRepository = taskRepository;
    this.mapper = mapper;
  }

  @Override
  protected List<ProcessResponse> getItems(JwtClaimsDto userClaims, Pageable request) {
    log.info("Selecting user unfinished process-instances");
    var userKeycloakId = userClaims.getPreferredUsername();
    var processInstances = repository.findAllByStartUserIdAndStateInAndSuperProcessInstanceIdIsNull(
        userKeycloakId, UNFINISHED_PROCESS_STATES, request);
    log.trace("Found {} process instances", processInstances.size());

    var activeProcessInstances = getActiveProcessInstanceMap(processInstances);

    var pendingTaskList = getPendingTaskList(userKeycloakId, activeProcessInstances.keySet());
    log.trace("Found pending tasks for {} process instances", pendingTaskList.size());

    definePendingStatusForProcessInstancesWithPendingTasks(activeProcessInstances, pendingTaskList);
    log.trace("PENDING status was set for {} process instances", pendingTaskList.size());

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

  private List<BpmHistoryTask> getPendingTaskList(String userKeycloakId,
      Set<String> activeProcessInstanceIds) {
    return taskRepository.findAllByAssigneeAndRootProcessInstanceIdInAndEndTimeIsNull(
        userKeycloakId, activeProcessInstanceIds);
  }

  private Map<String, BpmHistoryProcess> getActiveProcessInstanceMap(
      List<BpmHistoryProcess> result) {
    return result.stream()
        .filter(bpmHistoryProcess -> ProcessInstanceStatus.ACTIVE.name()
            .equals(bpmHistoryProcess.getState()))
        .collect(Collectors.toMap(BpmHistoryProcess::getProcessInstanceId, Function.identity()));
  }

  private void definePendingStatusForProcessInstancesWithPendingTasks(
      Map<String, BpmHistoryProcess> processInstances,
      List<BpmHistoryTask> pendingTaskList) {
    pendingTaskList.stream()
        .map(bpmHistoryTask -> processInstances.get(bpmHistoryTask.getRootProcessInstanceId()))
        .forEach(process -> process.setState(ProcessInstanceStatus.PENDING.name()));
  }

  private SystemRole getUserSystemRole(JwtClaimsDto userClaims) {
    return userClaims.getRoles().contains(SystemRole.OFFICER.getName()) ?
        SystemRole.OFFICER : SystemRole.CITIZEN;
  }
}
