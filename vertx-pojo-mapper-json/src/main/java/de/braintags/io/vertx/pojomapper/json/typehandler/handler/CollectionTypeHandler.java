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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.util.CounterObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * Deals all fields, which contain {@link Collection} content, which are NOT annotated as {@link Referenced} or
 * {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */

public class CollectionTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public CollectionTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Collection.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler#matchesAnnotation(java.lang.annotation.Annotation)
   */
  @Override
  protected boolean matchesAnnotation(Annotation annotation) {
    return annotation == null;
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
    if (source == null) {
      success(null, resultHandler);
    } else if (((JsonArray) source).isEmpty()) {
      success(field.getMapper().getObjectFactory().createCollection(field), resultHandler);
    } else {
      CounterObject<ITypeHandlerResult> co = new CounterObject<>(((JsonArray) source).size(), resultHandler);
      Collection coll = field.getMapper().getObjectFactory().createCollection(field);
      Iterator<?> ji = ((JsonArray) source).iterator();
      ITypeHandler subHandler = field.getSubTypeHandler();
      while (ji.hasNext() && !co.isError()) {
        Object o = ji.next();
        handleObjectFromStore(o, subHandler, coll, field, result -> {
          if (result.failed()) {
            co.setThrowable(result.cause());
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
  }

  /**
   * Create one instance of the {@link Collection} and add it into the Collection
   * 
   * @param o
   *          the object from the store
   * @param subHandler
   *          the subhandler to be used
   * @param coll
   *          the collection to be filled
   * @param field
   *          the field, where the Collection stays in
   * @param resultHandler
   *          the handler to be informed
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void handleObjectFromStore(Object o, ITypeHandler subHandler, Collection coll, IField field,
      Handler<AsyncResult<Void>> resultHandler) {
    if (subHandler != null) {
      subHandler.fromStore(o, field, field.getSubClass(), tmpResult -> {
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
    if (source == null) {
      success(null, resultHandler);
    } else if (((Collection<?>) source).isEmpty()) {
      success(new JsonArray(), resultHandler);
    } else {
      JsonArray jsonArray = new JsonArray();
      CounterObject<ITypeHandlerResult> co = new CounterObject<>(((Collection<?>) source).size(), resultHandler);
      Iterator<?> sourceIt = ((Collection<?>) source).iterator();
      ITypeHandler subHandler = field.getSubTypeHandler();
      // no generics were defined, so that subhandler could not be defined from mapping
      boolean determineSubhandler = subHandler == null;
      Class<?> valueClass = null;
      while (sourceIt.hasNext() && !co.isError()) {
        Object value = sourceIt.next();
        if (determineSubhandler) {
          boolean valueClassChanged = valueClass != null && value.getClass() != valueClass;
          valueClass = value.getClass();
          if (subHandler == null || valueClassChanged) {
            subHandler = getSubTypeHandler(value.getClass(), field.getEmbedRef());
            // TODO could it be useful to write the class of the value into the field, to restore it proper from
            // datastore?
          }
        }
        subHandler.intoStore(value, field, tmpResult -> {
          if (tmpResult.failed()) {
            co.setThrowable(tmpResult.cause());
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

}
