package com.guozz;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class HelloXlassLoader extends ClassLoader{

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            Class clazz = new HelloXlassLoader().findClass("Hello");
            Object object = clazz.newInstance();
            Method hello = clazz.getDeclaredMethod("hello");
            hello.invoke(object);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String filePath = Objects.requireNonNull(HelloXlassLoader.class.getClassLoader().getResource("Hello.xlass")).getPath();
        File file = new File(filePath);

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();
            byte[] newBytes = decode(bytes);
            return defineClass(name, newBytes, 0, newBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public byte[] decode(byte[] oldBytes) {
        byte [] newArray = new byte[oldBytes.length];
        for (int i = 0; i < oldBytes.length; i ++) {
            newArray[i] = (byte) (255 - oldBytes[i]);
        }
        return newArray;
    }
}
