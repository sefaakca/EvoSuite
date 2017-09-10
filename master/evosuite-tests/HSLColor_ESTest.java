/*
 * This file was automatically generated by EvoSuite
 * Sun Aug 27 20:06:04 GMT 2017
 */


import org.junit.Test;
import static org.junit.Assert.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class HSLColor_ESTest extends HSLColor_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.blend(190, 190, 190, 0.0F);
      assertEquals(0, hSLColor0.getHue());
  }

  @Test(timeout = 4000)
  public void test01()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initRGBbyHSL(190, 190, 190);
      hSLColor0.brighten(542.9F);
      assertEquals(190, hSLColor0.getHue());
  }

  @Test(timeout = 4000)
  public void test02()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.brighten(0.0F);
      assertEquals(0, hSLColor0.getRed());
  }

  @Test(timeout = 4000)
  public void test03()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.setLuminence(2142);
      assertEquals(255, hSLColor0.getRed());
      
      hSLColor0.brighten((-95));
      assertEquals(0, hSLColor0.getGreen());
  }

  @Test(timeout = 4000)
  public void test04()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.setLuminence((-95));
      assertEquals(0, hSLColor0.getBlue());
      assertEquals(0, hSLColor0.getHue());
      assertEquals(0, hSLColor0.getSaturation());
      assertEquals(0, hSLColor0.getGreen());
  }

  @Test(timeout = 4000)
  public void test05()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.setSaturation(1183);
      assertEquals(255, hSLColor0.getSaturation());
  }

  @Test(timeout = 4000)
  public void test06()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.setSaturation((-2135));
      assertEquals(0, hSLColor0.getSaturation());
      assertEquals(0, hSLColor0.getHue());
      assertEquals(0, hSLColor0.getBlue());
      assertEquals(0, hSLColor0.getGreen());
  }

  @Test(timeout = 4000)
  public void test07()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.setSaturation(190);
      assertEquals(190, hSLColor0.getSaturation());
  }

  @Test(timeout = 4000)
  public void test08()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initHSLbyRGB((-1), (-12), (-1));
      hSLColor0.setHue(0);
      assertEquals(0, hSLColor0.getRed());
  }

  @Test(timeout = 4000)
  public void test09()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initRGBbyHSL(3635, 3635, 3635);
      assertEquals(3635, hSLColor0.getLuminence());
  }

  @Test(timeout = 4000)
  public void test10()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initRGBbyHSL(190, 190, 190);
      hSLColor0.setHue((-2296));
      assertEquals(190, hSLColor0.getLuminence());
  }

  @Test(timeout = 4000)
  public void test11()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initHSLbyRGB((-1), (-12), (-1));
      hSLColor0.reverseColor();
      assertEquals(0, hSLColor0.getGreen());
  }

  @Test(timeout = 4000)
  public void test12()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.blend((-2296), 1470, 112, 0.9859475F);
      assertEquals(1449, hSLColor0.getGreen());
  }

  @Test(timeout = 4000)
  public void test13()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initHSLbyRGB(2886, 789, 386);
      assertEquals(386, hSLColor0.getBlue());
  }

  @Test(timeout = 4000)
  public void test14()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.blend(173, 173, 173, 173);
      assertEquals(173, hSLColor0.getBlue());
  }

  @Test(timeout = 4000)
  public void test15()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      hSLColor0.initHSLbyRGB((-1), (-3473), 85);
      assertEquals((-1), hSLColor0.getRed());
  }

  @Test(timeout = 4000)
  public void test16()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      int int0 = hSLColor0.getHue();
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test17()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      int int0 = hSLColor0.getGreen();
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test18()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      int int0 = hSLColor0.getRed();
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test19()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      int int0 = hSLColor0.getSaturation();
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test20()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      int int0 = hSLColor0.getLuminence();
      assertEquals(0, int0);
  }

  @Test(timeout = 4000)
  public void test21()  throws Throwable  {
      HSLColor hSLColor0 = new HSLColor();
      int int0 = hSLColor0.getBlue();
      assertEquals(0, int0);
  }
}
