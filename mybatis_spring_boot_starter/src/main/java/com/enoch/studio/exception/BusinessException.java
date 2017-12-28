package com.enoch.studio.exception;


import com.enoch.studio.constant.ResultCode;

public class BusinessException extends Exception {
	private ResultCode code;

	public ResultCode getCode() {
		return code;
	}

	public void setCode(ResultCode code) {
		this.code = code;
	}

	public BusinessException(ResultCode code, String message) {
		super(message);
		this.code = code;
	}

	public BusinessException(ResultCode code) {
		super();
		this.code = code;
	}

}
