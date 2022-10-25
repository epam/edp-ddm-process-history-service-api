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

package com.epam.digital.data.platform.bphistory.service.api.repository;

import com.epam.digital.data.platform.bphistory.service.api.repository.entity.BpmHistoryProcessTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProcessTaskRepository extends PagingAndSortingRepository<BpmHistoryProcessTask, String> {


  List<BpmHistoryProcessTask> findAllByStartUserIdAndStateInAndSuperProcessInstanceIdIsNull(
          String startUserId, Set<String> stateIn, Pageable pageable);

  List<BpmHistoryProcessTask> findAllByProcessInstanceIdIn(Set<String> processInstanceIdIn);

  long countByStartUserIdAndStateIn(String startUserId, Set<String> stateIn);
}
