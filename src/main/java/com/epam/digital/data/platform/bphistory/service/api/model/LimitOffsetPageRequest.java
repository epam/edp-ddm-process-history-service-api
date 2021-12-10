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

import java.util.Objects;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LimitOffsetPageRequest implements Pageable {

  private final int limit;
  private final long offset;
  private final Sort sort;

  public LimitOffsetPageRequest(long offset, int limit, Sort sort) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset less than 0");
    }

    if (limit < 1) {
      throw new IllegalArgumentException("Limit less than 1");
    }
    this.limit = limit;
    this.offset = offset;
    this.sort = sort;
  }

  public LimitOffsetPageRequest(long offset, int limit, Sort.Direction direction,
      String... properties) {
    this(offset, limit, Sort.by(direction, properties));
  }

  @Override
  public int getPageNumber() {
    return Math.toIntExact(offset / limit);
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Pageable next() {
    return new LimitOffsetPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
  }

  public LimitOffsetPageRequest previous() {
    return hasPrevious() ? new LimitOffsetPageRequest(getOffset() - getPageSize(), getPageSize(),
        getSort()) : this;
  }

  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  @Override
  public Pageable first() {
    return new LimitOffsetPageRequest(0, getPageSize(), getSort());
  }

  @Override
  public Pageable withPage(int pageNumber) {
    return new LimitOffsetPageRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
  }

  @Override
  public boolean hasPrevious() {
    return offset > limit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LimitOffsetPageRequest that = (LimitOffsetPageRequest) o;
    return limit == that.limit && offset == that.offset && Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(limit, offset, sort);
  }

  @Override
  public String toString() {
    return "LimitOffsetPageRequest{" +
        "limit=" + limit +
        ", offset=" + offset +
        ", sort=" + sort +
        '}';
  }
}
