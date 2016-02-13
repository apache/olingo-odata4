/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.ws.rs.NotFoundException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.client.core.serialization.AtomSerializer;
import org.apache.olingo.client.core.serialization.JsonSerializer;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSManager {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractUtilities.class);

  private static String MEM_PREFIX = "ram://";

  private static String RES_PREFIX = "res://";

  private final FileSystemManager fsManager;

  private static FSManager instance = null;

  public static FSManager instance() throws IOException {
    if (instance == null) {
      instance = new FSManager();
    }
    return instance;
  }

  private FSManager() throws IOException {
    fsManager = VFS.getManager();

    final FileObject basePath =
        fsManager.resolveFile(RES_PREFIX + File.separatorChar + ODataServiceVersion.V40.name());
    final String absoluteBaseFolder = basePath.getURL().getPath();

    for (FileObject fo : find(basePath, null)) {
      if (fo.getType() == FileType.FILE
          && !fo.getName().getBaseName().contains("Metadata")
          && !fo.getName().getBaseName().contains("metadata")) {
        final String path = fo.getURL().getPath().replace(absoluteBaseFolder, "//" + ODataServiceVersion.V40.name());
        putInMemory(fo.getContent().getInputStream(), path);
      }
    }
  }

  public String getAbsolutePath(final String relativePath, final Accept accept) {
    return File.separatorChar + ODataServiceVersion.V40.name() + File.separatorChar + relativePath
        + (accept == null ? "" : accept.getExtension());
  }

  public final FileObject putInMemory(final InputStream is, final String path) throws IOException {
    LOG.info("Write in memory {}", path);
    final FileObject memObject = fsManager.resolveFile(MEM_PREFIX + path);

    if (memObject.exists()) {
      memObject.delete();
    }

    // create in-memory file
    memObject.createFile();

    // read in-memory content
    final OutputStream os = memObject.getContent().getOutputStream();
    IOUtils.copy(is, os);
    IOUtils.closeQuietly(is);
    IOUtils.closeQuietly(os);

    return memObject;
  }

  public void putInMemory(final ResWrap<Entity> container, final String relativePath)
      throws IOException, ODataSerializerException {
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

    new AtomSerializer(true).write(writer, container);
    writer.flush();

    putInMemory(new ByteArrayInputStream(content.toByteArray()), getAbsolutePath(relativePath, Accept.ATOM));
    content.reset();

    new JsonSerializer(true, ContentType.JSON_FULL_METADATA).write(writer, container);
    writer.flush();

    putInMemory(new ByteArrayInputStream(content.toByteArray()), getAbsolutePath(relativePath, Accept.JSON_FULLMETA));
  }

  public InputStream readRes(final String relativePath, final Accept accept) {
    return readFile(relativePath, accept, RES_PREFIX);
  }

  public InputStream readFile(final String relativePath, final Accept accept) {
    return readFile(relativePath, accept, MEM_PREFIX);
  }

  public InputStream readFile(final String relativePath) {
    return readFile(relativePath, null, MEM_PREFIX);
  }

  private InputStream readFile(final String relativePath, final Accept accept, final String fs) {
    final String path = getAbsolutePath(relativePath, accept);
    LOG.info("Read {}{}", fs, path);

    try {
      final FileObject fileObject = fsManager.resolveFile(fs + path);

      if (fileObject.exists()) {
        // return new in-memory content
        return fileObject.getContent().getInputStream();
      } else {
        LOG.warn("In-memory path '{}' not found", path);
        throw new NotFoundException();
      }
    } catch (FileSystemException e) {
      throw new NotFoundException();
    }
  }

  public void deleteFile(final String relativePath) {
    for (Accept accept : Accept.values()) {
      final String path = getAbsolutePath(relativePath, accept);
      LOG.info("Delete {}", path);

      try {
        final FileObject fileObject = fsManager.resolveFile(MEM_PREFIX + path);

        if (fileObject.exists()) {
          fileObject.delete();
        }
      } catch (IOException ignore) {
        // ignore exception
      }
    }
  }

  public void deleteEntity(final String relativePath) {
    final String path = getAbsolutePath(relativePath, null);
    LOG.info("Delete {}", path);

    try {
      final FileObject fileObject = fsManager.resolveFile(MEM_PREFIX + path);

      if (fileObject.exists()) {
        fileObject.delete(new FileSelector() {
          @Override
          public boolean includeFile(final FileSelectInfo fileInfo) throws Exception {
            return true;
          }

          @Override
          public boolean traverseDescendents(final FileSelectInfo fileInfo) throws Exception {
            return true;
          }
        });
      }
    } catch (IOException ignore) {
      // ignore exception
    }
  }

  public FileObject resolve(final String path) throws FileSystemException {
    final FileObject res = fsManager.resolveFile(MEM_PREFIX + path);

    if (!res.exists()) {
      throw new FileSystemException("Unresolved path " + path);
    }

    return res;
  }

  public final FileObject[] find(final FileObject fo, final String ext) throws FileSystemException {
    return fo.findFiles(new FileSelector() {
      @Override
      public boolean includeFile(final FileSelectInfo fileInfo) throws Exception {
        return ext == null ? true : fileInfo.getFile().getName().getExtension().equals(ext);
      }

      @Override
      public boolean traverseDescendents(final FileSelectInfo fileInfo) throws Exception {
        return true;
      }
    });
  }
}
