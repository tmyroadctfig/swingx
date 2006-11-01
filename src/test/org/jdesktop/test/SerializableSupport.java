/*
 * Created on 24.08.2006
 *
 */
package org.jdesktop.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Support class to test serializable behaviour.
 * 
 *  @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class SerializableSupport {

    /**
     * Serialize the specified object to bytes, then deserialize it back.
     */
    public static <T> T serialize(T object) throws IOException, ClassNotFoundException {
        return (T)fromBytes(toBytes(object));
    }

    public static byte[] toBytes(Object object) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream objectsOut = new ObjectOutputStream(bytesOut);
        objectsOut.writeObject(object);
        return bytesOut.toByteArray();
    }

    public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objectsIn = new ObjectInputStream(bytesIn);
        return objectsIn.readObject();
    }

}
