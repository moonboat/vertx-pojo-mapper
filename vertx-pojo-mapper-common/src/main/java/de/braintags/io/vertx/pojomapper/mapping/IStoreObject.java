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
package de.braintags.io.vertx.pojomapper.mapping;

import java.util.Collection;

import de.braintags.io.vertx.pojomapper.IDataStore;
import io.vertx.codegen.annotations.Fluent;

/**
 * The IStoreObject is building the bridge between the propriate format coming from out of the used {@link IDataStore}
 * and the rest of the application. This can be a Json object or an internal format for later use of a ResultSet for
 * instance
 * 
 * @author Michael Remme
 * @param <T>
 *          the type, which is used internally as format, like Json for instance
 */

public interface IStoreObject<T> {

  /**
   * Checks wether for the given IField an entry is defined inside the current instance
   * 
   * @param field
   *          the {@link IField} to be checked
   * @return true, if property is defined
   */
  public boolean hasProperty(IField field);

  /**
   * Get the defined property in the propriate format of the current driver
   * 
   * @param field
   *          the {@link IField} describing the property
   * @return the value of the property in the propriate format of the current implementation of the {@link IDataStore}
   */
  Object get(IField field);

  /**
   * Adds a new property into the internal container
   * 
   * @param field
   *          the {@link IField} used to describe the property
   * @param value
   *          the value to be stored in the proprietary format
   * @return a reference to itself
   */
  @Fluent
  IStoreObject<T> put(IField field, Object value);

  /**
   * Get the raw container, which stores the information
   * 
   * @return the container
   */
  T getContainer();

  /**
   * Get the POJO entity, whihc was created from the stored information in the {@link IDataStore}
   * 
   * @return the entity
   */
  Object getEntity();

  @Override
  String toString();

  /**
   * Retrive all instances of {@link IObjectReference} which were generated when rereading an instance from the
   * datastore.
   * 
   * @return all instances of {@link IObjectReference}
   */
  Collection<IObjectReference> getObjectReferences();

  /**
   * Get the information, wether the current instance is existing in the datastore or not. By this information it is
   * decided, wether the record is inserted or updated
   * 
   * @return true, if it exists already, false if not
   */
  boolean isNewInstance();
}
