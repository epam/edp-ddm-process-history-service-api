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

import com.epam.digital.data.platform.bphistory.service.api.audit.RestAuditEventsFacade;
import com.epam.digital.data.platform.bphistory.service.api.model.DetailedErrorResponse;
import com.epam.digital.data.platform.bphistory.service.api.service.TraceService;
import com.epam.digital.data.platform.bphistory.service.api.util.ResponseCode;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import com.epam.digital.data.platform.starter.security.jwt.DefaultAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class UnauthorizedRequestHandler extends DefaultAuthenticationEntryPoint {

  private final TraceService traceService;
  private final ObjectMapper objectMapper;
  private final RestAuditEventsFacade restAuditEventsFacade;

  public UnauthorizedRequestHandler(
      ObjectMapper objectMapper,
      TraceService traceService,
      RestAuditEventsFacade restAuditEventsFacade) {
    super(objectMapper);
    this.traceService = traceService;
    this.objectMapper = objectMapper;
    this.restAuditEventsFacade = restAuditEventsFacade;
  }

  @Override
  @SuppressWarnings("findsecbugs:XSS_SERVLET")
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException e) throws IOException {
    restAuditEventsFacade.sendExceptionAudit(EventType.SECURITY_EVENT, ResponseCode.AUTHENTICATION_FAILED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(objectMapper.writeValueAsString(
        newDetailedResponse(ResponseCode.AUTHENTICATION_FAILED)));
  }

  private <T> DetailedErrorResponse<T> newDetailedResponse(String code) {
    var response = new DetailedErrorResponse<T>();
    response.setTraceId(traceService.getRequestId());
    response.setCode(code);
    return response;
  }
}
