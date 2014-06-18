package ys.succinct.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ys.succinct.util.BitVectorTestUtils.toIntVector;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link IntBitSet}.
 */
public class IntBitSetTest {

  private static final int BIT_COUNT = 1000003;

  private boolean[] bits;

  @Before
  public void setUp() {
    bits = BitVectorTestUtils.generateBits(BIT_COUNT, 17);
  }

  @Test
  public void testGet() {
    IntBitSet bitSet = new IntBitSet(toIntVector(bits), BIT_COUNT);
    for (int i = 0; i < BIT_COUNT; i++) {
      assertEquals(bits[i], bitSet.get(i));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_withNegativeIndex() {
    IntBitSet bitSet = new IntBitSet(toIntVector(bits), BIT_COUNT);
    bitSet.get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_withIndexOutOfBound() {
    IntBitSet bitSet = new IntBitSet(toIntVector(bits), BIT_COUNT);
    bitSet.get(BIT_COUNT);
  }

  @Test
  public void testSet_dynamic() {
    IntBitSet bitSet = new IntBitSet();
    for (int i = 0; i < BIT_COUNT; i++) {
      bitSet.set(i, bits[i]);
      assertEquals(bits[i], bitSet.get(i));
    }
  }

  @Test
  public void testSet_fixed() {
    IntBitSet bitSet = new IntBitSet(BIT_COUNT);
    for (int i = 0; i < BIT_COUNT; i++) {
      bitSet.set(i, bits[i]);
      assertEquals(bits[i], bitSet.get(i));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testSet_withNegativeIndex() {
    IntBitSet bitSet = new IntBitSet();
    bitSet.set(-1, true);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testSet_fixed_withIndexOutOfBound() {
    IntBitSet bitSet = new IntBitSet(BIT_COUNT);
    bitSet.set(BIT_COUNT, true);
  }

  @Test
  public void testSize_dynamic() {
    IntBitSet bitSet = new IntBitSet();
    int oldSize = bitSet.size();
    bitSet.set(oldSize * 3, true);

    assertTrue(bitSet.size() > oldSize);
  }

  @Test
  public void testSize_fixed() {
    IntBitSet bitSet = new IntBitSet(8);
    assertEquals(8, bitSet.size());
  }

  @Test
  public void testSet_withDefaultValue() {
    IntBitSet bitSet = new IntBitSet();
    assertFalse(bitSet.get(0));
    bitSet.set(0);
    assertTrue(bitSet.get(0));
  }

  @Test
  public void testSetInt() {
    IntBitSet bitSet = new IntBitSet(96);
    bitSet.setInt(0, 100);
    bitSet.setInt(1, -100);

    assertArrayEquals(new int[] {100, -100, 0}, bitSet.intArray());
  }

  @Test
  public void testIntArray() {
    int[] a = new int[] {0, 1, 2};
    IntBitSet bitSet = new IntBitSet(a, 32 * 3);

    assertEquals(a, bitSet.intArray());
  }

  @Test
  public void testToIntArray_truncated() {
    IntBitSet bitSet = new IntBitSet(new int[] {0x0F, 0xFF, 0xFFFF}, 32 * 3);

    assertArrayEquals(new int[] {0x0F, 0xFF}, bitSet.toIntArray(32 * 2));
    assertArrayEquals(new int[] {0x0F, 0x0F}, bitSet.toIntArray(32 + 4));
  }

  @Test
  public void testToIntArray_padded() {
    IntBitSet bitSet = new IntBitSet(new int[] {0x0F, 0xFF}, 32 * 2);
    assertArrayEquals(new int[] {0x0F, 0xFF, 0, 0}, bitSet.toIntArray(32 * 4));
  }
}
