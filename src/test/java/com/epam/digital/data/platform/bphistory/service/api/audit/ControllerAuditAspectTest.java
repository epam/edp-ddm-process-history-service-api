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

package com.epam.digital.data.platform.bphistory.service.api.audit;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.bphistory.service.api.controller.ProcessHistoryController;
import com.epam.digital.data.platform.bphistory.service.api.exception.ApplicationExceptionHandler;
import com.epam.digital.data.platform.bphistory.service.api.model.HistoryTaskResponse;
import com.epam.digital.data.platform.bphistory.service.api.service.TraceService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.HistoryProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.bphistory.service.api.util.RequestParamHelper;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.security.exception.JwtParsingException;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(AopAutoConfiguration.class)
@SpringBootTest(classes = {
    ProcessHistoryController.class,
    AuditEventProcessor.class,
    ControllerAuditAspect.class,
    ApplicationExceptionHandler.class,
    TokenParser.class
})
@MockBean(ObjectMapper.class)
@MockBean(TraceService.class)
class ControllerAuditAspectTest {

  @Autowired
  private ProcessHistoryController controller;
  @Autowired
  private ApplicationExceptionHandler applicationExceptionHandler;
  @Autowired
  private TokenParser tokenParser;

  @MockBean
  private TaskService taskService;
  @MockBean
  private HistoryProcessService historyProcessService;
  @MockBean
  private RestAuditEventsFacade restAuditEventsFacade;

  @MockBean
  private RequestParamHelper requestParamHelper;

  @Mock
  private SecurityContext mockSecurityContext;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void expectAuditAspectBeforeAndAfterGetMethodWhenNoException() {
    when(taskService.getItems(any(), any())).thenReturn(mockSuccessResponse());

    controller.getTasks(10, 0, "asc(startTime)", mockSecurityContext);

    verify(restAuditEventsFacade, times(2))
        .sendRestAudit(any(), any(), any(), any(), any(), any());
  }

  @Test
  void expectAuditAspectOnlyBeforeWhenExceptionOnGetMethod() {
    when(taskService.getItems(any(), any())).thenThrow(new RuntimeException());

    assertThrows(
        RuntimeException.class,
        () -> controller.getTasks(10, 0, "asc(startTime)", mockSecurityContext));

    verify(restAuditEventsFacade)
        .sendRestAudit(any(), any(), any(), any(), any(), any());
  }

  @Test
  void expectAuditAspectBeforeGetAndAfterExceptionHandler(){
    applicationExceptionHandler.handleException(new RuntimeException());

    verify(restAuditEventsFacade).sendExceptionAudit(any(), any());
  }

  @Test
  void expectAuditAspectWhenExceptionWhileTokenParsing() {
    assertThrows(
        JwtParsingException.class,
        () -> tokenParser.parseClaims("incorrectToken"));

    verify(restAuditEventsFacade).auditInvalidAccessToken();
  }

  private List<HistoryTaskResponse> mockSuccessResponse() {
    return emptyList();
  }
}