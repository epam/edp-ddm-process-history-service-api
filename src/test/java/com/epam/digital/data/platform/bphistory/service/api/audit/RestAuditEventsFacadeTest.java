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

import static com.epam.digital.data.platform.bphistory.service.api.audit.RestAuditEventsFacade.ACTION;
import static com.epam.digital.data.platform.bphistory.service.api.audit.RestAuditEventsFacade.CODE_JWT_INVALID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.bphistory.service.api.service.TraceService;
import com.epam.digital.data.platform.starter.audit.model.AuditEvent;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import com.epam.digital.data.platform.starter.audit.service.AuditService;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import com.epam.digital.data.platform.starter.security.jwt.TokenParser;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestAuditEventsFacadeTest {

  @Mock
  private AuditService auditService;
  @Mock
  private TraceService traceService;
  @Mock
  private TokenParser tokenParser;

  private final String appName = "testAppName";
  private final String requestId = "testRequestId";
  private final Clock clock = Clock.systemUTC();

  private RestAuditEventsFacade auditEventsFacade;

  @BeforeEach
  public void init() {
    auditEventsFacade = new RestAuditEventsFacade(auditService, appName, clock, traceService,
        tokenParser);

    when(traceService.getRequestId()).thenReturn(requestId);
  }

  @Test
  void testAuditInvalidAccessToken() {
    auditEventsFacade.auditInvalidAccessToken();

    var captor = ArgumentCaptor.forClass(AuditEvent.class);
    verify(auditService, times(1)).sendAudit(captor.capture());
    var auditEvent = captor.getValue();
    assertThat(auditEvent.getRequestId()).isEqualTo(requestId);
    assertThat(auditEvent.getApplication()).isEqualTo(appName);
    assertThat(auditEvent.getEventType()).isEqualTo(EventType.SECURITY_EVENT);
    assertThat(auditEvent.getContext().get(ACTION)).isEqualTo(CODE_JWT_INVALID);
  }

  @Test
  void testSendRestAudit() {
    var event = EventType.USER_ACTION;
    var method = "testMethod";
    var jwt = "jwt";
    var username = "testUsername";
    var jwtPayload = new JwtClaimsDto();
    jwtPayload.setFullName(username);

    when(tokenParser.parseClaims(jwt)).thenReturn(jwtPayload);

    auditEventsFacade.sendRestAudit(event, method, "action", jwt, "BEFORE", null);

    var captor = ArgumentCaptor.forClass(AuditEvent.class);
    verify(auditService, times(1)).sendAudit(captor.capture());
    var auditEvent = captor.getValue();
    assertThat(auditEvent.getRequestId()).isEqualTo(requestId);
    assertThat(auditEvent.getApplication()).isEqualTo(appName);
    assertThat(auditEvent.getUserInfo().getUserName()).isEqualTo(username);
    assertThat(auditEvent.getEventType()).isEqualTo(event);
    assertThat(auditEvent.getName()).contains(method);
  }

  @Test
  void testSendExceptionAudit() {
    var action = "action";
    var event = EventType.USER_ACTION;

    auditEventsFacade.sendExceptionAudit(event, action);

    var captor = ArgumentCaptor.forClass(AuditEvent.class);
    verify(auditService, times(1)).sendAudit(captor.capture());
    var auditEvent = captor.getValue();
    assertThat(auditEvent.getRequestId()).isEqualTo(requestId);
    assertThat(auditEvent.getApplication()).isEqualTo(appName);
    assertThat(auditEvent.getEventType()).isEqualTo(event);
  }
}