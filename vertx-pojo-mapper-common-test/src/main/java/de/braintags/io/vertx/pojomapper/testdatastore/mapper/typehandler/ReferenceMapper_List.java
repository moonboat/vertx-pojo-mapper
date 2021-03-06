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

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;

/**
 * Mapper to test {@link Referenced} annotation
 *
 * @author Michael Remme
 * 
 */

@Entity
public class ReferenceMapper_List extends BaseRecord {

  @Referenced
  public List<SimpleMapper> simpleMapper;

  public ReferenceMapper_List() {
  }

  /**
   * 
   */
  public ReferenceMapper_List(int numberOfSubRecords) {
    simpleMapper = new ArrayList<SimpleMapper>();
    for (int i = 0; i < numberOfSubRecords; i++) {
      simpleMapper.add(new SimpleMapper("referencedMapperList " + i, "sec prop " + i));
    }
  }

}
