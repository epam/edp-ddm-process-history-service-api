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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epam.digital.data.platform.bphistory.service.api.model.ProcessResponse;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ProcessRuntimeControllerTest extends BaseControllerTest {

  private static final String BASE_URL = "/api/runtime";

  @Test
  void expectListOfProcessesWhenSuccessfulRequest() throws Exception {
    var response = new ArrayList<ProcessResponse>();

    var process = new ProcessResponse();
    process.setStartUserId(USER_ID.toString());
    process.setBusinessKey(KEY);
    response.add(process);

    when(processService.getItems(any(), any())).thenReturn(response);

    mockMvc
        .perform(get(BASE_URL + "/process-instances"))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$[0].startUserId", is(USER_ID.toString())),
            jsonPath("$[0].businessKey", is(KEY)));
  }
}
