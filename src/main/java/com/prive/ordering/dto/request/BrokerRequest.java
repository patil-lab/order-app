package com.prive.ordering.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrokerRequest {

	private  String orderId;

	private String requestId;

	private String callbackUrl;
}
