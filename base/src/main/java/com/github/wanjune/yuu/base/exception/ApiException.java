package com.github.wanjune.yuu.base.exception;

import com.github.wanjune.yuu.base.model.MessageModel;
import com.github.wanjune.yuu.base.util.JsonUtil;
import com.github.wanjune.yuu.base.value.ApiMessageEnum;
import lombok.SneakyThrows;

public class ApiException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private MessageModel messageModel;

  public ApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(Throwable cause) {
    super(cause);
  }

  public ApiException() {
    super();
  }

  public ApiException(ApiMessageEnum messageEnum) {
    this.messageModel = new MessageModel(messageEnum);
  }

  public ApiException(ApiMessageEnum messageEnum, Object obj) {
    this.messageModel = new MessageModel(messageEnum, obj);
  }

  public MessageModel getMessageModel() {
    return messageModel;
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

  @SneakyThrows
  @Override
  public String getMessage() {
    if (messageModel != null) {
      return JsonUtil.writeValueAsString(messageModel);
    } else {
      return super.getMessage();
    }
  }

}
