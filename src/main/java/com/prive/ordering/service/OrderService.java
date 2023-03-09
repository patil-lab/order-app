package com.prive.ordering.service;

import com.prive.ordering.config.RestTemplateConfig;
import com.prive.ordering.constants.CallBackType;
import com.prive.ordering.constants.OrderServiceError;
import com.prive.ordering.constants.OrderStatus;
import com.prive.ordering.constants.OrderType;
import com.prive.ordering.constants.ServiceDomain;
import com.prive.ordering.dto.request.BrokerCallbackRequest;
import com.prive.ordering.dto.request.BrokerRequest;
import com.prive.ordering.dto.request.OrderDtoRequest;
import com.prive.ordering.dto.response.OrderDtoResponse;
import com.prive.ordering.entity.OrderEntity;
import com.prive.ordering.exception.OrderServiceException;
import com.prive.ordering.repo.OrderRepo;
import com.prive.ordering.util.ConstUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

	@Autowired
	private ServiceDomain serviceDomain;

	private final RestTemplate restTemplate;

	private OrderRepo orderRepo;


	@Value("${spring.application.name}")
	private String serviceName;

	@Autowired
	public OrderService(@Qualifier(RestTemplateConfig.DEFAULT_REST_TEMPLATE) RestTemplate restTemplate, OrderRepo orderRepo){
		this.restTemplate=restTemplate;
		this.orderRepo=orderRepo;
	}

	@Transactional
	public OrderDtoResponse createOrder(OrderDtoRequest orderDtoRequest){
		StringBuilder orderId;
		try {
			if (OrderType.LIMIT.equals(orderDtoRequest.getType())) {
				if (orderDtoRequest.getPrice() != null)
					checkValid(orderDtoRequest.getPrice());
			}


			orderId = new StringBuilder();
			orderId.append("order");
			orderId.append(ConstUtil.getUUID());
			StringBuilder requestId = new StringBuilder();
			requestId.append("request");
			requestId.append(ConstUtil.getUUID());
			OrderEntity orderEntity = orderDtoRequest.toOrderEntity();
			if (OrderType.LIMIT.equals(orderDtoRequest.getType())) {
				orderEntity.setPrice(new BigDecimal(orderDtoRequest.getPrice()).setScale(4, RoundingMode.HALF_EVEN));

			} else
				orderEntity.setPrice(BigDecimal.ZERO);
			orderEntity.setOrderStatus(OrderStatus.PLACED);
			orderEntity.setOrderId(orderId.toString());
			orderEntity.setRequestId(requestId.toString());
			orderRepo.save(orderEntity);

			BrokerRequest brokerRequest = BrokerRequest.builder().requestId(requestId.toString()).orderId(orderId.toString()).callbackUrl(serviceDomain.getOrderServiceUrl()).build();
			String url = String.format("%s/create-order", serviceDomain.getBrokerServiceUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<BrokerRequest> httpEntity = new HttpEntity<>(brokerRequest, headers);


			restTemplate.postForEntity(builder.toUriString(), httpEntity, String.class);

		} catch (Exception e) {
			throw new OrderServiceException(serviceName, e.getMessage(), 1);
		}

		return OrderDtoResponse.builder().orderId(orderId.toString()).build();

	}

	public void brokerCallback(BrokerCallbackRequest brokerCallbackRequest){
		CallBackType callBackType=checkPairValidity(brokerCallbackRequest);


		if(CallBackType.ORDER.equals(callBackType)){
			Optional<OrderEntity> entityOptional=orderRepo.findByOrderId(brokerCallbackRequest.getOrderId());
			if(entityOptional.isEmpty()){
				throw new OrderServiceException(serviceName,OrderServiceError.NO_ORDER_FOUND.getMessage(),OrderServiceError.NO_ORDER_FOUND.getErrorCode());
			}else {
				OrderEntity orderEntity=entityOptional.get();
				orderEntity.setOrderStatus(brokerCallbackRequest.getOrderStatus());
				orderRepo.save(orderEntity);
			}

		}else if(CallBackType.REQUEST.equals(callBackType)){

			Optional<OrderEntity> entityOptional=orderRepo.findByRequestId(brokerCallbackRequest.getReqId());
			if(entityOptional.isEmpty())
				throw new OrderServiceException(serviceName,OrderServiceError.NO_ORDER_FOUND.getMessage(),OrderServiceError.NO_ORDER_FOUND.getErrorCode());
			else {
				OrderEntity orderEntity=entityOptional.get();
				orderEntity.setRequestStatus(brokerCallbackRequest.getRequestStatus());
				orderRepo.save(orderEntity);
			}

		}else {
			throw new OrderServiceException(serviceName,OrderServiceError.NO_ORDER_FOUND.getMessage(),OrderServiceError.NO_ORDER_FOUND.getErrorCode());
		}
	}


	public List<OrderDtoResponse> getAllOrders(){
		Iterable<OrderEntity> entities = orderRepo.findAll();

		return StreamSupport.stream(entities.spliterator(),false).map(OrderEntity::toOrderDto).collect(Collectors.toList());
	}

	private CallBackType checkPairValidity(BrokerCallbackRequest brokerCallbackRequest) {
		if(brokerCallbackRequest.getOrderId()!=null){
			if(brokerCallbackRequest.getOrderStatus()!=null){
				return CallBackType.ORDER;
			}
		} else if (brokerCallbackRequest.getReqId() != null) {

			if(brokerCallbackRequest.getRequestStatus()!=null)
				return CallBackType.REQUEST;
		}
		return CallBackType.NONE;
	}

	private  void checkValid(String value) {
		try {
			BigDecimal decimal = new BigDecimal(value);
			boolean valid=false;
			if( decimal.signum() > 0 && decimal.scale() == 4)
				valid=true;
		}
		catch (NumberFormatException e) {
			throw new OrderServiceException(serviceName,OrderServiceError.PRICE_CANT_BE_NULL.getMessage(),OrderServiceError.PRICE_CANT_BE_NULL.getErrorCode());

		}
	}
}
