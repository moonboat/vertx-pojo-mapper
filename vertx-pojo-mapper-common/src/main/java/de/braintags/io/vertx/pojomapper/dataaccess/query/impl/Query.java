/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.impl.AbstractDataAccessObject;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryLogic;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public abstract class Query<T> extends AbstractDataAccessObject<T>implements IQuery<T> {
  private List<Object> filters = new ArrayList<Object>();

  /**
   * @param mapperClass
   * @param datastore
   */
  public Query(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public IFieldParameter<Query<T>> field(String fieldName) {
    FieldParameter<Query<T>> param = new FieldParameter<Query<T>>(this, getMapper().getField(fieldName));
    filters.add(param);
    return param;
  }

  @Override
  public IFieldParameter<LogicContainer<Query<T>>> and(String fieldName) {
    LogicContainer<Query<T>> container = new LogicContainer<Query<T>>(this, QueryLogic.AND);
    filters.add(container);
    return container.field(fieldName);
  }

  @Override
  public IFieldParameter<LogicContainer<Query<T>>> or(String fieldName) {
    LogicContainer<Query<T>> container = new LogicContainer<Query<T>>(this, QueryLogic.OR);
    filters.add(container);
    return container.field(fieldName);
  }

  /**
   * Traverses through all elements of the current definition, which implement {@link IRamblerSource} and executes the
   * methods of the {@link IQueryRambler}
   * 
   * @param rambler
   *          the rambler to be filled
   */
  public void executeQueryRambler(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.start(this);
    if (filters.isEmpty()) {
      finishCounter(rambler, resultHandler);
      return;
    }
    ErrorObject<Void> error = new ErrorObject<Void>();
    CounterObject co = new CounterObject(filters.size());
    for (Object filter : filters) {
      handleFilter(rambler, resultHandler, filter, error, co);
      if (error.isError()) {
        return;
      }
    }
  }

  private void handleFilter(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler, Object filter,
      ErrorObject<Void> error, CounterObject co) {
    if (!(filter instanceof IRamblerSource)) {
      error.setThrowable(
          new UnsupportedOperationException("NOT AN INSTANCE OF IRamblerSource: " + filter.getClass().getName()));
      error.handleError(resultHandler);
      return;
    }

    ((IRamblerSource) filter).applyTo(rambler, result -> {
      if (result.failed()) {
        error.setThrowable(result.cause());
        error.handleError(resultHandler);
        return;
      }
      if (co.reduce()) { // last element in the list
        finishCounter(rambler, resultHandler);
      }
    });
  }

  private void finishCounter(IQueryRambler rambler, Handler<AsyncResult<Void>> resultHandler) {
    rambler.stop(this);
    resultHandler.handle(Future.succeededFuture());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#getChildren()
   */
  @Override
  public List<Object> getChildren() {
    return filters;
  }

}
