/*
 * This file was automatically generated by EvoSuite
 * Sun Aug 27 19:44:44 GMT 2017
 */


import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedReader;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class ImprovedTokenizer_ESTest extends ImprovedTokenizer_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("", "");
      improvedTokenizer0.myState = 2;
      String string0 = improvedTokenizer0.next();
      assertNull(string0);
  }

  @Test(timeout = 4000)
  public void test01()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("", "");
      improvedTokenizer0.hasNext();
      // Undeclared exception!
      try { 
        improvedTokenizer0.next();
        fail("Expecting exception: IllegalStateException");
      
      } catch(IllegalStateException e) {
         //
         // invalid state: 1
         //
         verifyException("ImprovedTokenizer", e);
      }
  }

  @Test(timeout = 4000)
  public void test02()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("R @G)_jCY", "(sX\"v)");
      // Undeclared exception!
      try { 
        improvedTokenizer0.afterToken('^');
        fail("Expecting exception: NullPointerException");
      
      } catch(NullPointerException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("ImprovedTokenizer", e);
      }
  }

  @Test(timeout = 4000)
  public void test03()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer(")#;AA", ")#;AA");
      // Undeclared exception!
      try { 
        improvedTokenizer0.beforeToken('\"');
        fail("Expecting exception: NullPointerException");
      
      } catch(NullPointerException e) {
         //
         // no message in exception (getMessage() returned null)
         //
         verifyException("ImprovedTokenizer", e);
      }
  }

  @Test(timeout = 4000)
  public void test04()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("CC7Nm3l", "");
      improvedTokenizer0.hasNext();
      // Undeclared exception!
      try { 
        improvedTokenizer0.stop();
        fail("Expecting exception: RuntimeException");
      
      } catch(RuntimeException e) {
         //
         // Invalid state, 5
         //
         verifyException("ImprovedTokenizer", e);
      }
  }

  @Test(timeout = 4000)
  public void test05()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("R @G)_jCY", "(sX\"v)");
      String string0 = improvedTokenizer0.next();
      assertEquals("R @G", string0);
      assertNotNull(string0);
      
      int int0 = improvedTokenizer0.stop();
      assertEquals(5, int0);
  }

  @Test(timeout = 4000)
  public void test06()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("", "");
      improvedTokenizer0.hasNext();
      boolean boolean0 = improvedTokenizer0.advance();
      assertFalse(boolean0);
  }

  @Test(timeout = 4000)
  public void test07()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("V~n(9(}Q", "8XSl>;O?vD");
      String string0 = improvedTokenizer0.next();
      assertNotNull(string0);
      
      String string1 = improvedTokenizer0.next();
      assertEquals("V~n(9(}Q", string1);
  }

  @Test(timeout = 4000)
  public void test08()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("", "");
      boolean boolean0 = improvedTokenizer0.keepParsing(0);
      assertTrue(boolean0);
  }

  @Test(timeout = 4000)
  public void test09()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("}boC[UkVuyNd", "}boC[UkVuyNd");
      String string0 = improvedTokenizer0.next();
      assertNull(string0);
  }

  @Test(timeout = 4000)
  public void test10()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("R @G)_jCY", "(sX\"v)");
      String string0 = improvedTokenizer0.next();
      assertNotNull(string0);
      assertEquals("R @G", string0);
      
      boolean boolean0 = improvedTokenizer0.hasNext();
      assertTrue(boolean0);
  }

  @Test(timeout = 4000)
  public void test11()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("", "");
      improvedTokenizer0.myState = (-43);
      boolean boolean0 = improvedTokenizer0.hasNext();
      assertFalse(boolean0);
  }

  @Test(timeout = 4000)
  public void test12()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("", "");
      boolean boolean0 = improvedTokenizer0.hasNext();
      boolean boolean1 = improvedTokenizer0.hasNext();
      assertTrue(boolean1 == boolean0);
      assertFalse(boolean1);
  }

  @Test(timeout = 4000)
  public void test13()  throws Throwable  {
      PipedReader pipedReader0 = new PipedReader();
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer(pipedReader0, "");
      assertEquals(0, ImprovedTokenizer.STATE_START);
  }

  @Test(timeout = 4000)
  public void test14()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("V~n(9(}Q", "8XSl>;O?vD");
      String string0 = improvedTokenizer0.previousDelimiter();
      assertNull(string0);
  }

  @Test(timeout = 4000)
  public void test15()  throws Throwable  {
      ImprovedTokenizer improvedTokenizer0 = new ImprovedTokenizer("6I)Z", "Y?>p:D9a");
      PipedInputStream pipedInputStream0 = new PipedInputStream();
      improvedTokenizer0.initialize((InputStream) pipedInputStream0, "6I)Z");
      assertEquals(2, ImprovedTokenizer.STATE_BEFORE_TOKEN);
  }
}
