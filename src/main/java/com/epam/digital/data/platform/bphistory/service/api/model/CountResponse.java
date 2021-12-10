package com.epam.digital.data.platform.bphistory.service.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountResponse {

  private long count;
}
