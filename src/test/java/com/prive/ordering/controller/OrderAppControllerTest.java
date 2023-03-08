package com.prive.ordering.controller;

import com.google.gson.Gson;
import com.prive.ordering.constants.OrderStatus;
import com.prive.ordering.constants.OrderType;
import com.prive.ordering.dto.request.BrokerCallbackRequest;
import com.prive.ordering.dto.request.OrderDtoRequest;
import com.prive.ordering.dto.response.OrderDtoResponse;
import com.prive.ordering.dto.response.OrderServiceResult;
import com.prive.ordering.exception.ApplicationeError;
import com.prive.ordering.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class})
@SpringBootTest
class OrderAppControllerTest {

	@Value("${api-version}")
	private String apiVersionPath;

	@MockBean
	OrderService orderService;

	@Autowired
	private MockMvc mvc;

	private Gson gson=new Gson();


	@Test
	void createOrder_MarketOrder_Positive() throws Exception{
		OrderDtoResponse orderDtoResponse= OrderDtoResponse.builder().orderId("orderd1160d80-f9db-4f64-be14-b137b237ed8e").build();

		OrderDtoRequest orderDtoRequest=new OrderDtoRequest();
		orderDtoRequest.setCode("AAPL");
		orderDtoRequest.setType(OrderType.MARKET);
		orderDtoRequest.setPrice("");
		orderDtoRequest.setQty(200L);

		doReturn(orderDtoResponse).when(orderService).createOrder(orderDtoRequest);



		mvc.perform(MockMvcRequestBuilders.post("/{apiVersion}/orders",apiVersionPath)
				.contentType(MediaType.APPLICATION_JSON).content(gson.toJson(orderDtoRequest))).andExpect(status().isOk());


		verify(orderService,times(1)).createOrder(any(OrderDtoRequest.class));
	}

	@Test
	void createOrder_LIMITOrder_Positive() throws Exception{
		OrderDtoResponse orderDtoResponse= OrderDtoResponse.builder().orderId("orderd1160d80-f9db-4f64-be14-b137b237ed8e").build();

		OrderDtoRequest orderDtoRequest=new OrderDtoRequest();
		orderDtoRequest.setCode("AAPL");
		orderDtoRequest.setType(OrderType.LIMIT);
		orderDtoRequest.setPrice("123.6789");
		orderDtoRequest.setQty(200L);

		doReturn(orderDtoResponse).when(orderService).createOrder(orderDtoRequest);



		mvc.perform(MockMvcRequestBuilders.post("/{apiVersion}/orders",apiVersionPath)
				.contentType(MediaType.APPLICATION_JSON).content(gson.toJson(orderDtoRequest))).andExpect(status().isOk());


		verify(orderService,times(1)).createOrder(any(OrderDtoRequest.class));




	}

	@Test
	void brokerCallback() throws  Exception {

		BrokerCallbackRequest brokerCallbackRequest=new BrokerCallbackRequest();
		brokerCallbackRequest.setOrderId("order480c2c45-8827-4265-af7e-9b021e1fa8ca");
		brokerCallbackRequest.setOrderStatus(OrderStatus.ACCEPTED);
		doNothing().when(orderService).brokerCallback(brokerCallbackRequest);
		mvc.perform(MockMvcRequestBuilders.post("/{apiVersion}/broker-callback",apiVersionPath)
				.contentType(MediaType.APPLICATION_JSON).content(gson.toJson(brokerCallbackRequest))).andExpect(status().isOk());

		verify(orderService,times(1)).brokerCallback(any(BrokerCallbackRequest.class));
	}

	@Test
	void getOrders() throws Exception{

		List<OrderDtoResponse> orderDtoResponseList=new ArrayList<>();
		orderDtoResponseList.add(OrderDtoResponse.builder().build());
		orderDtoResponseList.add(OrderDtoResponse.builder().build());
		doReturn(orderDtoResponseList).when(orderService).getAllOrders();
		mvc.perform(MockMvcRequestBuilders.get("/{apiVersion}/orders",apiVersionPath).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		verify(orderService,times(1)).getAllOrders();
	}
}