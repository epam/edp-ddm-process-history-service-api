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

package com.epam.digital.data.platform.bphistory.service.api.mapper;

import com.epam.digital.data.platform.bphistory.service.api.i18n.HistoryProcessInstanceStatusMessageTitle;
import com.epam.digital.data.platform.bphistory.service.api.model.HistoryProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessInstanceStatus;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcess;
import com.epam.digital.data.platform.starter.localization.MessageResolver;
import java.util.List;
import java.util.Objects;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class HistoryProcessMapper {

  @Autowired
  private MessageResolver messageResolver;

  @IterableMapping(qualifiedByName = "toHistoryProcessModel")
  public abstract List<HistoryProcessResponse> toModelList(
      List<BpmHistoryProcess> bpmHistoryProcesses);

  @Named("toHistoryProcessModel")
  @Mapping(target = "status.code", source = "state")
  @Mapping(target = "status.title", source = "bpmHistoryProcess")
  public abstract HistoryProcessResponse toModel(BpmHistoryProcess bpmHistoryProcess);

  public String toStatusTitle(BpmHistoryProcess bpmHistoryProcess) {
    var state = bpmHistoryProcess.getState();
    if (ProcessInstanceStatus.EXTERNALLY_TERMINATED.name().equals(state)) {
      return messageResolver.getMessage(
          HistoryProcessInstanceStatusMessageTitle.EXTERNALLY_TERMINATED);
    }

    if (!ProcessInstanceStatus.COMPLETED.name().equals(state)) {
      return null;
    }

    return Objects.requireNonNullElseGet(bpmHistoryProcess.getCompletionResult(),
        () -> messageResolver.getMessage(HistoryProcessInstanceStatusMessageTitle.COMPLETED));
  }
}
