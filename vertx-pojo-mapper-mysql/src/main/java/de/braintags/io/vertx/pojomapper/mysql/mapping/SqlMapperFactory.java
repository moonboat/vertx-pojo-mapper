package de.braintags.io.vertx.pojomapper.mysql.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObjectFactory;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlMapperFactory extends MapperFactory<Object> {

  /**
   * @param dataStore
   * @param typeHandlerFactory
   * @param propertyMapperFactory
   * @param stf
   */
  public SqlMapperFactory(IDataStore dataStore, ITypeHandlerFactory typeHandlerFactory,
      IPropertyMapperFactory propertyMapperFactory, IStoreObjectFactory<Object> stf) {
    super(dataStore, typeHandlerFactory, propertyMapperFactory, stf);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory#createMapper(java.lang.Class)
   */
  @Override
  protected <T> Mapper<T> createMapper(Class<T> mapperClass) {
    return new SqlMapper<T>(mapperClass, this);
  }

}
