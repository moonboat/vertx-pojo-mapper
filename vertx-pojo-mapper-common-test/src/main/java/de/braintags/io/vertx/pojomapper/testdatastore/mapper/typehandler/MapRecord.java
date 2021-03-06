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

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class MapRecord extends BaseRecord {
  public Map<String, String> map = new HashMap<String, String>();

  public MapRecord() {
    map.put("Eins", "1");
    map.put("Zwei", "2");
    map.put("Drei", "3");
  }

}
