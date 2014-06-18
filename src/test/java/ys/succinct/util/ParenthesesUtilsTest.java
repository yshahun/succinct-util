package ys.succinct.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link ParenthesesUtils}.
 */
public class ParenthesesUtilsTest {

  @Test
  public void testGetForwardExcessIndex() {
    assertEquals(7, ParenthesesUtils.getForwardExcessIndex(0b00101011, 0, 1, 0));
    assertEquals(32, ParenthesesUtils.getForwardExcessIndex(0b00101011, 0, 1, 3));

    assertEquals(31, ParenthesesUtils.getForwardExcessIndex(0x30000000, 29, 5, 3));
    assertEquals(32, ParenthesesUtils.getForwardExcessIndex(0xA0000000, 29, 5, 6));
  }

  @Test
  public void testGetBackwardExcessIndex() {
    assertEquals(31, ParenthesesUtils.getBackwardExcessIndex(0xA0000000, 10, 10));
    assertEquals(30, ParenthesesUtils.getBackwardExcessIndex(0xA0000000, 10, 9));
    assertEquals(26, ParenthesesUtils.getBackwardExcessIndex(0xA0000000, 10, 11));
    assertEquals(-2, ParenthesesUtils.getBackwardExcessIndex(0xA0000000, 10, 0));

    assertEquals(0, ParenthesesUtils.getBackwardExcessIndex(0b0011, 1, 2, 1));
    assertEquals(-1, ParenthesesUtils.getBackwardExcessIndex(0b0011, 2, 1, 0));
    assertEquals(-2, ParenthesesUtils.getBackwardExcessIndex(0b0011, 0, 1, 2));
  }
}
