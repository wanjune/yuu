package com.github.wanjune.yuu.base.exception;

import com.github.wanjune.yuu.base.model.MessageModel;
import com.github.wanjune.yuu.base.util.JsonUtil;
import com.github.wanjune.yuu.base.value.TaskMessageEnum;
import lombok.SneakyThrows;

public class TaskException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private MessageModel messageModel;

  public TaskException(String message, Throwable cause) {
    super(message, cause);
  }

  public TaskException(String message) {
    super(message);
  }

  public TaskException(Throwable cause) {
    super(cause);
  }

  public TaskException() {
    super();
  }

  public TaskException(TaskMessageEnum messageEnum) {
    this.messageModel = new MessageModel(messageEnum);
  }

  public TaskException(TaskMessageEnum messageEnum, Object obj) {
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
