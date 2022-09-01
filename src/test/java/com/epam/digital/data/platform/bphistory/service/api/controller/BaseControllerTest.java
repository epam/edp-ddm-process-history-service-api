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

import com.epam.digital.data.platform.bphistory.service.api.config.TestBeansConfig;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.HistoryProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.ProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.starter.security.PermitAllWebSecurityConfig;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@TestPropertySource(properties = {"platform.security.enabled=false"})
@Import({TestBeansConfig.class, PermitAllWebSecurityConfig.class})
@ContextConfiguration
public abstract class BaseControllerTest {

  public static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
  public static final String KEY = "email@email.com";

  @Autowired
  protected MockMvc mockMvc;
  @MockBean
  protected TaskService taskService;
  @MockBean
  protected HistoryProcessService historyProcessService;
  @MockBean
  protected ProcessService processService;
}
