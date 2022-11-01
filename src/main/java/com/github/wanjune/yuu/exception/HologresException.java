package com.github.wanjune.yuu.exception;

public class HologresException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public HologresException(String message, Throwable cause) {
    super(message, cause);
  }

  public HologresException(String message) {
    super(message);
  }

  public HologresException(Throwable cause) {
    super(cause);
  }

  public HologresException() {
    super();
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
