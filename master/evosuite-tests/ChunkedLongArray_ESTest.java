/*
 * This file was automatically generated by EvoSuite
 * Mon Jul 17 12:58:12 GMT 2017
 */


import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class ChunkedLongArray_ESTest extends ChunkedLongArray_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray(0);
      // Undeclared exception!
      try { 
        chunkedLongArray0.set((long[]) null, 0, (-376), 689);
        fail("Expecting exception: ArrayIndexOutOfBoundsException");
      
      } catch(ArrayIndexOutOfBoundsException e) {
         //
         // no message in exception (getMessage() returned null)
         //
      }
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray(0);
      long[] longArray0 = new long[2];
      chunkedLongArray0.set(longArray0, (-2904), 2173, (-1427));
      chunkedLongArray0.set(chunkedLongArray0, 1, 0, 20);
      assertEquals(20, chunkedLongArray0.size());
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray((-1));
      chunkedLongArray0.set(chunkedLongArray0, (-1), (-1875), (-1875));
      assertEquals(0, chunkedLongArray0.size());
  }

  @Test(timeout = 4000)
  public void test3()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray();
      // Undeclared exception!
      try { 
        chunkedLongArray0.get(0);
        fail("Expecting exception: ArrayIndexOutOfBoundsException");
      
      } catch(ArrayIndexOutOfBoundsException e) {
         //
         // Index 0 requested with array length 0
         //
         verifyException("ChunkedLongArray", e);
      }
  }

  @Test(timeout = 4000)
  public void test4()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray(0);
      String string0 = chunkedLongArray0.toString();
      assertEquals("ChunkedLongArray(0 entries, 0 chunks, 0MB)", string0);
  }

  @Test(timeout = 4000)
  public void test5()  throws Throwable  {
      String string0 = ChunkedLongArray.memStats();
      //  // Unstable assertion: assertEquals("Allocated memory: 0MB, Allocated unused memory: 0MB, Heap memory used: 147MB, Max memory: 0MB", string0);
  }

  @Test(timeout = 4000)
  public void test6()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray(0);
      chunkedLongArray0.add(0);
      assertEquals(1, chunkedLongArray0.capacity());
  }

  @Test(timeout = 4000)
  public void test7()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray();
      int int0 = chunkedLongArray0.capacity();
      assertEquals(0, int0);
      assertEquals(0, chunkedLongArray0.size());
  }

  @Test(timeout = 4000)
  public void test8()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray(0);
      chunkedLongArray0.set(20, 1L);
      chunkedLongArray0.sort();
      assertEquals(21, chunkedLongArray0.capacity());
  }

  @Test(timeout = 4000)
  public void test9()  throws Throwable  {
      ChunkedLongArray chunkedLongArray0 = new ChunkedLongArray(0);
      int int0 = chunkedLongArray0.size();
      assertEquals(0, int0);
  }
}