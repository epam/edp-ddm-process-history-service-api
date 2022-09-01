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

import com.epam.digital.data.platform.bphistory.service.api.model.HistoryTaskResponse;
import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryTask;
import java.util.List;
import java.util.Map;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HistoryTaskMapper {

  List<HistoryTaskResponse> toResponseList(List<BpmHistoryTask> bpmHistoryTasks,
      @Context Map<String, String> businessKeys);

  @Mapping(target = "businessKey",
      expression = "java(businessKeys.get(bpmHistoryTask.getRootProcessInstanceId()))")
  HistoryTaskResponse toResponse(BpmHistoryTask bpmHistoryTask,
      @Context Map<String, String> businessKeys);

}
