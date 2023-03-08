package com.prive.ordering.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.prive.ordering.constants.OrderType;
import com.prive.ordering.entity.OrderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDtoRequest {

	@ApiModelProperty(value = "order type",example = "LIMIT or MARKET")
	private OrderType type;


	@ApiModelProperty(value = "order quantity",example = "100")
	@NotNull
	private Long qty;

	@ApiModelProperty(value = "order code",example = "AAPL")
	@NotEmpty
	@Pattern(regexp = "[A-Z]{3,4}",message = "3 to 4 uppercase letters")
	private String code;

	@ApiModelProperty(value = "order price",example = "123.4567")
	private String price;


	public OrderEntity toOrderEntity(){
		return OrderEntity.builder().orderCode(this.code).orderType(this.type).quantity(this.qty).
				orderCode(this.code).build();
	}

}
