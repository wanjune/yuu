package com.github.wanjune.yuu.exception;

public class OssException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public OssException(String message, Throwable cause) {
    super(message, cause);
  }

  public OssException(String message) {
    super(message);
  }

  public OssException(Throwable cause) {
    super(cause);
  }

  public OssException() {
    super();
  }

  /* avoid the expensive and useless stack trace for api exceptions */
  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
