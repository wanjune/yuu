package com.github.wanjune.yuu.base.exception;

public class RedisException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RedisException(String message, Throwable cause) {
    super(message, cause);
  }

  public RedisException(String message) {
    super(message);
  }

  public RedisException(Throwable cause) {
    super(cause);
  }

  public RedisException() {
    super();
  }

  /* avoid the expensive and useless stack trace for api exceptions */
  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
