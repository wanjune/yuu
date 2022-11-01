package com.github.wanjune.yuu.exception;

public class MaxComputeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MaxComputeException(String message, Throwable cause) {
    super(message, cause);
  }

  public MaxComputeException(String message) {
    super(message);
  }

  public MaxComputeException(Throwable cause) {
    super(cause);
  }

  public MaxComputeException() {
    super();
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
