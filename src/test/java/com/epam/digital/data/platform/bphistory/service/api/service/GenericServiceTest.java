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

package com.epam.digital.data.platform.bphistory.service.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.bphistory.model.HistoryTask;
import com.epam.digital.data.platform.bphistory.service.api.mapper.HistoryTaskMapper;
import com.epam.digital.data.platform.bphistory.service.api.model.LimitOffsetPageRequest;
import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.TaskRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcess;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryTask;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.localization.MessageResolver;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class GenericServiceTest {

  private static final UUID USER_ID = UUID.fromString("321e7654-e89b-12d3-a456-426655441111");
  private static final UUID ACTIVITY_ID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
  private static final UUID PROCESS_ID = UUID.fromString("123e4567-e89b-12d3-a456-426655440123");

  private TaskService taskService;

  @Mock
  private JwtInfoProvider jwtInfoProvider;
  @Mock
  private TaskRepository taskRepository;
  @Mock
  private ProcessRepository processRepository;
  @Mock
  private MessageResolver messageResolver;
  @InjectMocks
  private HistoryTaskMapper mapper = Mappers.getMapper(HistoryTaskMapper.class);

  @Captor
  private ArgumentCaptor<HistoryTask> taskArgumentCaptor;

  @BeforeEach
  void beforeEach() {
    taskService = new TaskService(taskRepository, processRepository, jwtInfoProvider, mapper);

    var mockClaims = new JwtClaimsDto();
    mockClaims.setPreferredUsername(USER_ID.toString());
    when(jwtInfoProvider.getUserClaims(any())).thenReturn(mockClaims);
  }

  @Test
  void expectHistoryTaskFromDbReturnedIfExist() {
    var task = new BpmHistoryTask();
    task.setAssignee(USER_ID.toString());
    task.setActivityInstanceId(ACTIVITY_ID.toString());
    task.setRootProcessInstanceId(PROCESS_ID.toString());

    var pageRequest = new LimitOffsetPageRequest(1, 10, Sort.by(Direction.ASC, "endTime"));

    when(taskRepository.findAllByAssigneeAndEndTimeIsNotNull(USER_ID.toString(), pageRequest))
        .thenReturn(List.of(task));

    var process = new BpmHistoryProcess();
    process.setProcessInstanceId(PROCESS_ID.toString());
    process.setBusinessKey("businessKey");
    when(processRepository.findAllByProcessInstanceIdIn(Set.of(PROCESS_ID.toString())))
        .thenReturn(List.of(process));

    var result = taskService.getItems(pageRequest, new SecurityContext());
    var actual = result.get(0);

    assertThat(actual.getAssignee()).isEqualTo(task.getAssignee());
    assertThat(actual.getActivityInstanceId()).isEqualTo(task.getActivityInstanceId());
    assertThat(actual.getBusinessKey()).isEqualTo(process.getBusinessKey());
  }
}
