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

package org.apache.iotdb.db.mpp.plan.statement.crud;

import org.apache.iotdb.commons.path.PartialPath;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.db.mpp.plan.statement.Statement;
import org.apache.iotdb.db.mpp.plan.statement.StatementVisitor;
import org.apache.iotdb.tsfile.common.constant.TsFileConstant;
import org.apache.iotdb.tsfile.utils.FilePathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadTsFileStatement extends Statement {
  private File file;
  private boolean autoCreateSchema;
  private int sgLevel;
  private boolean verifySchema;

  private List<File> tsFiles;

  public LoadTsFileStatement(String filePath) {
    this.file = new File(filePath);
    this.autoCreateSchema = true;
    this.sgLevel = IoTDBDescriptor.getInstance().getConfig().getDefaultStorageGroupLevel();
    this.verifySchema = true;

    tsFiles = new ArrayList<>();
    if (file.isFile()) {
      tsFiles.add(file);
    } else {
      findAllTsFile(file);
    }
    sortTsFiles(tsFiles);
  }

  private void findAllTsFile(File file) {
    for (File nowFile : file.listFiles()) {
      if (nowFile.getName().endsWith(TsFileConstant.TSFILE_SUFFIX)) {
        tsFiles.add(nowFile);
      } else if (nowFile.isDirectory()) {
        findAllTsFile(nowFile);
      }
    }
  }

  private void sortTsFiles(List<File> files) {
    Map<File, Long> file2Timestamp = new HashMap<>();
    Map<File, Long> file2Version = new HashMap<>();
    for (File file : files) {
      String[] splitStrings = file.getName().split(FilePathUtils.FILE_NAME_SEPARATOR);
      file2Timestamp.put(file, Long.parseLong(splitStrings[0]));
      file2Version.put(file, Long.parseLong(splitStrings[1]));
    }

    Collections.sort(
        files,
        (o1, o2) -> {
          long timestampDiff = file2Timestamp.get(o1) - file2Timestamp.get(o2);
          if (timestampDiff != 0) {
            return (int) (timestampDiff);
          }
          return (int) (file2Version.get(o1) - file2Version.get(o2));
        });
  }

  public void setAutoCreateSchema(boolean autoCreateSchema) {
    this.autoCreateSchema = autoCreateSchema;
  }

  public void setSgLevel(int sgLevel) {
    this.sgLevel = sgLevel;
  }

  public void setVerifySchema(boolean verifySchema) {
    this.verifySchema = verifySchema;
  }

  public List<File> getTsFiles() {
    return tsFiles;
  }

  @Override
  public List<? extends PartialPath> getPaths() {
    return Collections.emptyList();
  }

  @Override
  public <R, C> R accept(StatementVisitor<R, C> visitor, C context) {
    return visitor.visitLoadFile(this, context);
  }

  @Override
  public String toString() {
    return "LoadTsFileStatement{"
        + "file="
        + file
        + ", autoCreateSchema="
        + autoCreateSchema
        + ", sgLevel="
        + sgLevel
        + ", verifySchema="
        + verifySchema
        + ", tsFiles Size="
        + tsFiles.size()
        + '}';
  }
}
