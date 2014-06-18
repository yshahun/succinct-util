package ys.succinct.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ys.succinct.util.BitVectorTestUtils.generateInts;

import org.junit.Test;

/**
 * Tests for {@link CompactIntArray}.
 */
public class CompactIntArrayTest {

  private static final int SEED = 31;

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_withNegativeMaxValue() {
    new CompactIntArray(new int[] {0, 1, 2}, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_withGreaterElement() {
    new CompactIntArray(new int[] {10, 20, 30}, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_withNegativeElement() {
    new CompactIntArray(new int[] {10, 20, -1}, 1024);
  }

  @Test
  public void testGetInt_withRandomArray() {
    int n = 1000003;
    int max = 1024;
    int[] a = generateInts(n, max, SEED);
    CompactIntArray ints = new CompactIntArray(a, max);

    for (int i = 0; i < n; i++) {
      assertEquals(a[i], ints.getInt(i));
    }
  }

  @Test
  public void testGetInt_withZeroAsMaxValue() {
    int[] a = new int[] {0, 0};
    CompactIntArray ints = new CompactIntArray(a, 0);

    assertEquals(0, ints.getInt(0));
    assertEquals(0, ints.getInt(1));
  }

  @Test
  public void testGetInt_withOneAsMaxValue() {
    int[] a = new int[] {0, 1};
    CompactIntArray ints = new CompactIntArray(a, 1);

    assertEquals(0, ints.getInt(0));
    assertEquals(1, ints.getInt(1));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetInt_withNegativeIndex() {
    CompactIntArray ints = new CompactIntArray(new int[] {0, 1}, 1);
    ints.getInt(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetInt_withIndexOutOfBound() {
    CompactIntArray ints = new CompactIntArray(new int[] {0, 1}, 1);
    ints.getInt(2);
  }

  @Test
  public void testSize() {
    int[] a = generateInts(99, 1024, SEED);
    CompactIntArray ints = new CompactIntArray(a, 1024);

    assertEquals(a.length, ints.size());
  }

  @Test
  public void testGetRatio() {
    assertTrue(new CompactIntArray(new int[] {0, 0}, 0).getRatio() < 1d);
    assertTrue(new CompactIntArray(new int[] {0, 1}, 1).getRatio() < 1d);
    assertTrue(new CompactIntArray(generateInts(1000, 1024, SEED), 1024).getRatio() < 1d);
  }
}
