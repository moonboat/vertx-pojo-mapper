/*
 * #%L vertx-pojongo %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */

/**
 * == Implementation of pojo mapper for Mongo
 *
 * We provide an implementation of {@link de.braintags.io.vertx.pojomapper.IDataStore} which uses the Vert.x
 * {@link io.vertx.ext.mongo.MongoClient} to perform mapping of POJOs into the datastore
 *
 * To create an instance of MongoDataStore programmatically:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#example1(io.vertx.core.Vertx )}
 * ----
 * 
 * === Initialize by DataStoreSettings
 * {@link de.braintags.io.vertx.pojomapper.mongo.init}
 * 
 * 
 * The rest of the usage is the same than described in the documentation of
 * https://github.com/BraintagsGmbH/vertx-pojo-mapper/blob/master/vertx-pojo-mapper-common/src/main/asciidoc/java/index.
 * adoc[vertx-pojo-mapper-common]
 * 
 * @author Michael Remme
 * 
 */

package de.braintags.io.vertx.pojomapper.mongo;
