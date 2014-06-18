package ys.succinct.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link SamplingBitVector}.
 */
public class SamplingBitVectorTest {

  @Test
  public void testSelect_random() {
    int n = 1000003;
    boolean[] bits = BitVectorTestUtils.generateBits(n, 31);
    SamplingBitVector vector = new SamplingBitVector(BitVectorTestUtils.toIntVector(bits), n);
    int bitCount = vector.rank();
    int[] selects = BitVectorTestUtils.getSelects(bits, bitCount);

    for (int i = 0; i < bitCount; i++) {
      assertEquals(String.format("select(%d)", i), selects[i], vector.select(i));
    }
  }

  @Test
  public void testSelect_withIndexOutOfRank() {
    SamplingBitVector vector = new SamplingBitVector(new int[] {0xA5A5A5}, 24);
    int totalRank = vector.rank();

    for (int i = totalRank; i < vector.size; i++) {
      assertEquals(-1, vector.select(i));
    }
  }
  @Test(expected = IndexOutOfBoundsException.class)
  public void testSelect_withNegativeIndex() {
    SamplingBitVector vector = new SamplingBitVector(new int[] {0xA5A5A5}, 24);
    vector.select(-1);
  }


  @Test(expected = IndexOutOfBoundsException.class)
  public void testSelect_withIndexOutOfBound() {
    SamplingBitVector vector = new SamplingBitVector(new int[] {0xA5A5A5}, 24);
    vector.select(vector.size());
  }
}
