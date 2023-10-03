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
import com.epam.digital.data.platform.bphistory.service.api.audit.AuditableController;
import com.epam.digital.data.platform.bphistory.service.api.model.DetailedErrorResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.HistoryProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.HistoryTaskResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.HistoryProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.bphistory.service.api.util.RequestParamHelper;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.security.annotation.PreAuthorizeAnySystemRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(description = "Business processes history management Rest API", name = "process-history-service-api")
@RestController
@RequestMapping("/api/history")
@PreAuthorizeAnySystemRole
public class ProcessHistoryController {

  private final HistoryProcessService historyProcessService;
  private final TaskService taskService;

  private final RequestParamHelper requestParamHelper;

  public ProcessHistoryController(HistoryProcessService historyProcessService, TaskService taskService,
                                  RequestParamHelper requestParamHelper) {
    this.historyProcessService = historyProcessService;
    this.taskService = taskService;
    this.requestParamHelper = requestParamHelper;
  }

  @Operation(
      summary = "Get a list of historical data of tasks",
      description = "### Endpoint assignment: \n This endpoint is used to retrieve a list of historical data of tasks based on specified filtering criteria, including offset, constraint, and sorting parameters.",
      parameters = {
          @Parameter(
              name = "X-Access-Token",
              description = "User access token",
              in = ParameterIn.HEADER,
              schema = @Schema(type = "string")
          ),
          @Parameter(
              name = "offset",
              description = "Record offset",
              in = ParameterIn.QUERY,
              required = true,
              schema = @Schema(type = "integer", defaultValue = "0")
          ),
          @Parameter(
              name = "limit",
              description = "Maximum number of records to return",
              in = ParameterIn.QUERY,
              required = true,
              schema = @Schema(type = "integer", defaultValue = "10")
          ),
          @Parameter(
              name = "sort",
              description = "Field and order for sorting the records. Example: asc(<field>) / desc(<field>)",
              in = ParameterIn.QUERY,
              required = true,
              schema = @Schema(type = "string", defaultValue = "desc(endTime)")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "OK. List of historical tasks data successfully retrieved.",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @ArraySchema(schema = @Schema(implementation = ProcessResponse.class)),
                  examples = @ExampleObject(
                      value = "[\n" +
                          "    {\n" +
                          "        \"activityInstanceId\": \"10001\",\n" +
                          "        \"taskDefinitionKey\": \"task1\",\n" +
                          "        \"taskDefinitionName\": \"First task\",\n" +
                          "        \"processInstanceId\": \"1234\",\n" +
                          "        \"processDefinitionId\": \"91011\",\n" +
                          "        \"processDefinitionKey\": \"myProcess\",\n" +
                          "        \"processDefinitionName\": \"My Process\",\n" +
                          "        \"startTime\": \"2021-04-01T09:00:00Z\",\n" +
                          "        \"endTime\": \"2021-04-01T12:00:00Z\",\n" +
                          "        \"assignee\": \"john.doe\"\n" +
                          "    }\n" +
                          "]"
                  )
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad Request. Invalid excerpt type or incorrect request parameters.",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
          ),
          @ApiResponse(
              responseCode = "401",
              description = "Unauthorized. Missing or invalid access token or digital signature.",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal Server Error. Server error while processing the request.",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = DetailedErrorResponse.class))
          ),
      }
  )
  @AuditableController
  @GetMapping("/tasks")
  public ResponseEntity<List<HistoryTaskResponse>> getTasks(
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "desc(endTime)") String sort,
      @HttpSecurityContext SecurityContext securityContext) {
    var page = requestParamHelper.getPageRequest(limit, offset, sort);

    return ResponseEntity.ok(taskService.getItems(page, securityContext));
  }

  @Operation(
      summary = "Get a list of historical data of processes",
      description = "### Endpoint assignment: \n This endpoint is used to retrieve a list of historical data of processes based on specified filtering criteria, including offset, constraint, and sorting parameters.",
      parameters = {
          @Parameter(
              name = "X-Access-Token",
              description = "User access token",
              in = ParameterIn.HEADER,
              schema = @Schema(type = "string")
          ),
          @Parameter(
              name = "offset",
              description = "Record offset",
              in = ParameterIn.QUERY,
              required = true,
              schema = @Schema(type = "integer", defaultValue = "0")
          ),
          @Parameter(
              name = "limit",
              description = "Maximum number of records to return",
              in = ParameterIn.QUERY,
              required = true,
              schema = @Schema(type = "integer", defaultValue = "10")
          ),
          @Parameter(
              name = "sort",
              description = "Field and order for sorting the records. Example: asc(<field>) / desc(<field>)",
              in = ParameterIn.QUERY,
              required = true,
              schema = @Schema(type = "string", defaultValue = "desc(endTime)")
          )
      },
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "OK. List of historical process data successfully retrieved.",
              content = @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  array = @ArraySchema(schema = @Schema(implementation = ProcessResponse.class)),
                  examples = @ExampleObject(
                      value = "[\n" +
                          "    {\n" +
                          "      \"processInstanceId\":  \"1234\",\n" +
                          "      \"superProcessInstanceId\": \"5678\",\n" +
                          "      \"processDefinitionId\": \"91011\",\n" +
                          "      \"processDefinitionKey\": \"myProcess\",\n" +
                          "      \"processDefinitionName\": \"My Process\",\n" +
                          "      \"businessKey\": \"1234-5678\",\n" +
                          "      \"startTime\": \"2021-01-01T00:00:00Z\",\n" +
                          "      \"endTime\": \"2021-01-01T00:01:00Z\",\n" +
                          "      \"startUserId\": \"john.doe\",\n" +
                          "      \"excerptId\": \"4321\",\n" +
                          "      \"status\": {\n" +
                          "        \"code\": \"COMPLETED\",\n" +
                          "        \"title\": \"COMPLETED\"\n" +
                          "      }\n" +
                          "    }\n" +
                          "]"
                  )
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Bad Request. Invalid excerpt type or incorrect request parameters.",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
          ),
          @ApiResponse(
              responseCode = "401",
              description = "Unauthorized. Missing or invalid access token or digital signature.",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal Server Error. Server error while processing the request.",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = DetailedErrorResponse.class))
          ),
      }
  )
  @AuditableController
  @GetMapping("/process-instances")
  public ResponseEntity<List<HistoryProcessResponse>> getProcesses(
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "desc(endTime)") String sort,
      @HttpSecurityContext SecurityContext securityContext) {
    var page = requestParamHelper.getPageRequestSortedByStatusAndStartTime(limit, offset, sort);

    return ResponseEntity.ok(historyProcessService.getItems(page, securityContext));
  }
}
