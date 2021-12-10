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

package com.epam.digital.data.platform.bphistory.service.api.controller;

import com.epam.digital.data.platform.bphistory.service.api.annotation.HttpSecurityContext;
import com.epam.digital.data.platform.bphistory.service.api.model.CountResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.ProcessService;
import com.epam.digital.data.platform.bphistory.service.api.util.RequestParamHelper;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.security.annotation.PreAuthorizeAnySystemRole;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime")
@PreAuthorizeAnySystemRole
public class ProcessRuntimeController {

  private final ProcessService processService;

  private final RequestParamHelper requestParamHelper;

  public ProcessRuntimeController(ProcessService processService,
      RequestParamHelper requestParamHelper) {
    this.processService = processService;
    this.requestParamHelper = requestParamHelper;
  }

  @GetMapping("/process-instances")
  public ResponseEntity<List<ProcessResponse>> getProcesses(
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "desc(endTime)") String sort,
      @HttpSecurityContext SecurityContext securityContext) {
    var page = requestParamHelper.getPageRequest(limit, offset, sort);

    return ResponseEntity.ok(processService.getItems(page, securityContext));
  }

  @GetMapping("/process-instances/count")
  public ResponseEntity<CountResponse> count(@HttpSecurityContext SecurityContext securityContext) {
    return ResponseEntity.ok(processService.count(securityContext));
  }
}
