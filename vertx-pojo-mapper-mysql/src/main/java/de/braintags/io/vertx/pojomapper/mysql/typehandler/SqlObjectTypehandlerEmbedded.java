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
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObject;
import de.braintags.io.vertx.pojomapper.mysql.dataaccess.SqlStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Michael Remme
 * 
 */

public class SqlObjectTypehandlerEmbedded extends ObjectTypeHandlerEmbedded {

  /**
   * @param typeHandlerFactory
   */
  public SqlObjectTypehandlerEmbedded(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object dbValue, IField field, Class<?> cls, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    try {
      JsonObject jsonObject = new JsonObject((String) dbValue);
      super.fromStore(jsonObject, field, cls, handler);
    } catch (Exception e) {
      fail(e, handler);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.json.typehandler.handler.ObjectTypeHandlerEmbedded#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @Override
  public void intoStore(Object embeddedObject, IField field, Handler<AsyncResult<ITypeHandlerResult>> handler) {
    super.intoStore(embeddedObject, field, result -> {
      if (result.failed()) {
        handler.handle(result);
      }
      try {
        String newResult = ((JsonObject) result.result().getResult()).encode();
        success(newResult, handler);
      } catch (Exception e) {
        fail(e, handler);
      }
    });
  }

  @Override
  protected void writeSingleValueAsMapper(IDataStore store, Object embeddedObject, IMapper embeddedMapper, IField field,
      Handler<AsyncResult<ITypeHandlerResult>> handler) {
    checkId(store, embeddedObject, embeddedMapper, idResult -> {
      if (idResult.failed()) {
        fail(idResult.cause(), handler);
      } else {
        ((SqlStoreObjectFactory) store.getMapperFactory().getStoreObjectFactory()).createStoreObject(embeddedMapper,
            embeddedObject, result -> {
              if (result.failed()) {
                fail(result.cause(), handler);
              } else {
                success(((SqlStoreObject) result.result()).getContainerAsJson(), handler);
              }
            });
      }
    });

  }

  private void checkId(IDataStore store, Object embeddedObject, IMapper mapper, Handler<AsyncResult<Void>> handler) {
    IField field = mapper.getIdField();
    Object id = field.getPropertyAccessor().readData(embeddedObject);
    if (id != null) {
      handler.handle(Future.succeededFuture());
    } else {
      store.getDefaultKeyGenerator().generateKey(mapper, keyResult -> {
        if (keyResult.failed()) {
          handler.handle(Future.failedFuture(keyResult.cause()));
        } else {
          field.getTypeHandler().fromStore(keyResult.result().getKey(), field, null, result -> {
            if (result.failed()) {
              handler.handle(Future.failedFuture(result.cause()));
            } else {
              Object javaValue = result.result().getResult();
              field.getPropertyAccessor().writeData(embeddedObject, javaValue);
              handler.handle(Future.succeededFuture());
            }
          });
        }
      });
    }
  }
}
