package com.prive.ordering.entity;

import com.prive.ordering.constants.OrderStatus;
import com.prive.ordering.constants.OrderType;
import com.prive.ordering.constants.RequestStatus;
import com.prive.ordering.dto.response.OrderDtoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;


@Data
@Entity
@Builder
@Table(name = "user_order",uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id","request_id"},name = "uk_order_id_request_id")})
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "order_id",nullable = false)
	private String orderId;

	@Column(name = "request_id" ,nullable = false)
	private String requestId;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_type")
	private OrderType orderType;

	@Basic(optional = false)
	@Column(name = "quantity",nullable = false)
	private Long quantity;

	@Column(name = "order_code",nullable = false)
	private String orderCode;

	@Column(name = "price")
	private BigDecimal price=new BigDecimal(0);

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status")
	private OrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_status")
	private RequestStatus requestStatus;

	public OrderDtoResponse toOrderDto(){
		return OrderDtoResponse.builder().orderId(this.orderId).orderCode(this.orderCode).
				orderStatus(this.orderStatus).orderType(this.orderType).price(this.price.toString()).quantity(this.quantity).build();
	}
}
