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
package de.braintags.io.vertx.pojomapper.testdatastore.typehandler.json;

import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.EmbeddedMapper_Single;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.SimpleMapperEmbedded;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedSingleTest extends EmbeddedSingleTest_Null {

  /**
   * 
   */
  public EmbeddedSingleTest() {
  }

  @Override
  @Test
  public void extreme(TestContext context) {
    clearTable(context, EmbeddedMapper_Single.class.getSimpleName());
    EmbeddedMapper_Single record = new EmbeddedMapper_Single();
    record.simpleMapper = null;
    saveRecord(context, record);
    IQuery<EmbeddedMapper_Single> query = getDataStore(context).createQuery(EmbeddedMapper_Single.class);
    List list = findAll(context, query);
    context.assertEquals(1, list.size());
    EmbeddedMapper_Single loaded = (EmbeddedMapper_Single) list.get(0);
    context.assertNull(loaded.simpleMapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.testdatastore.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance(TestContext context) {
    EmbeddedMapper_Single mapper = new EmbeddedMapper_Single();
    mapper.simpleMapper = new SimpleMapperEmbedded("testname", "secnd prop");
    return mapper;
  }
}
