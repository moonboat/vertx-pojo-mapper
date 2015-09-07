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
/**
 * == vertx pojo mapper common - Map Java objects into datastores and back
 * 
 * This module contains common interfaces and default implementations for pojo mapper.
 *
 * To use this project, add the following dependency to the _dependencies_ section of your build descriptor:
 *
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>de.braintags</groupId>
 *   <artifactId>vertx-pojo-mapper</artifactId>
 *   <version>{maven-version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * compile {maven-groupId}:{maven-artifactId}:{maven-version}
 * ----
 *
 * == Quick-Start
 * Although this library will deal with completely different types of datastores lioke SQL, NoSQL etc. we are using some idioms inside the 
 * further description, which are more or less database depending. Thus a "table" will refer to a table inside a MySQL for instance and to a
 * collection inside a MongoDB - just to avoid stringy text constructions.
 *
 * === Initializing an IDataStore 
 * The initialization of the {@link de.braintags.io.vertx.pojomapper.IDataStore} is the only action, where you
 * are directly referring to a concrete implementation. 
 * See https://github.com/BraintagsGmbH/vertx-pojo-mapper/blob/master/vertx-pojongo/src/main/asciidoc/java/index.adoc[vertx-pojongo] 
 * to initialize an {@link de.braintags.io.vertx.pojomapper.IDataStore} which works with MongoDB.
 * 
 * === Create a mapper
 * See the mapper examples.mapper.SimpleMapper inside the current module. 
 * The only required information in a Mapper is the annotation de.braintags.io.vertx.pojomapper.annotation.field.Id,
 * which flags one field as the primary key field of a mapper. All other annotations which are described later are optional.
 * 
 * === Creating an instance
 * Create and fill a typical Java object like always
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example2()}
 * ----
 * 
 * === Saving data
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example3(de.braintags.io.vertx.pojomapper.IDataStore, examples.mapper.SimpleMapper )}
 * ----
 * 
 * To save one or more instances inside the datastore, we first create an {@link de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite}. 
 * By creating the IWrite the system will automatically perform the mapping process, if not done already.
 * After adding the instance into the IWrite, we are able to execute the action, which will return an {@link de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult},
 * by which we are getting further information about the action, like the generated id of the record and wether it was inserted
 * or updated, for instance.
 * 
 * === Searching data
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example4(de.braintags.io.vertx.pojomapper.IDataStore )}
 * ----
 * 
 * To search in the datastore, we are creating first an instance of {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery}
 * and define the query arguments. In the current example we are only searching for the name, but cause IQuery supports a fluent api
 * we could simply and quickly add further arguments.
 * Again - with the creation of the IQuery - the system checks wether the class was mapped already and performs the mapping if not.
 * The query is processed by calling the execute method, which in turn will deliver an {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult},
 * which contains several information like the native query and a reference to found records.
 * The found records can be requested step by step by an Iterator or once as Array by requesting the method toArray. Both methods
 * are requiring a Handler, since only during this request the Java object is created if not done already. For complexer objects this can
 * mean, that further informations must be loaded from the IDataStore.
 * 
 * 
 * === Deleting data
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example5(de.braintags.io.vertx.pojomapper.IDataStore, examples.mapper.SimpleMapper )}
 * ----
 * 
 * Deletion is processed either by deleting concrete objects or by using an {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery} as argument. Mixing of both is not possible.
 * In the current example we are deleting an object, which we are expecting to exist in the datastore.
 * First we are creating an {@link de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete} and add the instance to be deleted. The execution od mthe delete is processed by calling method
 * delete, which will return an instance of {@link de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult}. The method {@link de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getOriginalCommand()}
 * returns the native arguments which were used to perform the delete action
 * 
 * ----
 * {@link examples.Examples#example6(de.braintags.io.vertx.pojomapper.IDataStore )}
 * ----
 * 
 * This example shows how to perform a delete action by using an {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery}. All records, which are fitting the arguments of the query are deleted.
 *
 *
 * == Working with vertx-pojo-mapper
 * 
 * === Mapping of Java classes
 * There is no need to start a special mapping process in your application. The mapping of Java classes is automatically performed at the
 * moment, when it is needed.
 * During the mapping process the class is inspected for several information. The persistent fields of a mapper are generated by inspecting
 * public fields and BeanProperties. The rest of the configuration of a mapper is done by using annotations. Annotations are always added
 * to a field or the Class itself. Even annotations for those properties, which aree defined as getter / setter-method are added to the 
 * underlaying field of the property.
 * 
 * You will find some mapper definitions in the example package, for instance:
 * 
 *  * {@link examples.mapper.SimpleMapper} as a very simple mapper
 *  * {@link examples.mapper.DemoMapper} as an example for referenced and embedded usage
 *  
 *  Existing annotations are:
 * 
 * ==== @Entity ( name = "tableName" )
 * By annotating a class with de.braintags.io.vertx.pojomapper.annotation.Entity you are able to set the name of the table
 * which is used to store the information in the {@link de.braintags.io.vertx.pojomapper.IDataStore}. By default the system will use the short classname of the mapper.
 * 
 * ==== @Id
 * One field of the mapper must be annotated by de.braintags.io.vertx.pojomapper.annotation.field.Id, which will mark the annotated field
 * as primary key
 * 
 * ==== @Property 
 * Properties of a mapper are stored inside the {@link de.braintags.io.vertx.pojomapper.IDataStore} by using the fieldname by default. 
 * By annotating a field with de.braintags.io.vertx.pojomapper.annotation.field.Property you are able to modify the name of the column 
 * in the table.
 * 
 * ==== @Referenced
 * This annotation is used to mark a field, so that values of this field are stored inside a separate table and that those values are referenced by their id
 * inside the stored result. 
 * 
 * ==== @Embedded
 * This annotation is used to mark a field, so that values of that field are stored directly as content of the given field.
 * 
 * ==== @ObjectFactory
 * By default the {@link de.braintags.io.vertx.pojomapper.mapping.IObjectFactory} is defined inside each {@link de.braintags.io.vertx.pojomapper.mapping.IMapper} by
 * using a default implementation. If you need another implementation you are able to set it by adding this annotation to the mapper class and reference the
 * class of the {@link de.braintags.io.vertx.pojomapper.mapping.IObjectFactory} you want to use.
 * 
 * ==== @AfterLoad
 * All methods, which are annotated by this annotation are executed after an instance was loaded from the {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ==== @BeforeSave
 * All methods, which are annotated by this annotation are executed before an instance is saved into the {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ==== @AfterSave
 * All methods, which are annotated by this annotation are executed after an instance was saved into the {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ==== @BeforeDelete
 * All methods, which are annotated by this annotation are executed before an instance is deleted from the {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ==== @AfterDelete
 * All methods, which are annotated by this annotation are executed after an instance was deleted from the {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * 
 * ==== @ConcreteClass
 * not yet supported
 * 
 * ==== @ConstructorArguments
 * to be tested
 * 
 * ==== @Indexes
 * not yet implemented
 * 
 * 
 * 
 * == Creating a new implementation 
 * tbd
 *
 * @author Michael Remme
 */
@Document(fileName = "index.adoc")
@GenModule(name = "vertx-pojo-mapper-common", groupPackageName = "de.braintags")
package de.braintags.io.vertx.pojomapper;

import io.vertx.codegen.annotations.GenModule;
import io.vertx.docgen.Document;

