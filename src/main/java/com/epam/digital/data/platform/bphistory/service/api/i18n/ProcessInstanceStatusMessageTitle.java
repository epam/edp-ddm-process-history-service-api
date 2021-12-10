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

package com.epam.digital.data.platform.bphistory.service.api.i18n;

import com.epam.digital.data.platform.bphistory.service.api.model.ProcessInstanceStatus;
import com.epam.digital.data.platform.starter.localization.MessageTitle;
import com.epam.digital.data.platform.starter.security.SystemRole;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a list of possible statuses in a business process instance. The process can change
 * status at runtime.
 */
@Getter
@RequiredArgsConstructor
public enum ProcessInstanceStatusMessageTitle implements MessageTitle {
  PENDING(ProcessInstanceStatus.PENDING, SystemRole.OFFICER,
      "process-instance.status.title.pending"),
  SUSPENDED(ProcessInstanceStatus.SUSPENDED, SystemRole.OFFICER,
      "process-instance.status.title.suspended"),
  IN_PROGRESS(ProcessInstanceStatus.ACTIVE, SystemRole.OFFICER,
      "process-instance.status.title.in-progress"),

  CITIZEN_PENDING(ProcessInstanceStatus.PENDING, SystemRole.CITIZEN,
      "process-instance.status.title.citizen-pending"),
  CITIZEN_SUSPENDED(ProcessInstanceStatus.SUSPENDED, SystemRole.CITIZEN,
      "process-instance.status.title.citizen-suspended"),
  CITIZEN_IN_PROGRESS(ProcessInstanceStatus.ACTIVE, SystemRole.CITIZEN,
      "process-instance.status.title.citizen-in-progress");

  private final ProcessInstanceStatus processInstanceStatus;
  private final SystemRole systemRole;
  private final String titleKey;

  public static ProcessInstanceStatusMessageTitle from(
      ProcessInstanceStatus processInstanceStatus, SystemRole systemRole) {

    return Stream.of(values())
        .filter(message -> message.getProcessInstanceStatus().equals(processInstanceStatus))
        .filter(message -> Objects.isNull(message.getSystemRole())
            || message.getSystemRole().equals(systemRole))
        .reduce((messageTitle, messageTitle2) -> {
          throw new IllegalStateException("More than 1 message found");
        })
        .orElse(null);
  }
}
