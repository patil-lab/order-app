package com.prive.ordering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prive.ordering.constants.OrderStatus;
import com.prive.ordering.constants.OrderType;
import com.prive.ordering.constants.RequestStatus;
import com.prive.ordering.dto.request.BrokerCallbackRequest;
import com.prive.ordering.dto.request.OrderDtoRequest;
import com.prive.ordering.dto.response.OrderDtoResponse;
import com.prive.ordering.entity.OrderEntity;
import com.prive.ordering.exception.OrderServiceException;
import com.prive.ordering.repo.OrderRepo;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class})
@SpringBootTest
class OrderServiceTest {

	@Autowired
	RestTemplate restTemplate;

	@SpyBean
	private OrderService orderService;

	@MockBean
	OrderRepo orderRepo;
	@MockBean
	OrderEntity orderEntity;

	final Iterable mockIterable = Mockito.mock(Iterable.class);

	private MockRestServiceServer mockServer;
	private ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	public void init() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	void createOrder_LimitOrderPositive() throws Exception {

		OrderDtoRequest orderDtoRequest = new OrderDtoRequest();
		orderDtoRequest.setCode("AAPL");
		orderDtoRequest.setType(OrderType.LIMIT);
		orderDtoRequest.setPrice("123.6789");
		orderDtoRequest.setQty(200L);
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://broker-app:8081/api/v1.0/create-order")))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(mapper.writeValueAsString(orderDtoRequest)))

		;

		OrderDtoResponse result = orderService.createOrder(orderDtoRequest);
		MatcherAssert.assertThat(result.getOrderId(), containsString("order"));
	}

	@Test
	void createOrder_LimitOrderNegative() throws Exception {

		OrderDtoRequest orderDtoRequest = new OrderDtoRequest();
		orderDtoRequest.setCode("AAPL");
		orderDtoRequest.setType(OrderType.LIMIT);
		orderDtoRequest.setPrice("");
		orderDtoRequest.setQty(200L);
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://broker-app:8081/api/v1.0/create-order")))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(mapper.writeValueAsString(orderDtoRequest)))

		;

		String expectedError = "{\"service\":\"order-service\",\"error\":{\"message\":\"price cant be null for limit order\",\"code\":1001}}";
		OrderServiceException thrown = Assertions.assertThrows(OrderServiceException.class, () -> {
			orderService.createOrder(orderDtoRequest);
		}, "OrderServiceException was expected");

		Assertions.assertEquals(expectedError.trim(), thrown.getMessage());
	}

	@Test
	void createOrder_MarketOrderPositive() throws Exception {

		OrderDtoRequest orderDtoRequest = new OrderDtoRequest();
		orderDtoRequest.setCode("AAPL");
		orderDtoRequest.setType(OrderType.MARKET);
		orderDtoRequest.setPrice("");
		orderDtoRequest.setQty(200L);
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI("http://broker-app:8081/api/v1.0/create-order")))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(mapper.writeValueAsString(orderDtoRequest)))

		;

		OrderDtoResponse result = orderService.createOrder(orderDtoRequest);
		MatcherAssert.assertThat(result.getOrderId(), containsString("order"));
	}

	@Test
	void brokerCallback_OrderCallback_Positive() {

		BrokerCallbackRequest brokerCallbackRequest = new BrokerCallbackRequest();
		brokerCallbackRequest.setOrderStatus(OrderStatus.ACCEPTED);
		brokerCallbackRequest.setOrderId("order123");
		OrderEntity orderEntity = OrderEntity.builder().id(1L).orderId("order123").orderCode("AAPL").
				orderType(OrderType.LIMIT).orderStatus(OrderStatus.PLACED).price(new BigDecimal(123.2345)).build();
		doReturn(Optional.of(orderEntity)).when(orderRepo).findByOrderId(any());
		orderService.brokerCallback(brokerCallbackRequest);
		verify(orderRepo,times(1)).save(any(OrderEntity.class));

	}

	@Test
	void brokerCallback_OrderCallback_Negative() {

		BrokerCallbackRequest brokerCallbackRequest = new BrokerCallbackRequest();
		brokerCallbackRequest.setOrderStatus(OrderStatus.ACCEPTED);
		brokerCallbackRequest.setOrderId("order123");

		String expectedError = "{\"service\":\"order-service\",\"error\":{\"message\":\"No order found\",\"code\":1002}}";
		OrderServiceException thrown = Assertions.assertThrows(OrderServiceException.class, () -> {
			orderService.brokerCallback(brokerCallbackRequest);
		}, "OrderServiceException was expected");

		Assertions.assertEquals(expectedError.trim(), thrown.getMessage());
	}
	@Test
	void brokerCallback_RequestCallback_Positive() {

		BrokerCallbackRequest brokerCallbackRequest = new BrokerCallbackRequest();
		brokerCallbackRequest.setRequestStatus(RequestStatus.ACCEPTED);
		brokerCallbackRequest.setReqId("request123");
		OrderEntity orderEntity = OrderEntity.builder().id(1L).orderId("order123").orderCode("AAPL").
				orderType(OrderType.LIMIT).orderStatus(OrderStatus.PLACED).price(new BigDecimal(123.2345)).build();
		doReturn(Optional.of(orderEntity)).when(orderRepo).findByRequestId(any());
		orderService.brokerCallback(brokerCallbackRequest);
		verify(orderRepo,times(1)).save(any(OrderEntity.class));

	}

	@Test
	void brokerCallback_RequestCallback_Negative() {

		BrokerCallbackRequest brokerCallbackRequest = new BrokerCallbackRequest();
		brokerCallbackRequest.setOrderStatus(OrderStatus.ACCEPTED);
		brokerCallbackRequest.setOrderId("request123");

		String expectedError = "{\"service\":\"order-service\",\"error\":{\"message\":\"No order found\",\"code\":1002}}";
		OrderServiceException thrown = Assertions.assertThrows(OrderServiceException.class, () -> {
			orderService.brokerCallback(brokerCallbackRequest);
		}, "OrderServiceException was expected");

		Assertions.assertEquals(expectedError.trim(), thrown.getMessage());
	}



	@Test
	void getAllOrders() {
		List<OrderEntity> entities=new ArrayList<>();
		entities.add(OrderEntity.builder().id(1L).orderId("order123").orderCode("AAPL").
				orderType(OrderType.LIMIT).orderStatus(OrderStatus.PLACED).price(new BigDecimal(123.2345)).build());
		entities.add(OrderEntity.builder().id(1L).orderId("order123").orderCode("AAPL").
				orderType(OrderType.LIMIT).orderStatus(OrderStatus.PLACED).price(new BigDecimal(123.2345)).build());
		doReturn(entities).when(orderRepo).findAll();
		orderService.getAllOrders();
		verify(orderRepo,times(1)).findAll();
	}
}