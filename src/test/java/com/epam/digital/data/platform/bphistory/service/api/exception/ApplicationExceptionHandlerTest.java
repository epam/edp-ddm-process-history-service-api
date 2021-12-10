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

package com.epam.digital.data.platform.bphistory.service.api.exception;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.digital.data.platform.bphistory.service.api.audit.RestAuditEventsFacade;
import com.epam.digital.data.platform.bphistory.service.api.config.TestBeansConfig;
import com.epam.digital.data.platform.bphistory.service.api.controller.ProcessHistoryController;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.HistoryProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.bphistory.service.api.service.TraceService;
import com.epam.digital.data.platform.bphistory.service.api.util.ResponseCode;
import com.epam.digital.data.platform.starter.security.PermitAllWebSecurityConfig;
import com.epam.digital.data.platform.starter.security.exception.JwtParsingException;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import com.epam.digital.data.platform.starter.security.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@WebMvcTest
@ContextConfiguration(
    classes = {ProcessHistoryController.class, ApplicationExceptionHandler.class, TokenParser.class})
@Import({TokenProvider.class, PermitAllWebSecurityConfig.class, TestBeansConfig.class})
class ApplicationExceptionHandlerTest extends ResponseEntityExceptionHandler {

  private static final String BASE_URL = "/api/history/tasks";

  private static final String TRACE_ID = "1";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private TaskService taskService;
  @MockBean
  private HistoryProcessService historyProcessService;
  @MockBean
  private RestAuditEventsFacade restAuditEventsFacade;
  @MockBean
  private TraceService traceService;

  @BeforeEach
  void beforeEach() {
    when(traceService.getRequestId()).thenReturn(TRACE_ID);
  }

  @Test
  void shouldReturnRuntimeErrorOnGenericException() throws Exception {
    when(taskService.getItems(any(), any())).thenThrow(RuntimeException.class);

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isInternalServerError())
        .andExpect(
            matchAll(
                jsonPath("$.traceId").value(is(TRACE_ID)),
                jsonPath("$.code").value(Matchers.is(ResponseCode.RUNTIME_ERROR)),
                jsonPath("$.details").doesNotExist()));
  }

  @Test
  void shouldReturnBadRequestOnHttpNotReadable() throws Exception {
    when(taskService.getItems(any(), any())).thenThrow(HttpMessageNotReadableException.class);

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isBadRequest())
        .andExpect(
            response ->
                assertTrue(
                    response.getResolvedException() instanceof HttpMessageNotReadableException));
  }

  @Test
  void shouldReturn401WhenJwtParsingException() throws Exception {
    when(taskService.getItems(any(), any())).thenThrow(JwtParsingException.class);

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(
            matchAll(
                status().isUnauthorized(),
                jsonPath("$.traceId").value(is(TRACE_ID)),
                jsonPath("$.code").value(is(ResponseCode.JWT_INVALID))));

    verify(restAuditEventsFacade).auditInvalidAccessToken();
  }

  @Test
  void shouldReturn400WhenSortParamWrongFormatException() throws Exception {
    when(taskService.getItems(any(), any()))
        .thenThrow(new SortParamWrongFormatException("sort param is wrong format"));

    mockMvc
        .perform(get(BASE_URL))
        .andExpect(
            matchAll(
                status().isBadRequest(),
                jsonPath("$.traceId").value(is(TRACE_ID)),
                jsonPath("$.code").value(is(ResponseCode.BAD_REQUEST))));
  }

  @Test
  void shouldReturn404WhenNoHandlerFoundException() throws Exception {
    mockMvc
        .perform(get("/someBadUrl"))
        .andExpect(
            matchAll(
                status().isNotFound(),
                jsonPath("$.traceId").value(is(TRACE_ID)),
                jsonPath("$.code").value(is(ResponseCode.NOT_FOUND))));
  }
}
