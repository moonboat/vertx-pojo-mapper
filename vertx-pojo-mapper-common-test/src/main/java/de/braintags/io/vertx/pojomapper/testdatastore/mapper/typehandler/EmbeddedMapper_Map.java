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
package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import java.util.HashMap;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class EmbeddedMapper_Map extends BaseRecord {

  @Embedded
  public Map<Integer, SimpleMapperEmbedded> simpleMapper;

  /**
   * 
   */
  public EmbeddedMapper_Map() {
    simpleMapper = new HashMap<Integer, SimpleMapperEmbedded>();
    for (int i = 0; i < 5; i++) {
      simpleMapper.put(i, new SimpleMapperEmbedded("name " + i, "sec prop " + i));
    }
  }

}
