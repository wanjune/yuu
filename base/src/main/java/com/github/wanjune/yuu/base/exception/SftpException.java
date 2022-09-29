package com.github.wanjune.yuu.base.exception;

public class SftpException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SftpException(String message, Throwable cause) {
    super(message, cause);
  }

  public SftpException(String message) {
    super(message);
  }

  public SftpException(Throwable cause) {
    super(cause);
  }

  public SftpException() {
    super();
  }

  /* avoid the expensive and useless stack trace for api exceptions */
  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
