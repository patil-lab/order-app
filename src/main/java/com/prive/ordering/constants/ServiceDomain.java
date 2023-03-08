package com.prive.ordering.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ServiceDomain {

	@Value("${order.service.url}")
	private String orderServiceUrl;

	@Value("${broker.service.url}")
	private String brokerServiceUrl;
}
