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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
  void getOrderedHistoryProcessInstancesAsc() throws Exception {

    createBpmHistoryProcessAndSaveToDatabase("id2", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "COMPLETED", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id4", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "SUSPENDED", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id1", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "EXTERNALLY_TERMINATED", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id5", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "ACTIVE", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id3", "procDef",
        LocalDateTime.of(2022, 1, 10, 11, 42), "PENDING", "testuser", null);


    mockMvc.perform(get("/api/history/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN)
            .queryParam("sort", "asc(statusTitle)"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$[0].status.code", is("EXTERNALLY_TERMINATED")))
        .andExpect(jsonPath("$[1].status.code", is("COMPLETED")));
  }
  
  @Test
  void getOrderedHistoryProcessInstancesDesc() throws Exception {

    createBpmHistoryProcessAndSaveToDatabase("id2", "procDef",
            LocalDateTime.of(2022, 1, 10, 11, 42), "COMPLETED", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id4", "procDef",
            LocalDateTime.of(2022, 1, 10, 11, 42), "SUSPENDED", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id1", "procDef",
            LocalDateTime.of(2022, 1, 10, 11, 42), "EXTERNALLY_TERMINATED", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id5", "procDef",
            LocalDateTime.of(2022, 1, 10, 11, 42), "ACTIVE", "testuser", null);

    createBpmHistoryProcessAndSaveToDatabase("id3", "procDef",
            LocalDateTime.of(2022, 1, 10, 11, 42), "PENDING", "testuser", null);


    mockMvc.perform(get("/api/history/process-instances")
                    .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN)
                    .queryParam("sort", "desc(statusTitle)"))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$[0].status.code", is("COMPLETED")))
            .andExpect(jsonPath("$[1].status.code", is("EXTERNALLY_TERMINATED")));
  }

  @Test
  void shouldReturnSortedByStatusAndStartTimeDesc() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("Process1", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 37), "COMPLETED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process4", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 38), "EXTERNALLY_TERMINATED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process2", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 16), "COMPLETED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process5", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 15), "EXTERNALLY_TERMINATED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process3", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 44), "COMPLETED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process6", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 46), "EXTERNALLY_TERMINATED", "testuser", null);

    mockMvc.perform(get("/api/history/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN)
            .queryParam("sort", "desc(statusTitle)"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$[0].startTime", is("2022-01-11T12:44:00.000Z")))
        .andExpect(jsonPath("$[0].status.code", is("COMPLETED")))
        .andExpect(jsonPath("$[1].startTime", is("2022-01-11T12:37:00.000Z")))
        .andExpect(jsonPath("$[1].status.code", is("COMPLETED")))
        .andExpect(jsonPath("$[2].startTime", is("2022-01-11T12:16:00.000Z")))
        .andExpect(jsonPath("$[2].status.code", is("COMPLETED")))
        .andExpect(jsonPath("$[3].startTime", is("2022-01-11T12:46:00.000Z")))
        .andExpect(jsonPath("$[3].status.code", is("EXTERNALLY_TERMINATED")))
        .andExpect(jsonPath("$[4].startTime", is("2022-01-11T12:38:00.000Z")))
        .andExpect(jsonPath("$[4].status.code", is("EXTERNALLY_TERMINATED")))
        .andExpect(jsonPath("$[5].startTime", is("2022-01-11T12:15:00.000Z")))
        .andExpect(jsonPath("$[5].status.code", is("EXTERNALLY_TERMINATED")));
  }


  @Test
  void shouldReturnSortedByStatusAndStartTimeAsc() throws Exception {
    createBpmHistoryProcessAndSaveToDatabase("Process1", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 37), "COMPLETED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process4", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 38), "EXTERNALLY_TERMINATED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process2", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 16), "COMPLETED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process5", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 15), "EXTERNALLY_TERMINATED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process3", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 44), "COMPLETED", "testuser", null);
    createBpmHistoryProcessAndSaveToDatabase("Process6", "processDefinition",
        LocalDateTime.of(2022, 1, 11, 12, 46), "EXTERNALLY_TERMINATED", "testuser", null);

    mockMvc.perform(get("/api/history/process-instances")
            .header(X_ACCESS_TOKEN.getHeaderName(), OFFICER_TOKEN)
            .queryParam("sort", "asc(statusTitle)"))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$[0].startTime", is("2022-01-11T12:46:00.000Z")))
        .andExpect(jsonPath("$[0].status.code", is("EXTERNALLY_TERMINATED")))
        .andExpect(jsonPath("$[1].startTime", is("2022-01-11T12:38:00.000Z")))
        .andExpect(jsonPath("$[1].status.code", is("EXTERNALLY_TERMINATED")))
        .andExpect(jsonPath("$[2].startTime", is("2022-01-11T12:15:00.000Z")))
        .andExpect(jsonPath("$[2].status.code", is("EXTERNALLY_TERMINATED")))
        .andExpect(jsonPath("$[3].startTime", is("2022-01-11T12:44:00.000Z")))
        .andExpect(jsonPath("$[3].status.code", is("COMPLETED")))
        .andExpect(jsonPath("$[4].startTime", is("2022-01-11T12:37:00.000Z")))
        .andExpect(jsonPath("$[4].status.code", is("COMPLETED")))
        .andExpect(jsonPath("$[5].startTime", is("2022-01-11T12:16:00.000Z")))
        .andExpect(jsonPath("$[5].status.code", is("COMPLETED")));
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
