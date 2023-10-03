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

package com.epam.digital.data.platform.bphistory.service.api;

import com.epam.digital.data.platform.bphistory.service.api.repository.ProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.TaskRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.UpdaptableProcessRepository;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryTask;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.UpdaptableBpmHistoryProcess;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UserProcessHistoryServiceApiApplication.class)
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
public abstract class BaseIT {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ProcessRepository processRepository;

  @Autowired
  protected UpdaptableProcessRepository updaptableProcessRepository;
  @Autowired
  protected TaskRepository taskRepository;

  @AfterEach
  void cleanUp() {
    updaptableProcessRepository.deleteAll();
    taskRepository.deleteAll();
  }

  protected UpdaptableBpmHistoryProcess createBpmHistoryProcessAndSaveToDatabase(String id,
                                                                                 String processDefinitionId, LocalDateTime startTime, String state, String startUserId,
                                                                                 String businessKey) {
    var processInstance = new UpdaptableBpmHistoryProcess();
    processInstance.setProcessInstanceId(id);
    processInstance.setProcessDefinitionId(processDefinitionId);
    processInstance.setStartTime(startTime);
    processInstance.setState(state);
    processInstance.setStartUserId(startUserId);
    processInstance.setBusinessKey(businessKey);
    return updaptableProcessRepository.save(processInstance);
  }

  protected BpmHistoryTask createBpmHistoryTaskAndSaveToDatabase(String id,
      String rootProcessInstanceId, LocalDateTime startTime, LocalDateTime endTime, String assignee) {
    var task = new BpmHistoryTask();
    task.setActivityInstanceId(id);
    task.setRootProcessInstanceId(rootProcessInstanceId);
    task.setStartTime(startTime);
    task.setEndTime(endTime);
    task.setAssignee(assignee);
    return taskRepository.save(task);
  }
}
