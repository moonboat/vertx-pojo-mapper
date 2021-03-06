/*
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.impl;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryLogicTranslator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryOperatorTranslator;
import de.braintags.io.vertx.pojomapper.exception.UnsupportedKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContextFactory;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.TriggerContextFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DebugGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.util.security.crypt.IEncoder;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * An abstract implementation of {@link IDataStore}
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractDataStore implements IDataStore {
  private Vertx vertx;
  private JsonObject properties;
  private IMapperFactory mapperFactory;
  private ITableGenerator tableGenerator;
  private IDataStoreSynchronizer dataStoreSynchronizer;
  private Map<String, IKeyGenerator> keyGeneratorMap = new HashMap<>();
  private ITriggerContextFactory triggerContextFactory = new TriggerContextFactory();
  private IQueryLogicTranslator logicTranslator;
  private IQueryOperatorTranslator queryOperatorTranslator;
  private Map<String, IEncoder> encoderMap = new HashMap<>();

  /**
   * Create a new instance. The possible properties are defined by its concete implementation
   * 
   * @param vertx
   *          the instance if {@link Vertx} used
   * @param properties
   *          the properties by which the new instance is created
   */
  public AbstractDataStore(Vertx vertx, JsonObject properties, IQueryLogicTranslator logicTranslator,
      IQueryOperatorTranslator queryOperatorTranslator) {
    this.vertx = vertx;
    this.properties = properties;
    this.logicTranslator = logicTranslator;
    this.queryOperatorTranslator = queryOperatorTranslator;
    initSupportedKeyGenerators();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getMapperFactory()
   */
  @Override
  public final IMapperFactory getMapperFactory() {
    return mapperFactory;
  }

  /**
   * @param mapperFactory
   *          the mapperFactory to set
   */
  protected final void setMapperFactory(IMapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }

  /**
   * @return the tableGenerator
   */
  @Override
  public final ITableGenerator getTableGenerator() {
    return tableGenerator;
  }

  /**
   * @param tableGenerator
   *          the tableGenerator to set
   */
  public final void setTableGenerator(ITableGenerator tableGenerator) {
    this.tableGenerator = tableGenerator;
  }

  /**
   * @return the dataStoreSynchronizer
   */
  @Override
  public final IDataStoreSynchronizer getDataStoreSynchronizer() {
    return dataStoreSynchronizer;
  }

  /**
   * @param dataStoreSynchronizer
   *          the dataStoreSynchronizer to set
   */
  public final void setDataStoreSynchronizer(IDataStoreSynchronizer dataStoreSynchronizer) {
    this.dataStoreSynchronizer = dataStoreSynchronizer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getProperties()
   */
  @Override
  public JsonObject getProperties() {
    return properties;
  }

  /**
   * Add an {@link IKeyGenerator} supported by the current instance
   * 
   * @param generator
   */
  protected void addSupportedKeyGenerator(IKeyGenerator generator) {
    keyGeneratorMap.put(generator.getName(), generator);
  }

  /**
   * Define all {@link IKeyGenerator}, which are supported by the current instance by using the method
   * {@link #addSupportedKeyGenerator(IKeyGenerator)}
   */
  protected void initSupportedKeyGenerators() {
    addSupportedKeyGenerator(new DebugGenerator(this));
    addSupportedKeyGenerator(new DefaultKeyGenerator(this));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getKeyGenerator(java.lang.String)
   */
  @Override
  public final IKeyGenerator getKeyGenerator(String generatorName) {
    if (keyGeneratorMap.containsKey(generatorName)) {
      return keyGeneratorMap.get(generatorName);
    }
    throw new UnsupportedKeyGenerator("This generator is not supported by the current datastore: " + generatorName);
  }

  @Override
  public Vertx getVertx() {
    return vertx;
  }

  /**
   * @return the triggerContextFactory
   */
  @Override
  public final ITriggerContextFactory getTriggerContextFactory() {
    return triggerContextFactory;
  }

  /**
   * @param triggerContextFactory
   *          the triggerContextFactory to set
   */
  @Override
  public final void setTriggerContextFactory(ITriggerContextFactory triggerContextFactory) {
    this.triggerContextFactory = triggerContextFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getQueryLogicTranslator()
   */
  @Override
  public IQueryLogicTranslator getQueryLogicTranslator() {
    return logicTranslator;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getQueryOperatorTranslator()
   */
  @Override
  public IQueryOperatorTranslator getQueryOperatorTranslator() {
    return queryOperatorTranslator;
  }

  /**
   * The map contains the {@link IEncoder} which are defined for the current instance
   * 
   * @return the encoderMap
   */
  public Map<String, IEncoder> getEncoderMap() {
    return encoderMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getEncoder(java.lang.String)
   */
  @Override
  public IEncoder getEncoder(String name) {
    return getEncoderMap().get(name);
  }

}
