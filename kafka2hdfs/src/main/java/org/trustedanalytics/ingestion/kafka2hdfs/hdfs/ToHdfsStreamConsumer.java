/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.ingestion.kafka2hdfs.hdfs;

import org.trustedanalytics.ingestion.kafka2hdfs.core.StreamConsumer;

import com.google.common.base.Preconditions;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class ToHdfsStreamConsumer implements StreamConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ToHdfsStreamConsumer.class);

    private final static byte[] SEPARATOR =
            System.lineSeparator().getBytes(Charset.defaultCharset());

    private final FileSystem fs;
    private final Path filePath;
    private FSDataOutputStream out;

    public ToHdfsStreamConsumer(FileSystem fs, String filePath) {
        this.fs = fs;
        this.filePath = new Path(fs.getWorkingDirectory(), filePath);
        out = null;
    }

    @Override
    public void init() throws IOException {
        Preconditions.checkState(out == null, "init called more then once");
        fs.createNewFile(filePath);
        out = fs.append(filePath);
        LOGGER.info("Opened stream for {}", filePath);
    }

    @Override
    public void cleanUp() throws IOException {
        if (out != null) {
            try {
                out.close();
                LOGGER.info("Closed stream for {}", filePath);
            } catch (IOException ex) {
                LOGGER.warn("Failed closing the stream for {}", filePath, ex);
            }
        } else {
            LOGGER.info("No need to close stream for {}", filePath);
        }
    }

    @Override
    public void consumeMessage(byte[] message) throws IOException {
        out.write(message);
        // shall we append new line?
        out.write(SEPARATOR);
        out.hsync();
    }
}
