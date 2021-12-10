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

package com.epam.digital.data.platform.bphistory.service.api.repository.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = BpmHistoryTask.TABLE_NAME)
public class BpmHistoryTask {

  public static final String TABLE_NAME = "bpm_history_task";

  @Id
  @NotNull
  private String activityInstanceId;
  private String taskDefinitionKey;
  private String taskDefinitionName;
  private String processInstanceId;
  private String processDefinitionId;
  private String processDefinitionKey;
  private String processDefinitionName;
  private String rootProcessInstanceId;
  @NotNull
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String assignee;
}
