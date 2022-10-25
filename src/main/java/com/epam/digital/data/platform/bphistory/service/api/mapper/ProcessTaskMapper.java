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

package com.epam.digital.data.platform.bphistory.service.api.mapper;

import com.epam.digital.data.platform.bphistory.service.api.i18n.ProcessInstanceStatusMessageTitle;
import com.epam.digital.data.platform.bphistory.service.api.model.ProcessResponse;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcessTask;
import com.epam.digital.data.platform.starter.localization.MessageResolver;
import com.epam.digital.data.platform.starter.security.SystemRole;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProcessTaskMapper {

  @Autowired
  private MessageResolver messageResolver;

  @IterableMapping(qualifiedByName = "toProcessModel")
  public abstract List<ProcessResponse> toModelList(List<BpmHistoryProcessTask> bpmHistoryProcessTasks,
      @Context SystemRole systemRole);

  @Named("toProcessModel")
  @Mapping(target = "status.code", source = "processState")
  public abstract ProcessResponse toModel(BpmHistoryProcessTask bpmHistoryProcessTask,
                                          @Context SystemRole systemRole);

  @AfterMapping
  public ProcessResponse setStatusTitle(@MappingTarget ProcessResponse response,
      @Context SystemRole systemRole) {
    var messageTitle = ProcessInstanceStatusMessageTitle.from(response.getStatus().getCode(),
        systemRole);

    var title = Objects.isNull(messageTitle) ? null : messageResolver.getMessage(messageTitle);
    response.getStatus().setTitle(title);
    return response;
  }
}
