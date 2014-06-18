package ys.succinct.util;

import java.util.Random;

/**
 * Utilities for testing bit vectors.
 *
 * @author Yauheni Shahun
 */
public class BitVectorTestUtils {

  private static final int INT_SIZE = 32;

  /**
   * Generates a random bit set represented as the boolean array in which {@code true} elements
   * correspond to 1s and {@code false} elements correspond to 0s.
   */
  public static boolean[] generateBits(int n, int seed) {
    boolean[] bits = new boolean[n];
    Random random = (seed == -1) ? new Random() : new Random(seed);
    for (int i = 0; i < n; i++) {
      bits[i] = random.nextBoolean();
    }
    return bits;
  }

  /**
   * Generates a random {@code int} array with elements whose values are no greater than the given
   * {@code maxValue}.
   */
  public static int[] generateInts(int count, int maxValue, int seed) {
    int[] a = new int[count];
    Random random = (seed == -1) ? new Random() : new Random(seed);
    for (int i = 0; i < count; i++) {
      a[i] = random.nextInt(maxValue);
    }
    return a;
  }

  /**
   * Returns excesses for the bit set represented as the boolean array.
   */
  public static int[] getExcesses(boolean[] bits) {
    int[] excesses = new int[bits.length];
    int e = 0;
    for (int i = 0; i < bits.length; i++) {
      e += bits[i] ? 1 : -1;
      excesses[i] = e;
    }
    return excesses;
  }

  /**
   * Returns ranks of 1s for the bit set represented as the boolean array.
   */
  public static int[] getRanks(boolean[] bits) {
    int[] ranks = new int[bits.length];
    int rank = 0;
    for (int i = 0; i < bits.length; i++) {
      if (bits[i]) {
        rank++;
      }
      ranks[i] = rank;
    }
    return ranks;
  }

  /**
   * Returns ranks of 0s for the bit set represented as the boolean array.
   */
  public static int[] getZeroRanks(boolean[] bits) {
    int[] ranks = new int[bits.length];
    int rank = 0;
    for (int i = 0; i < bits.length; i++) {
      if (!bits[i]) {
        rank++;
      }
      ranks[i] = rank;
    }
    return ranks;
  }

  /**
   * Returns select answers for the bit set represented as the boolean array.
   */
  public static int[] getSelects(boolean[] bits, int count) {
    int[] selects = new int[count];
    for (int i = 0, j = 0; i < bits.length; i++) {
      if (bits[i]) {
        selects[j++] = i;
      }
    }
    return selects;
  }

  /**
   * Converts the given bit set represented as the boolean array to the {@code int} array.
   */
  public static int[] toIntVector(boolean[] bits) {
    int n = (bits.length + INT_SIZE - 1) / INT_SIZE;
    int[] vector = new int[n];
    for (int i = 0; i < n; i++) {
      vector[i] = toInt(bits, i * INT_SIZE);
    }
    return vector;
  }

  private static int toInt(boolean[] bits, int lo) {
    int acc = 0;
    int n = Math.min(bits.length - lo, INT_SIZE);
    for (int i = 0; i < n; i++) {
      if (bits[lo + i]) {
        acc |= 1 << i;
      }
    }
    return acc;
  }
}
