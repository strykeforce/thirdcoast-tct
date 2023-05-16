package org.strykeforce.tct.device;

public class NotImplementedError extends Error {
  public NotImplementedError() {
    super("An operation is not implemented");
  }

  public NotImplementedError(String message) {
    super(message);
  }
}
