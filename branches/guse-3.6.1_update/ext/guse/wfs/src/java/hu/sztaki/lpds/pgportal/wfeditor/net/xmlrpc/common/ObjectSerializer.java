/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// import sun.misc.BASE64Decoder;
// import sun.misc.BASE64Encoder;

/**
 * Objektumok serializalasat - deserializalasat vegzi el
 *
 * @author lpds
 */
public class ObjectSerializer {

    public ObjectSerializer() {
    }

    /**
     * serialize object to string
     */
    public static String serializeObjectToSting(Object obj) {
        String retString = new String();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOUT = new ObjectOutputStream(out);
            objOUT.writeObject(obj);
            byte[] buff = out.toByteArray();
            // retString = new BASE64Encoder().encodeBuffer(buff);
            retString = new String().valueOf(Base64Coder.encode(buff));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retString;
    }

    /**
     * serialize object to byte[]
     */
    public static byte[] serializeObjectToBytes(Object obj) {
        byte[] buff = new byte[0];
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOUT = new ObjectOutputStream(out);
            objOUT.writeObject(obj);
            buff = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buff;
    }

    /**
     * deserialize object from string
     */
    public static Object deserializeObjectFromString(String stringbuff) {
        Object retObject = new Object();
        try {
            // byte[] buff = new BASE64Decoder().decodeBuffer(stringbuff);
            byte[] buff = Base64Coder.decode(stringbuff);
            ByteArrayInputStream in = new ByteArrayInputStream(buff);
            ObjectInputStream objIN = new ObjectInputStream(in);
            retObject = (Object) objIN.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retObject;
    }

    /**
     * deserialize object from byte[]
     */
    public static Object deserializeObjectFromBytes(byte[] buff) {
        Object retObject = new Object();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(buff);
            ObjectInputStream objIN = new ObjectInputStream(in);
            retObject = (Object) objIN.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retObject;
    }

}
