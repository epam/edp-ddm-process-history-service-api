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
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;


@Data
@Entity
@Table(name = BpmHistoryProcess.TABLE_NAME)
@Immutable
@Subselect("SELECT t.*, " +
        "CASE " +
        " WHEN t.state = 'EXTERNALLY_TERMINATED' THEN '01' " +
        " WHEN t.state = 'COMPLETED' THEN '02' " +
        " WHEN t.state = 'PENDING' THEN '03' " +
        " WHEN t.state = 'SUSPENDED' THEN '04' " +
        " WHEN t.state = 'ACTIVE' THEN '05' " +
        " ELSE '99' " +
        "END AS status_title  " +
        " FROM " + BpmHistoryProcess.TABLE_NAME + " t"
)
public class BpmHistoryProcess {

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
  private String excerptId;
  private String completionResult;
  private String statusTitle;
}
