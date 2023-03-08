package com.prive.ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prive.ordering.constants.OrderStatus;
import com.prive.ordering.constants.OrderType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDtoResponse {

	@JsonProperty("id")
	private String orderId;
	@JsonProperty("type")
	private OrderType orderType;

	@JsonProperty("qty")
	private Long quantity;

	@JsonProperty("code")
	private String orderCode;

	@JsonProperty("price")
	private String price;

	@JsonProperty("status")
	private OrderStatus orderStatus;
}
