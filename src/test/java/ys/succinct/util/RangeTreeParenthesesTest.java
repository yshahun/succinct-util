package ys.succinct.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link RangeTreeParentheses}.
 */
public class RangeTreeParenthesesTest {

  private static final int SEED = 17;

  @Test
  public void testFindClose() {
    int n = 1000003;
    IntBitSet bits = ParenthesesTestUtils.generateParentheses(n * 2, SEED);
    RangeTreeParentheses bp =
        new RangeTreeParentheses(bits.intArray(), bits.size());
    int[] m = ParenthesesTestUtils.getCloseParenthesesMapping(bits);

    for (int i = 0; i < n; i++) {
      assertEquals(String.format("findClose(%d)", m[i]), m[i + n], bp.findClose(m[i]));
    }
  }

  @Test
  public void testFindOpen() {
    int n = 1000003;
    IntBitSet bits = ParenthesesTestUtils.generateParentheses(n * 2, SEED);
    RangeTreeParentheses bp =
        new RangeTreeParentheses(bits.intArray(), bits.size());
    int[] m = ParenthesesTestUtils.getOpenParenthesesMapping(bits);

    for (int i = 0; i < n; i++) {
      assertEquals(String.format("findOpen(%d)", m[i]), m[i + n], bp.findOpen(m[i]));
    }
  }

  @Test
  public void testEnclose() {
    int n = 1000003;
    IntBitSet bits = ParenthesesTestUtils.generateParentheses(n * 2, SEED);
    RangeTreeParentheses bp =
        new RangeTreeParentheses(bits.intArray(), bits.size());
    int[] m = ParenthesesTestUtils.getEncloseParenthesesMapping(bits);

    for (int i = 0; i < n; i++) {
      assertEquals(String.format("enclose(%d)", i), m[i + n], bp.enclose(m[i]));
    }
  }
}
