/*
 * Copyright 2014 Yauheni Shahun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ys.succinct.util;

/**
 * Compact representation of an array of the non-negative integers that requires smaller fixed
 * number of bits per an element.
 *
 * @author Yauheni Shahun
 */
public class CompactIntArray {

  private static final int BLOCK_SIZE = 32;

  /**
   * Compacted {@code int} array.
   */
  private final int[] vector;
  /**
   * Number of the elements in the array.
   */
  private final int size;
  /**
   * Fixed number of bits required to represent each {@code int} element.
   */
  private final int bitNum;

  /**
   * Constructs a compacted version of the given array.
   *
   * @param values the {@code int} array
   * @param maxValue the maximum value among all the elements in the array
   *
   * @throws IllegalArgumentException if the {@code maxValue} is negative, or any element is
   *         negative or greater than {@code maxValue}
   */
  public CompactIntArray(int[] values, int maxValue) {
    if (maxValue < 0) {
      throw new IllegalArgumentException("maxValue can't be a negative integer.");
    }

    this.bitNum = 32 - Integer.numberOfLeadingZeros(Math.max(1, maxValue));
    int newLength = (int) ((bitNum * (long) values.length + BLOCK_SIZE - 1) / BLOCK_SIZE);
    this.vector = new int[newLength];
    this.size = values.length;
    compact(values, maxValue);
  }

  /**
   * Compacts the given array into the internal array of the smaller size.
   */
  private void compact(int[] values, int maxValue) {
    int blockIndex = 0;
    int bitIndex = 0;

    for (int value : values) {
      if (value < 0 || value > maxValue) {
        throw new IllegalArgumentException("Bad element: " + value);
      }
      vector[blockIndex] |= value << bitIndex;
      int b = Math.min(bitNum, BLOCK_SIZE - bitIndex);
      bitIndex += b;

      if (bitIndex == BLOCK_SIZE) {
        blockIndex++;
        bitIndex = 0;

        if (b < bitNum) {
          vector[blockIndex] |= value >>> b;
          bitIndex = bitNum - b;
        }
      }
    }
  }

  /**
   * Returns an element of the array at the given index.
   *
   * @throws IndexOutOfBoundsException if the index is negative, or the index is equal to or greater
   *         than the array size
   */
  public int getInt(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

    int startIndex = index * bitNum;
    int blockIndex = startIndex / BLOCK_SIZE;
    int bitIndex = startIndex % BLOCK_SIZE;
    int x = vector[blockIndex];

    x >>>= bitIndex;
    int b = Math.min(bitNum, BLOCK_SIZE - bitIndex);
    x &= (1 << b) - 1;

    if (b < bitNum) {
      x |= vector[blockIndex + 1] << b;
      x &= (1 << bitNum) - 1;
    }

    return x;
  }

  /**
   * Returns the number of elements in the array.
   */
  public int size() {
    return size;
  }

  /**
   * Returns the ratio of the length of the compact array to the length of the initial array.
   */
  public double getRatio() {
    return (double) vector.length / size;
  }
}
