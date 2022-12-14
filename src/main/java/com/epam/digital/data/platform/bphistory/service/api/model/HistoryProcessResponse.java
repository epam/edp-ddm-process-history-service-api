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

package com.epam.digital.data.platform.bphistory.service.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HistoryProcessResponse {

  private String processInstanceId;
  private String superProcessInstanceId;
  private String processDefinitionId;
  private String processDefinitionKey;
  private String processDefinitionName;
  private String businessKey;
  @JsonFormat(
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  )
  private LocalDateTime startTime;
  @JsonFormat(
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  )
  private LocalDateTime endTime;
  private String startUserId;
  private String excerptId;
  private StatusModel status;
}
