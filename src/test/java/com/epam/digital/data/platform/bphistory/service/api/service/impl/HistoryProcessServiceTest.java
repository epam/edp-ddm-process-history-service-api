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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.bphistory.service.api.mapper.HistoryProcessMapper;
import com.epam.digital.data.platform.bphistory.service.api.model.LimitOffsetPageRequest;
import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcess;
import com.epam.digital.data.platform.bphistory.service.api.service.JwtInfoProvider;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.localization.MessageResolver;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class HistoryProcessServiceTest {

  private static final String userId = "331e7654-e89b-12d3-a456-426655441111";
  private static final String processInstanceId = "123e4567-e89b-12d3-a456-426655440001";

  private HistoryProcessService service;

  @Mock
  private ProcessRepository repository;
  @Mock
  private MessageResolver messageResolver;
  @Mock
  private JwtInfoProvider jwtInfoProvider;
  @InjectMocks
  private HistoryProcessMapper mapper = Mappers.getMapper(HistoryProcessMapper.class);

  @BeforeEach
  public void init() {
    service = new HistoryProcessService(repository, jwtInfoProvider, mapper);

    var mockClaims = new JwtClaimsDto();
    mockClaims.setPreferredUsername(userId);
    when(jwtInfoProvider.getUserClaims(any())).thenReturn(mockClaims);
  }

  @Test
  void testGetItems() {
    var process = new BpmHistoryProcess();
    process.setProcessInstanceId(processInstanceId);

    var pageRequest = new LimitOffsetPageRequest(1, 10, Sort.by(Direction.ASC, "endTime"));

    when(repository.findAllByStartUserIdAndStateInAndSuperProcessInstanceIdIsNull(userId,
        Set.of("COMPLETED", "EXTERNALLY_TERMINATED"), pageRequest)).thenReturn(List.of(process));

    var result = service.getItems(pageRequest, new SecurityContext());
    var actual = result.get(0);

    assertThat(actual.getProcessInstanceId()).isEqualTo(process.getProcessInstanceId());
  }
}