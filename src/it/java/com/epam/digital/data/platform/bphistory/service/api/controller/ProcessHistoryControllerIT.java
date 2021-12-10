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

import static com.epam.digital.data.platform.bphistory.service.api.TestUtils.OFFICER_TOKEN;
import static com.epam.digital.data.platform.bphistory.service.api.util.Header.X_ACCESS_TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.digital.data.platform.bphistory.service.api.BaseIT;
import com.epam.digital.data.platform.bphistory.service.api.TestUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ProcessHistoryControllerIT extends BaseIT {

  @Test
  void getHistoryProcessInstances() throws Exception {

    createBpmHistoryProcessAndSaveToDatabase("id", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "COMPLETED", "testuser", null);

    var expectedJson = TestUtils.readClassPathResource(
        "/json/getHistoryProcessInstancesExpectedResponse.json");

    mockMvc.perform(get("/api/history/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void getTasks() throws Exception {

    createBpmHistoryProcessAndSaveToDatabase("processInstanceId", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "COMPLETED", "testUser", "businessKey");

    createBpmHistoryTaskAndSaveToDatabase("unCompletedTask", "processInstanceId",
        LocalDateTime.of(2022, 1, 10, 11, 42), null, "testuser");
    createBpmHistoryTaskAndSaveToDatabase("completedTask", "processInstanceId",
        LocalDateTime.of(2022, 1, 10, 11, 42),
        LocalDateTime.of(2022, 1, 10, 11, 43), "testuser");

    var expectedJson = TestUtils.readClassPathResource(
        "/json/getHistoryTasksExpectedResponse.json");

    mockMvc.perform(get("/api/history/tasks")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().json(expectedJson));
  }
}
