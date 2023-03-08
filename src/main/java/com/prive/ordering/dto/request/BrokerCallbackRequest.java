package com.prive.ordering.dto.request;


import com.prive.ordering.constants.OrderStatus;
import com.prive.ordering.constants.RequestStatus;
import lombok.Data;

@Data
public class BrokerCallbackRequest {

	private RequestStatus requestStatus;

	private String reqId;

	private OrderStatus orderStatus;

	private String orderId;

}
