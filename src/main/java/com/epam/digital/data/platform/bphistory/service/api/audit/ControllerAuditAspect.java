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

import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAuditAspect {

  private static final String READ = "READ ENTITY";

  private final AuditEventProcessor processor;

  public ControllerAuditAspect(AuditEventProcessor auditEventProcessor) {
    this.processor = auditEventProcessor;
  }

  @Pointcut("@annotation(com.epam.digital.data.platform.bphistory.service.api.audit.AuditableException)")
  public void exceptionHandlerPointcut() {
  }

  @Pointcut("execution(public * com.epam.digital.data.platform.starter.security.jwt.TokenParser.parseClaims(..))")
  public void jwtParsingPointcut() {
  }

  @Pointcut("@annotation(com.epam.digital.data.platform.bphistory.service.api.audit.AuditableController)")
  public void controllerPointcut() {
  }

  @AfterReturning(pointcut = "exceptionHandlerPointcut()", returning = "response")
  void exceptionAudit(ResponseEntity<?> response) {
    processor.prepareAndSendExceptionAudit(response);
  }

  @AfterThrowing("jwtParsingPointcut()")
  void auditInvalidJwt() {
    processor.sendInvalidAccessTokenAudit();
  }

  @Around("controllerPointcut() && args(limit, offset, sort, securityContext)")
  Object auditGetSearch(ProceedingJoinPoint joinPoint, int limit, int offset, String sort,
      SecurityContext securityContext) throws Throwable {
    return processor.prepareAndSendRestAudit(joinPoint, READ, securityContext);
  }
}
