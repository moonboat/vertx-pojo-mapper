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
package de.braintags.io.vertx.pojomapper.mapping.impl;

import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContext;
import de.braintags.io.vertx.util.FutureImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A TriggerContext can be used as argument for mapper methods, which are annotated by one of the annotations like
 * {@link BeforeSave}, {@link AfterSave} etc.
 * 
 * @author Michael Remme
 * 
 */
public class TriggerContext extends FutureImpl<Void> implements ITriggerContext {
  private IMapper mapper;

  /**
   * 
   */
  TriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler) {
    this.mapper = mapper;
    setHandler(handler);
  }

  /**
   * Get the instance of IMapper, which is underlaying the current request
   * 
   * @return the mapper
   */
  @Override
  public final IMapper getMapper() {
    return mapper;
  }

}
