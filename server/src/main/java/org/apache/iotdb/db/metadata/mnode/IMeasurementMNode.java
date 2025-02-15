/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.db.metadata.mnode;

import org.apache.iotdb.db.engine.trigger.executor.TriggerExecutor;
import org.apache.iotdb.db.metadata.lastCache.container.ILastCacheContainer;
import org.apache.iotdb.db.metadata.path.MeasurementPath;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.write.schema.IMeasurementSchema;

/** This interface defines a MeasurementMNode's operation interfaces. */
public interface IMeasurementMNode extends IMNode {

  @Override
  IEntityMNode getParent();

  MeasurementPath getMeasurementPath();

  IMeasurementSchema getSchema();

  TSDataType getDataType(String measurementId);

  String getAlias();

  void setAlias(String alias);

  long getOffset();

  void setOffset(long offset);

  TriggerExecutor getTriggerExecutor();

  void setTriggerExecutor(TriggerExecutor triggerExecutor);

  ILastCacheContainer getLastCacheContainer();

  void setLastCacheContainer(ILastCacheContainer lastCacheContainer);

  boolean isPreDeleted();

  void setPreDeleted(boolean preDeleted);
}
