package de.braintags.io.vertx.pojomapper.mongo.init;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.mapping.ISyncCommand;
import de.braintags.io.vertx.pojomapper.mapping.ISyncResult;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link ISyncResult} for mongodb, which uses Json as format
 * 
 * @author Michael Remme
 * 
 */
public class MongoSyncResult implements ISyncResult<JsonObject> {
  private List<ISyncCommand<JsonObject>> commands = new ArrayList<>();

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.ISyncResult#getCommands()
   */
  @Override
  public List<ISyncCommand<JsonObject>> getCommands() {
    return commands;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.ISyncResult#addCommand(de.braintags.io.vertx.pojomapper.mapping.
   * ISyncCommand)
   */
  @Override
  public void addCommand(ISyncCommand<JsonObject> command) {
    commands.add(command);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.ISyncResult#isUnmodified()
   */
  @Override
  public boolean isUnmodified() {
    return commands.isEmpty();
  }

}
