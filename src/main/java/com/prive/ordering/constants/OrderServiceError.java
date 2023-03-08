package com.prive.ordering.constants;

import org.springframework.http.HttpStatus;

public enum  OrderServiceError {
	UNKNOWN(0, "unknown error", HttpStatus.BAD_REQUEST),
	PRICE_CANT_BE_NULL(1001,"price cant be null for limit order",HttpStatus.BAD_REQUEST),
	NO_ORDER_FOUND(1002,"No order found",HttpStatus.BAD_REQUEST);

	private final int errorCode;
	private final String message;
	private final HttpStatus httpStatus;

	OrderServiceError(int errorCode ,String message,HttpStatus status){
		this.message=message;
		this.errorCode=errorCode;
		this.httpStatus=status;
	}

	public String getMessage(){
		return this.message;
	}

	public int getErrorCode(){
		return this.errorCode;
	}

	public HttpStatus getHttpStatus(){
		return this.httpStatus;
	}
}
