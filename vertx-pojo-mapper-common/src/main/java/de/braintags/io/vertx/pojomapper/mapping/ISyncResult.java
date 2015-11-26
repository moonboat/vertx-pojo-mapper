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

package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * ISyncResult is the result, which is returned by the method
 * {@link IDataStoreSynchronizer#synchronize(IMapper, io.vertx.core.Handler)} and which contains all relevant
 * information for the synchronization process
 * 
 * @author Michael Remme
 * 
 * @param T
 *          the dataformat which is used as sync command
 * 
 */

public interface ISyncResult<T> {

  /**
   * Retrieve the native object which was used as command to synchronize the connected table / column of the
   * {@link IDataStore} If no synchronization was performed, then this will be null
   * 
   * @return the native object which was used to synchronize or null, if no synchronization was performed
   */
  public T getSyncCommand();

  /**
   * Get the {@link SyncAction} which was performed by a synchronization
   * 
   * @return
   */
  public SyncAction getAction();

}
