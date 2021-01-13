package cwh;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class HelloClassLoader extends ClassLoader {

  public static void main(String[] args) {
    try {
      // Class<?> helloClass = new HelloClassLoader().findClass("Hello");
      Object helloObject = new HelloClassLoader().findClass("Hello").newInstance();
      Method helloMethod = helloObject.getClass().getDeclaredMethod("hello", null);
      helloMethod.invoke(helloObject);
    } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    String path = name + ".xlass";
    File file = new File(path);
    try {
      byte[] buff = Files.readAllBytes(file.toPath());
      byte[] converted = convert(buff);
      return defineClass(name, converted, 0, converted.length);
    } catch (IOException e) {
      e.printStackTrace();
      throw new ClassNotFoundException();
    }
  }

  private byte[] convert(byte[] buff) {
    byte[] converted = new byte[buff.length];
    for (int i = 0; i < buff.length; i++) {
      converted[i] = (byte) (255 - buff[i]);
    }
    return converted;
  }
}
