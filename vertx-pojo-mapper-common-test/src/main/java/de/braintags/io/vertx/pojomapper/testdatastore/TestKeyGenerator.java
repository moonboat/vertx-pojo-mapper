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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DebugGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.KeyGeneratorMapper;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.KeyGeneratorMapperDebugGenerator;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author mremme
 * 
 */
public class TestKeyGenerator extends DatastoreBaseTest {

  @Test
  public void testKeyGenerator(TestContext context) {
    if (getDataStore(context).getClass().getName().equals("de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore")) {
      // all fine
    } else if (getDataStore(context).getClass().getName()
        .equals("de.braintags.io.vertx.pojomapper.mongo.MongoDataStore")) {
      testKeyGeneratorMongo(context);
    } else {
      context.fail(new UnsupportedOperationException(
          "unsupported datastore in test: " + getDataStore(context).getClass().getName()));
    }
  }

  /**
   * @param context
   * @throws AssertionError
   */
  private void testKeyGeneratorMongo(TestContext context) throws AssertionError {
    IKeyGenerator keyGen = getDataStore(context).getDefaultKeyGenerator();
    context.assertNotNull(keyGen, "keygenerator must not be null");
    context.assertTrue(keyGen instanceof DefaultKeyGenerator,
        "not an instance of DefaultKeyGenerator: " + String.valueOf(keyGen.getName()));

    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(KeyGeneratorMapper.class);
    context.assertTrue(mapper.getKeyGenerator() instanceof DefaultKeyGenerator,
        "not an instance of DefaultKeyGenerator: " + String.valueOf(mapper.getKeyGenerator()));
    clearTable(context, "KeyGeneratorMapper");
    KeyGeneratorMapper sm = new KeyGeneratorMapper();
    sm.name = "testName";
    ResultContainer resultContainer = saveRecord(context, sm);
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), WriteAction.INSERT);
    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0); // "ID wasn't set by insert statement",
    try {
      Integer.parseInt(sm.id);
    } catch (NumberFormatException e) {
      context.fail("Not a numeric ID: " + sm.id);
    }

    sm.name = "testNameModified";
    resultContainer = saveRecord(context, sm);
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.UPDATE);
    context.assertEquals(String.valueOf(we1.getId()), String.valueOf(we.getId()),
        "id must not change after update: " + we1.getId() + " | " + we.getId());
  }

  @Test
  public void testKeyExists(TestContext context) {
    clearTable(context, "KeyGeneratorMapperDebugGenerator");
    DebugGenerator gen = (DebugGenerator) getDataStore(context).getKeyGenerator(DebugGenerator.NAME);
    gen.resetCounter();

    KeyGeneratorMapperDebugGenerator km = doInsert(context, "testName");
    int id = Integer.parseInt(km.id);
    context.assertEquals(id, 1, "expected first id as 1");
    km = doInsert(context, "testName2");
    km = doInsert(context, "testName3");
    id = Integer.parseInt(km.id);
    context.assertEquals(id, 3, "expected id as 3");

    gen.resetCounter();
    km = doInsert(context, "testId Exists");
    id = Integer.parseInt(km.id);
    context.assertEquals(id, 4, "expected first id as 4 cause of existing record");
  }

  /**
   * @param context
   * @return
   */
  private KeyGeneratorMapperDebugGenerator doInsert(TestContext context, String name) {
    KeyGeneratorMapperDebugGenerator km = new KeyGeneratorMapperDebugGenerator();
    km.name = name;
    ResultContainer resultContainer = saveRecord(context, km);
    checkWriteAction(context, resultContainer, WriteAction.INSERT);
    return km;
  }

  private void checkWriteAction(TestContext context, ResultContainer resultContainer, WriteAction we) {
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), we);
  }

}
