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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
}
