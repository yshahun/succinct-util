package ys.succinct.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link IntRank}.
 */
public class IntRankTest {

  private static final int BIT_COUNT = 1000003;

  private boolean[] bits;
  private IntRank rank;

  @Before
  public void setUp() {
    bits = BitVectorTestUtils.generateBits(BIT_COUNT, 17);
    rank = new IntRank(BitVectorTestUtils.toIntVector(bits), BIT_COUNT);
  }

  @Test
  public void testGet() {
    for (int i = 0; i < BIT_COUNT; i++) {
      assertEquals(bits[i], rank.get(i));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_withNegativeIndex() {
    rank.get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGet_withOutOfBoundIndex() {
    rank.get(BIT_COUNT);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSet() {
    rank.set(0, true);
  }

  @Test
  public void testSize() {
    assertEquals(BIT_COUNT, rank.size());
  }

  @Test
  public void testExcess() {
    int[] excesses = BitVectorTestUtils.getExcesses(bits);

    for (int i = 0; i < BIT_COUNT; i++) {
      assertEquals(String.format("excess(%d)", i), excesses[i], rank.excess(i));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testExcess_withNegativeIndex() {
    rank.excess(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testExcess_withIndexOutOfBound() {
    rank.excess(BIT_COUNT);
  }

  @Test
  public void testRank() {
    int[] ranks = BitVectorTestUtils.getRanks(bits);

    for (int i = 0; i < BIT_COUNT; i++) {
      assertEquals(String.format("rank(%d)", i), ranks[i], rank.rank(i));
    }
    assertEquals(ranks[BIT_COUNT - 1], rank.rank());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testRank_withNegativeIndex() {
    rank.rank(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testRank_withIndexOutOfBound() {
    rank.rank(BIT_COUNT);
  }

  @Test
  public void testRank0() {
    int[] ranks = BitVectorTestUtils.getZeroRanks(bits);

    for (int i = 0; i < BIT_COUNT; i++) {
      assertEquals(String.format("rank0(%d)", i), ranks[i], rank.rank0(i));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testRank0_withNegativeIndex() {
    rank.rank0(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testRank0_withIndexOutOfBound() {
    rank.rank0(BIT_COUNT);
  }
}
