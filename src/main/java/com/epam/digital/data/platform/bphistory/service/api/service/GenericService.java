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

package com.epam.digital.data.platform.bphistory.service.api.service;

import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public abstract class GenericService<I> {

  protected final JwtInfoProvider jwtInfoProvider;

  protected GenericService(JwtInfoProvider jwtInfoProvider) {
    this.jwtInfoProvider = jwtInfoProvider;
  }

  public List<I> getItems(Pageable request, SecurityContext securityContext) {
    var userClaims = jwtInfoProvider.getUserClaims(securityContext.getAccessToken());

    return getItems(userClaims, request);
  }

  protected abstract List<I> getItems(JwtClaimsDto userClaims, Pageable request);
}
