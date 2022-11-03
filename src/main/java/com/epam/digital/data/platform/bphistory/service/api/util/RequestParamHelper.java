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

package com.epam.digital.data.platform.bphistory.service.api.util;

import com.epam.digital.data.platform.bphistory.service.api.exception.SortParamWrongFormatException;
import com.epam.digital.data.platform.bphistory.service.api.model.LimitOffsetPageRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

@Component
public class RequestParamHelper {

  private static final String ASC_PREFIX = "asc(";
  private static final String DESC_PREFIX = "desc(";
  private static final String STATUS_TITLE = "statusTitle";
  private static final String START_TIME = "startTime";

  public Pageable getPageRequest(int limit, int offset, String sort) {
    checkValidity(sort);

    return new LimitOffsetPageRequest(offset, limit, getSortDirection(sort), getSortField(sort));
  }

  public Pageable getPageRequestSortedByStatusAndStartTime(int limit, int offset, String sort) {
    checkValidity(sort);

    String sortField = getSortField(sort);
    Direction direction = getSortDirection(sort);
    if(sortField.equals(STATUS_TITLE)) {
      return new LimitOffsetPageRequest(offset, limit, 
          Sort.by(
              new Order(direction, sortField), 
              new Order(Direction.DESC, START_TIME)
          ));
    }
    return new LimitOffsetPageRequest(offset, limit, direction, sortField);
  }

  private void checkValidity(String sort) {
    if (!(sort.startsWith(ASC_PREFIX) || sort.startsWith(DESC_PREFIX)) || !sort.endsWith(")")) {
      throw new SortParamWrongFormatException("Wrong format for sort request parameter");
    }
  }

  private Direction getSortDirection(String sort) {
    return sort.startsWith(ASC_PREFIX)
        ? Direction.ASC
        : Direction.DESC;
  }

  private String getSortField(String sort) {
    return StringUtils.substringBetween(sort, "(", ")");
  }
}
