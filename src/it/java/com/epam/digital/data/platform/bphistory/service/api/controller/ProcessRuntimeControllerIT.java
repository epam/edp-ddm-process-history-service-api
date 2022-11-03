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

import static com.epam.digital.data.platform.bphistory.service.api.TestUtils.CITIZEN_TOKEN;
import static com.epam.digital.data.platform.bphistory.service.api.TestUtils.OFFICER_TOKEN;
import static com.epam.digital.data.platform.bphistory.service.api.util.Header.X_ACCESS_TOKEN;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.digital.data.platform.bphistory.service.api.BaseIT;
import com.epam.digital.data.platform.bphistory.service.api.TestUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ProcessRuntimeControllerIT extends BaseIT {

  @Test
  void getOfficerRuntimeProcessInstances() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("officerActiveProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 16), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("officerPendingProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 17), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("officerSuspendedProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 18), "SUSPENDED", "testuser", null);
    createBpmHistoryTaskAndSaveToDatabase("officerCompletedTask", "officerActiveProcess",
        LocalDateTime.of(2022, 1, 11, 12, 36),
        LocalDateTime.of(2022, 1, 11, 12, 37), "testuser");
    createBpmHistoryTaskAndSaveToDatabase("officerTask", "officerPendingProcess",
        LocalDateTime.of(2022, 1, 11, 12, 17), null, "testuser");

    var expectedJson = TestUtils.readClassPathResource(
        "/json/getOfficerRuntimeProcessInstancesExpectedResponse.json");

    mockMvc.perform(get("/api/runtime/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void getCitizenRuntimeProcessInstances() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("citizenActiveProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 35), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("citizenPendingProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 36), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("citizenSuspendedProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 37), "SUSPENDED", "testuser", null);
    createBpmHistoryTaskAndSaveToDatabase("citizenCompletedTask", "citizenActiveProcess",
        LocalDateTime.of(2022, 1, 11, 12, 36),
        LocalDateTime.of(2022, 1, 11, 12, 37), "testuser");
    createBpmHistoryTaskAndSaveToDatabase("citizenTask", "citizenPendingProcess",
        LocalDateTime.of(2022, 1, 11, 12, 36), null, "testuser");

    var expectedJson = TestUtils.readClassPathResource(
        "/json/getCitizenRuntimeProcessInstancesExpectedResponse.json");

    mockMvc.perform(get("/api/runtime/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), CITIZEN_TOKEN))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void countCitizenActiveProcessInstances() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("citizenActiveProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 35), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("citizenPendingProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 36), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("citizenSuspendedProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 37), "SUSPENDED", "testuser", null);

    var expectedJson = TestUtils.readClassPathResource(
        "/json/countCitizenActiveProcessInstances.json");

    mockMvc.perform(get("/api/runtime/process-instances/count")
            .header(X_ACCESS_TOKEN.getHeaderName(), CITIZEN_TOKEN))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void countOfficerActiveProcessInstances() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("officerActiveProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 16), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("officerPendingProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 17), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("officerSuspendedProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 18), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("citizenSuspendedProcess", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 37), "SUSPENDED", "testuser", null);

    var expectedJson = TestUtils.readClassPathResource(
        "/json/countOfficerActiveProcessInstances.json");

    mockMvc.perform(get("/api/runtime/process-instances/count")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldReturnSortedByStatusAndStartTime() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("Process1", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 37), "SUSPENDED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process4", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 38), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process2", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 16), "SUSPENDED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process5", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 15), "ACTIVE", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process3", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 44), "SUSPENDED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process6", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 46), "ACTIVE", "testuser", null);

    mockMvc.perform(get("/api/runtime/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN)
            .queryParam("sort", "asc(statusTitle)"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$[0].startTime", is("2022-01-11T12:44:00.000Z")))
        .andExpect(jsonPath("$[0].status.code", is("SUSPENDED")))
        .andExpect(jsonPath("$[1].startTime", is("2022-01-11T12:37:00.000Z")))
        .andExpect(jsonPath("$[1].status.code", is("SUSPENDED")))
        .andExpect(jsonPath("$[2].startTime", is("2022-01-11T12:16:00.000Z")))
        .andExpect(jsonPath("$[2].status.code", is("SUSPENDED")))
        .andExpect(jsonPath("$[3].startTime", is("2022-01-11T12:46:00.000Z")))
        .andExpect(jsonPath("$[3].status.code", is("ACTIVE")))
        .andExpect(jsonPath("$[4].startTime", is("2022-01-11T12:38:00.000Z")))
        .andExpect(jsonPath("$[4].status.code", is("ACTIVE")))
        .andExpect(jsonPath("$[5].startTime", is("2022-01-11T12:15:00.000Z")))
        .andExpect(jsonPath("$[5].status.code", is("ACTIVE")));
  }
}
