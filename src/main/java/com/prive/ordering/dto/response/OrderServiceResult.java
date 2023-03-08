package com.prive.ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderServiceResult<T> {
	@JsonProperty("succeed")
	private boolean succeed;

	@JsonProperty("content")
	private T content;

	@JsonProperty("error")
	private T error;


	public static <T> OrderServiceResult<T> succeed(T content){
		return OrderServiceResult.<T>builder()
				.succeed(true)
				.content(content)
				.error(null)
				.build();
	}

	public static <T> OrderServiceResult<T> fail(T error){
		return OrderServiceResult.<T>builder()
				.succeed(false)
				.content(null)
				.error(error)
				.build();
	}
}
