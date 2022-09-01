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

package com.epam.digital.data.platform.bphistory.service.api.config;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.digital.data.platform.bphistory.service.api.audit.RestAuditEventsFacade;
import com.epam.digital.data.platform.bphistory.service.api.controller.ProcessHistoryController;
import com.epam.digital.data.platform.bphistory.service.api.service.TraceService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.HistoryProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.bphistory.service.api.util.ResponseCode;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import com.epam.digital.data.platform.starter.security.WebSecurityConfig;
import com.epam.digital.data.platform.starter.security.jwt.DefaultAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(TestBeansConfig.class)
@ContextConfiguration(
    classes = {ProcessHistoryController.class, UnauthorizedRequestHandler.class, WebSecurityConfig.class})
@ComponentScan(
    basePackages = {"com.epam.digital.data.platform.starter.security"},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = DefaultAuthenticationEntryPoint.class)
    })
class UnauthorizedRequestHandlerTest {

  private static final String BASE_URL = "/api/history/tasks";
  private static final String TRACE_ID = "1";
  private static final UUID ENTITY_ID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private TaskService taskService;
  @MockBean
  private HistoryProcessService historyProcessService;
  @MockBean
  private TraceService traceService;
  @MockBean
  private RestAuditEventsFacade restAuditEventsFacade;

  @BeforeEach
  void beforeEach() {
    when(traceService.getRequestId()).thenReturn(TRACE_ID);
  }

  @Test
  void shouldReturnResponseWithUnauthorizedCodeIfNoJwtProvided() throws Exception {
    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isUnauthorized())
        .andExpect(
            matchAll(
                jsonPath("$.traceId").value(is(TRACE_ID)),
                jsonPath("$.code").value(Matchers.is(ResponseCode.AUTHENTICATION_FAILED)),
                jsonPath("$.details").doesNotExist()));

    verify(restAuditEventsFacade).sendExceptionAudit(EventType.SECURITY_EVENT, ResponseCode.AUTHENTICATION_FAILED);
  }
}
