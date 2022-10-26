package com.github.wanjune.yuu.base.exception;

import com.github.wanjune.yuu.base.model.MessageModel;
import com.github.wanjune.yuu.base.util.JsonUtil;
import com.github.wanjune.yuu.base.value.Messageable;
import lombok.SneakyThrows;

public class YuuException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private MessageModel messageModel = null;

  public YuuException(String message, Throwable cause) {
    super(message, cause);
  }

  public YuuException(String message) {
    super(message);
  }

  public YuuException(Throwable cause) {
    super(cause);
  }

  public YuuException() {
    super();
  }

  public YuuException(Messageable message) {
    this.messageModel = new MessageModel(message);
  }

  public YuuException(Messageable message, Object obj) {
    this.messageModel = new MessageModel(message, obj);
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
