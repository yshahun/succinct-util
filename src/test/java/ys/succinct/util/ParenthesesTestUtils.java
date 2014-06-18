package ys.succinct.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * Utilities for testing {@link BalancedParentheses}.
 *
 * @author Yauheni Shahun
 */
public final class ParenthesesTestUtils {

  private ParenthesesTestUtils() {}

  /**
   * Generates the balanced parentheses randomly.
   *
   * @param count the number of the parentheses
   * @param seed the random seed
   * @return {@link IntBitSet} as the binary representation of the parentheses
   */
  public static IntBitSet generateParentheses(int count, int seed) {
    if ((count % 2) != 0) {
      throw new IllegalArgumentException("Count must be divisible by 2.");
    }

    IntBitSet bits = new IntBitSet(count);
    Random random = (seed == -1) ? new Random() : new Random(seed);
    int openCount = count / 2;
    int i = 0;
    int bitIndex = 0;
    int excess = 0;

    while (i < openCount) {
      boolean isOpen = (excess == 0) ? true : random.nextBoolean();
      if (isOpen) {
        bits.set(bitIndex);
        i++;
        excess++;
      } else {
        excess--;
      }
      bitIndex++;
    }

    return bits;
  }

  /**
   * Gets the mapping of the open parentheses to their closed parentheses.
   *
   * @param bits the bit set of the size {@code 2n} as the parentheses representation
   * @return the array where the first {@code n} elements (0..n-1) are the indexes of the open
   *         parentheses, and the second {@code n} elements (n..2n-1) are the indexes of the
   *         corresponding closed parentheses
   */
  public static int[] getCloseParenthesesMapping(IntBitSet bits) {
    int n = bits.size() / 2;
    int[] r = new int[2 * n];
    Deque<Integer> deque = new ArrayDeque<>();

    int j = 0;
    for (int i = 0; i < bits.size(); i++) {
      if (bits.get(i)) {
        deque.push(j);
        r[j++] = i;
      } else {
        r[deque.pop() + n] = i;
      }
    }

    return r;
  }

  /**
   * Gets the mapping of the closed parentheses to their open parentheses.
   *
   * @param bits the bit set of the size {@code 2n} as the parentheses representation
   * @return the array where the first {@code n} elements (0..n-1) are the indexes of the closed
   *         parentheses, and the second {@code n} elements (n..2n-1) are the indexes of the
   *         corresponding open parentheses
   */
  public static int[] getOpenParenthesesMapping(IntBitSet bits) {
    int n = bits.size() / 2;
    int[] r = new int[2 * n];
    Deque<Integer> deque = new ArrayDeque<>();

    int j = 0;
    for (int i = 0; i < bits.size(); i++) {
      if (bits.get(i)) {
        deque.push(i);
      } else {
        r[j + n] = deque.pop();
        r[j++] = i;
      }
    }

    return r;
  }

  /**
   * Gets the mapping of the open parentheses to the open parentheses that immediately enclose them.
   *
   * @param bits the bit set of the size {@code 2n} as the parentheses representation
   * @return the array where the first {@code n} elements (0..n-1) are the indexes of the open
   *         parentheses, and the second {@code n} elements (n..2n-1) are the indexes of the
   *         corresponding enclosing parentheses or {@code -1} if there is no enclosing parenthesis
   */
  public static int[] getEncloseParenthesesMapping(IntBitSet bits) {
    int n = bits.size() / 2;
    int[] r = new int[2 * n];
    for (int i = n; i < r.length; i++) {
      r[i] = -1;
    }
    Deque<Integer> deque = new ArrayDeque<>();

    int j = 0;
    for (int i = 0; i < bits.size(); i++) {
      if (bits.get(i)) {
        if (!deque.isEmpty()) {
          r[j + n] = deque.peek();
        }
        deque.push(i);
        r[j++] = i;
      } else {
        deque.pop();
      }
    }

    return r;
  }
}
