/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.exception;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DateParseException extends RuntimeException {

  /**
   * 
   */
  public DateParseException() {
  }

  /**
   * @param message
   */
  public DateParseException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DateParseException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public DateParseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public DateParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
