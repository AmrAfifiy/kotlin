// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.gradle.tooling.serialization;

import org.gradle.internal.impldep.org.apache.commons.io.input.ClassLoaderObjectInputStream;

import java.io.*;

/**
 * @author Vladislav.Soroka
 */
public class DefaultSerializationService implements SerializationService {
  @Override
  public byte[] write(Object object, Class modelClazz) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ObjectOutputStream outputStream = new ObjectOutputStream(os);
    try {
      outputStream.writeObject(object);
    }
    finally {
      outputStream.close();
    }
    return os.toByteArray();
  }

  @Override
  public Object read(byte[] object, final Class modelClazz) throws IOException {
    ObjectInputStream inputStream = new ClassLoaderObjectInputStream(modelClazz.getClassLoader(), new ByteArrayInputStream(object));
    try {
      return inputStream.readObject();
    }
    catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
    finally {
      inputStream.close();
    }
  }

  @Override
  public Class getModelClass() {
    throw new IllegalStateException("The method should never be called for this serializer service implementation");
  }
}
