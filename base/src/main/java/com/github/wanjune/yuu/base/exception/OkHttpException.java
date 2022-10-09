package com.github.wanjune.yuu.base.exception;

public class OkHttpException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public OkHttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public OkHttpException(String message) {
    super(message);
  }

  public OkHttpException(Throwable cause) {
    super(cause);
  }

  public OkHttpException() {
    super();
  }

  /* avoid the expensive and useless stack trace for api exceptions */
  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
