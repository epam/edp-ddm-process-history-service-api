/*
 * Copyright 2022 EPAM Systems.
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

import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = BpmHistoryProcessTask.TABLE_NAME)
@Immutable
@Subselect("SELECT DISTINCT p.*, " +
        "CASE " +
        " WHEN p.state = 'ACTIVE' AND t.root_process_instance_id IS NOT NULL THEN 'PENDING'" +
        " ELSE p.state " +
        "END AS process_state,  " +
        "CASE " +
        " WHEN p.state = 'EXTERNALLY_TERMINATED' THEN '01' " +
        " WHEN p.state = 'COMPLETED' THEN '02' " +
        " WHEN p.state = 'PENDING' THEN '03' " +
        " WHEN p.state = 'ACTIVE' AND t.root_process_instance_id IS NOT NULL THEN '03' " +
        " WHEN p.state = 'SUSPENDED' THEN '04' " +
        " WHEN p.state = 'ACTIVE' AND t.root_process_instance_id IS NULL THEN '05' " +
        " ELSE '99' " +
        "END AS status_title  " +
        "FROM bpm_history_process p " +
        "LEFT OUTER JOIN (SELECT * FROM bpm_history_task WHERE end_time IS NULL) t " +
        "ON p.process_instance_id = t.root_process_instance_id AND t.assignee = p.start_user_id ")
public class BpmHistoryProcessTask {

  public static final String TABLE_NAME = "bpm_history_process";

  @Id
  @NotNull
  private String processInstanceId;
  private String superProcessInstanceId;
  @NotNull
  private String processDefinitionId;
  private String processDefinitionKey;
  private String processDefinitionName;
  private String businessKey;
  @NotNull
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String startUserId;
  private String state;
  private String processState;
  private String excerptId;
  private String completionResult;
  private String statusTitle;
}
