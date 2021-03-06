/*
 * This file was automatically generated by EvoSuite
 * Wed Aug 23 18:52:22 GMT 2017
 */


import org.junit.Test;
import static org.junit.Assert.*;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Set;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.mock.java.io.MockFileOutputStream;
import org.evosuite.runtime.mock.java.io.MockPrintStream;
import org.evosuite.runtime.mock.java.io.MockPrintWriter;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class Properties_ESTest extends Properties_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      Properties properties0 = new Properties();
      properties0.setProperty("vp-B& :jru1W{{oau", "vp-B& :jru1W{{oau");
      Set<String> set0 = properties0.stringPropertyNames();
      assertFalse(set0.isEmpty());
  }

  @Test(timeout = 4000)
  public void test01()  throws Throwable  {
      Properties properties0 = new Properties();
      Properties properties1 = new Properties(properties0);
      Set<String> set0 = properties1.stringPropertyNames();
      assertEquals(0, set0.size());
  }

  @Test(timeout = 4000)
  public void test02()  throws Throwable  {
      Properties properties0 = new Properties();
      Properties properties1 = new Properties(properties0);
      MockPrintWriter mockPrintWriter0 = new MockPrintWriter("...");
      properties1.list((PrintWriter) mockPrintWriter0);
      assertTrue(properties1.isEmpty());
  }

  @Test(timeout = 4000)
  public void test03()  throws Throwable  {
      Properties properties0 = new Properties();
      properties0.setProperty("]c28>hC=k=cA0g[$i", "");
      MockPrintStream mockPrintStream0 = new MockPrintStream("-- listing properties --");
      properties0.list((PrintStream) mockPrintStream0);
      assertFalse(properties0.isEmpty());
  }

  @Test(timeout = 4000)
  public void test04()  throws Throwable  {
      Properties properties0 = new Properties();
      Properties properties1 = new Properties(properties0);
      String string0 = properties1.getProperty("");
      assertNull(string0);
  }

  @Test(timeout = 4000)
  public void test05()  throws Throwable  {
      Properties properties0 = new Properties();
      properties0.setProperty("", "p[]ZE%P.#s");
      String string0 = properties0.getProperty("", "");
      assertEquals("p[]ZE%P.#s", string0);
  }

  @Test(timeout = 4000)
  public void test06()  throws Throwable  {
      Properties properties0 = new Properties();
      String string0 = properties0.getProperty("=", "=");
      assertEquals("=", string0);
  }

  @Test(timeout = 4000)
  public void test07()  throws Throwable  {
      Properties properties0 = new Properties();
      MockPrintWriter mockPrintWriter0 = new MockPrintWriter("Gid%UbTgCbd'sP'5Z");
      BufferedWriter bufferedWriter0 = new BufferedWriter(mockPrintWriter0);
      properties0.store((Writer) bufferedWriter0, "Gid%UbTgCbd'sP'5Z");
      assertEquals(0, properties0.size());
  }

  @Test(timeout = 4000)
  public void test08()  throws Throwable  {
      Properties properties0 = new Properties();
      properties0.setProperty("h]DHt*i=@B)\"", "h]DHt*i=@B)\"");
      MockFileOutputStream mockFileOutputStream0 = new MockFileOutputStream("qK#q@u,]kU");
      properties0.store((OutputStream) mockFileOutputStream0, "");
      assertFalse(properties0.isEmpty());
  }

  @Test(timeout = 4000)
  public void test09()  throws Throwable  {
      Properties properties0 = new Properties();
      properties0.setProperty("TO", "O,Sh\"+S_>pt*?j!A");
      MockPrintWriter mockPrintWriter0 = new MockPrintWriter("TO");
      properties0.store((Writer) mockPrintWriter0, " &U,58WtI`");
      assertEquals(1, properties0.size());
  }

  @Test(timeout = 4000)
  public void test10()  throws Throwable  {
      Properties properties0 = new Properties();
      byte[] byteArray0 = new byte[4];
      ByteArrayInputStream byteArrayInputStream0 = new ByteArrayInputStream(byteArray0);
      properties0.load((InputStream) byteArrayInputStream0);
      assertEquals(1, properties0.size());
  }

  @Test(timeout = 4000)
  public void test11()  throws Throwable  {
      Properties properties0 = new Properties();
      StringReader stringReader0 = new StringReader("=");
      properties0.load((Reader) stringReader0);
      assertEquals(1, properties0.size());
  }

  @Test(timeout = 4000)
  public void test12()  throws Throwable  {
      Properties properties0 = new Properties();
      Enumeration<?> enumeration0 = properties0.propertyNames();
      assertNotNull(enumeration0);
  }

  @Test(timeout = 4000)
  public void test13()  throws Throwable  {
      Properties properties0 = new Properties();
      properties0.setProperty("vp-B& :jru1W{{oau", "vp-B& :jru1W{{oau");
      MockFileOutputStream mockFileOutputStream0 = new MockFileOutputStream("vp-B& :jru1W{{oau");
      properties0.save(mockFileOutputStream0, "vp-B& :jru1W{{oau");
      assertFalse(properties0.isEmpty());
  }
}
