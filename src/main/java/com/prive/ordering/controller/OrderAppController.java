package com.prive.ordering.controller;

import com.prive.ordering.dto.request.BrokerCallbackRequest;
import com.prive.ordering.dto.request.OrderDtoRequest;
import com.prive.ordering.dto.response.OrderDtoResponse;
import com.prive.ordering.dto.response.OrderServiceResult;
import com.prive.ordering.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.xml.bind.ValidationEventLocator;
import java.util.List;

@RestController
@RequestMapping(value = "${api-version}",produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class OrderAppController {

	private OrderService orderService;

	@Autowired
	OrderAppController(OrderService orderService){
		this.orderService=orderService;
	}

	@PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderServiceResult<OrderDtoResponse>> createOrder(@RequestBody @Valid OrderDtoRequest orderDtoRequest){
		OrderDtoResponse orderDtoResponse=orderService.createOrder(orderDtoRequest);
		return ResponseEntity.ok(OrderServiceResult.succeed(orderDtoResponse));
	}

	@PostMapping(value = "/broker-callback",consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderServiceResult<String>> brokerCallback(@RequestBody @Valid BrokerCallbackRequest brokerCallbackRequest){
		orderService.brokerCallback(brokerCallbackRequest);
		return ResponseEntity.ok(OrderServiceResult.succeed(HttpStatus.OK.getReasonPhrase()));
	}

	@GetMapping("/orders")
	public ResponseEntity<OrderServiceResult<List<OrderDtoResponse>>> getOrders(){
		List<OrderDtoResponse> orderDtoResponseList=orderService.getAllOrders();
		return ResponseEntity.ok(OrderServiceResult.succeed(orderDtoResponseList));
	}
}
