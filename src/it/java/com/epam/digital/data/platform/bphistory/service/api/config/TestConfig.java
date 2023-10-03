/*
 * Copyright 2023 EPAM Systems.
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

package com.epam.digital.data.platform.bphistory.service.api.config;

import com.epam.digital.data.platform.bphistory.service.api.UserProcessHistoryServiceApiApplication;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.HistoryProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.ProcessService;
import com.epam.digital.data.platform.bphistory.service.api.service.impl.TaskService;
import com.epam.digital.data.platform.bphistory.service.api.util.RequestParamHelper;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@ConditionalOnMissingBean(UserProcessHistoryServiceApiApplication.class)
public class TestConfig {

  @Bean
  public HistoryProcessService testHistoryProcessService() {
    return Mockito.mock(HistoryProcessService.class);
  }

  @Bean
  public TaskService testTaskService() {
    return Mockito.mock(TaskService.class);
  }
  @Bean
  public ProcessService testProcessService() {
    return Mockito.mock(ProcessService.class);
  }
  @Bean
  public RequestParamHelper testRequestParamHelper() {
    return Mockito.mock(RequestParamHelper.class);
  }
}
