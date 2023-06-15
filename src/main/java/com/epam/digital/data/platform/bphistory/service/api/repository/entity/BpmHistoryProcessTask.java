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
@Subselect("SELECT P.*, " +
          "CASE " +
              "WHEN P.STATE = 'ACTIVE' " +
                    "AND TASK_EXISTS THEN 'PENDING' " +
              "ELSE P.STATE " +
          "END AS PROCESS_STATE, " +
          "CASE " +
              "WHEN P.STATE = 'EXTERNALLY_TERMINATED' THEN '01' " +
              "WHEN P.STATE = 'COMPLETED' THEN '02' " +
              "WHEN P.STATE = 'PENDING' THEN '03' " +
              "WHEN P.STATE = 'ACTIVE' " +
                      "AND TASK_EXISTS THEN '03' " +
              "WHEN P.STATE = 'SUSPENDED' THEN '04' " +
              "WHEN P.STATE = 'ACTIVE' " +
                    "AND NOT TASK_EXISTS THEN '05' " +
              "ELSE '99' " +
          "END AS STATUS_TITLE " +
          "FROM " +
             "(SELECT HP.*, " +
                    "EXISTS " +
                "(SELECT 1 " +
                "FROM BPM_HISTORY_TASK T " +
                "WHERE T.END_TIME IS NULL " +
                  "AND HP.PROCESS_INSTANCE_ID = T.ROOT_PROCESS_INSTANCE_ID " +
                  "AND T.ASSIGNEE = HP.START_USER_ID) TASK_EXISTS " +
             "FROM BPM_HISTORY_PROCESS HP) P")
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
