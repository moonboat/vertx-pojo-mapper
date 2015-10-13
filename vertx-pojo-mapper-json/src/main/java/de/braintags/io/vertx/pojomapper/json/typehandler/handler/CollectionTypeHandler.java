/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import java.util.Collection;
import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * Deals all fields, which contain {@link Collection} content, in spite of maps
 * 
 * @author Michael Remme
 * 
 */

public class CollectionTypeHandler extends AbstractTypeHandler {

  /**
   * @param classesToDeal
   */
  public CollectionTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Collection.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null || ((JsonArray) source).isEmpty()) {
      success(null, resultHandler);
      return;
    }

    CounterObject co = new CounterObject(((JsonArray) source).size());
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(resultHandler);
    @SuppressWarnings("rawtypes")
    Collection coll = field.getMapper().getObjectFactory().createCollection(field);
    Iterator<?> ji = ((JsonArray) source).iterator();
    ITypeHandler subHandler = field.getSubTypeHandler();
    while (ji.hasNext() && !errorObject.isError()) {
      Object o = ji.next();
      handleObjectFromStore(o, subHandler, coll, field, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
          return;
        } else {
          if (co.reduce()) {
            success(coll, resultHandler);
            return;
          }
        }
      });
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void handleObjectFromStore(Object o, ITypeHandler subHandler, Collection coll, IField field,
      Handler<AsyncResult<Void>> resultHandler) {
    if (subHandler != null) {
      subHandler.fromStore(o, null, field.getSubClass(), tmpResult -> {
        if (tmpResult.failed()) {
          resultHandler.handle(Future.failedFuture(tmpResult.cause()));
          return;
        } else {
          Object dest = tmpResult.result().getResult();
          coll.add(dest);
          resultHandler.handle(Future.succeededFuture());
        }
      });
    } else {
      coll.add(o);
      resultHandler.handle(Future.succeededFuture());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null || ((Collection<?>) source).isEmpty()) {
      success(null, resultHandler);
      return;
    }

    JsonArray jsonArray = new JsonArray();
    CounterObject co = new CounterObject(((Collection<?>) source).size());
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(resultHandler);
    Iterator<?> sourceIt = ((Collection<?>) source).iterator();
    ITypeHandler subHandler = field.getSubTypeHandler();
    // no generics were defined, so that subhandler could not be defined from mapping
    boolean determineSubhandler = subHandler == null;
    Class<?> valueClass = null;
    while (sourceIt.hasNext() && !errorObject.isError()) {
      Object value = sourceIt.next();

      if (determineSubhandler) {
        boolean valueClassChanged = valueClass != null && value.getClass() != valueClass;
        valueClass = value.getClass();
        if (subHandler == null || valueClassChanged) {
          subHandler = ((Mapper) field.getMapper()).getMapperFactory().getDataStore().getTypeHandlerFactory()
              .getTypeHandler(value.getClass());
          // TODO could it be useful to write the class of the value into the field, to restore it proper from
          // datastore?
        }
      }
      subHandler.intoStore(value, null, tmpResult -> {
        if (tmpResult.failed()) {
          errorObject.setThrowable(tmpResult.cause());
          return;
        } else {
          ITypeHandlerResult thResult = tmpResult.result();
          jsonArray.add(thResult.getResult());
          if (co.reduce()) {
            success(jsonArray, resultHandler);
          }
        }
      });

    }
  }

}
