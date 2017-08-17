/*
 * This file was automatically generated by EvoSuite
 * Thu Aug 17 11:26:43 GMT 2017
 */


import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import java.io.PrintWriter;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.mock.java.io.MockPrintWriter;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class CharArrayWriter_ESTest extends CharArrayWriter_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      CharArrayWriter charArrayWriter1 = charArrayWriter0.append((CharSequence) "", 0, 0);
      assertEquals(0, charArrayWriter1.size());
  }

  @Test(timeout = 4000)
  public void test01()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      charArrayWriter0.append((CharSequence) "UTF-8");
      assertEquals(5, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test02()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      charArrayWriter0.append((CharSequence) null);
      assertEquals(4, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test03()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      CharArrayWriter charArrayWriter1 = charArrayWriter0.append((CharSequence) null, 0, 0);
      assertEquals(0, charArrayWriter1.size());
  }

  @Test(timeout = 4000)
  public void test04()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      char[] charArray0 = new char[2];
      charArrayWriter0.write(charArray0);
      assertEquals(2, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test05()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      char[] charArray0 = new char[0];
      // Undeclared exception!
      try { 
        charArrayWriter0.write(charArray0, 0, 17);
        fail("Expecting exception: IndexOutOfBoundsException");
      
      } catch(IndexOutOfBoundsException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("CharArrayWriter", e);
      }
  }

  @Test(timeout = 4000)
  public void test06()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      char[] charArray0 = new char[3];
      // Undeclared exception!
      try { 
        charArrayWriter0.write(charArray0, 0, (-1190));
        fail("Expecting exception: IndexOutOfBoundsException");
      
      } catch(IndexOutOfBoundsException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("CharArrayWriter", e);
      }
  }

  @Test(timeout = 4000)
  public void test07()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      char[] charArray0 = new char[1];
      // Undeclared exception!
      try { 
        charArrayWriter0.write(charArray0, 2674, 0);
        fail("Expecting exception: IndexOutOfBoundsException");
      
      } catch(IndexOutOfBoundsException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("CharArrayWriter", e);
      }
  }

  @Test(timeout = 4000)
  public void test08()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      char[] charArray0 = new char[0];
      // Undeclared exception!
      try { 
        charArrayWriter0.write(charArray0, (-793), (-793));
        fail("Expecting exception: IndexOutOfBoundsException");
      
      } catch(IndexOutOfBoundsException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("CharArrayWriter", e);
      }
  }

  @Test(timeout = 4000)
  public void test09()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      MockPrintWriter mockPrintWriter0 = new MockPrintWriter(charArrayWriter0);
      PrintWriter printWriter0 = mockPrintWriter0.append('a');
      charArrayWriter0.writeTo(printWriter0);
      assertEquals(2, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test10()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = null;
      try {
        charArrayWriter0 = new CharArrayWriter((-3794));
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // Negative initial size: -3794
         //
         verifyException("CharArrayWriter", e);
      }
  }

  @Test(timeout = 4000)
  public void test11()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      MockPrintWriter mockPrintWriter0 = new MockPrintWriter(charArrayWriter0);
      charArrayWriter0.writeTo(mockPrintWriter0);
      assertEquals(0, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test12()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      charArrayWriter0.flush();
      assertEquals(0, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test13()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      charArrayWriter0.append('w');
      assertEquals(1, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test14()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      charArrayWriter0.close();
      assertEquals(0, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test15()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      String string0 = charArrayWriter0.toString();
      assertEquals("", string0);
  }

  @Test(timeout = 4000)
  public void test16()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      int int0 = charArrayWriter0.size();
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test17()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter(0);
      charArrayWriter0.reset();
      assertEquals(0, charArrayWriter0.size());
  }

  @Test(timeout = 4000)
  public void test18()  throws Throwable  {
      CharArrayWriter charArrayWriter0 = new CharArrayWriter();
      char[] charArray0 = charArrayWriter0.toCharArray();
      assertArrayEquals(new char[] {}, charArray0);
  }
}
