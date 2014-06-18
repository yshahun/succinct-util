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
 * A plain bit set that is able to answer {@link Rank} queries.
 * <p>
 * The bit set is backed by an integer array where each {@code int} value represents 32 consecutive
 * bits packed together. Though the bit set is read-only, the provided integer array is used as is.
 * Be sure you will not modify the array beyond this structure. The bit set can handle up to
 * {@link Integer#MAX_VALUE} bits.
 * <p>
 * The rank implementation follows Jacobson's approach (G. Jacobson. Succinct static data
 * structures).
 *
 * @author Yauheni Shahun
 */
public class IntRank extends AbstractRank implements BitSet {

  /**
   * Number of bits in the small (int) block.
   */
  protected static final int BLOCK_SIZE = 32;
  /**
   * Number of the small blocks in the large block.
   */
  protected static final int SMALL_BLOCK_COUNT = 8;
  /**
   * Number of bits in the large block (256).
   */
  protected static final int LARGE_BLOCK_BIT_COUNT = SMALL_BLOCK_COUNT * BLOCK_SIZE;

  /**
   * Bit array.
   */
  protected final int[] vector;
  /**
   * Number of bits that the set holds.
   */
  protected final int size;
  /**
   * Rank directory that stores absolute cumulative ranks of the large blocks of 256 bits. The
   * length of the directory is the number of large blocks plus one. That last rank value is
   * reserved to hold the total rank of the bit set.
   */
  protected final int[] largeRankDirectory;
  /**
   * Rank directory that stores ranks of the small blocks of 32 bits. The ranks are calculated
   * cumulatively within the large block that the small blocks belong to. The small blocks
   * correspond to the {@code int} elements of the bit array.
   * <p>
   * The maximum expected rank within the large block is 32*7=224 what fits the {@code byte} data
   * type with use of the upcast technique.
   */
  protected final byte[] smallRankDirectory;

  /**
   * Constructs a bit set that answers the rank queries.
   *
   * @param vector the array representation of the bit set
   * @param size the number of bits in the set
   * @throws IllegalArgumentException if the size is less than or equal to 0, or the size is greater
   *         than the number of bits in the given array
   */
  public IntRank(int[] vector, int size) {
    if (size <= 0 || size > vector.length * BLOCK_SIZE) {
      throw new IllegalArgumentException("Invalid vector or size.");
    }

    this.vector = vector;
    this.size = size;
    int largeBlockCount =
        (int) (((long) vector.length + SMALL_BLOCK_COUNT - 1) / SMALL_BLOCK_COUNT) + 1;
    this.largeRankDirectory = new int[largeBlockCount];
    this.smallRankDirectory = new byte[vector.length];

    buildRankDirectories();
  }

  private void buildRankDirectories() {
    // Iterate over large blocks.
    for (int i = 0; i < vector.length; i = i + SMALL_BLOCK_COUNT) {
      int r = 0;
      int c = Math.min(i + SMALL_BLOCK_COUNT, vector.length);
      // Iterate over small blocks within the large block.
      for (int j = i; j < c; j++) {
        smallRankDirectory[j] = (byte) r;
        r += Integer.bitCount(vector[j]);
      }
      int k = i / SMALL_BLOCK_COUNT;
      largeRankDirectory[k + 1] = largeRankDirectory[k] + r;
    }
  }

  /*
   * BitSet implementation.
   */

  @Override
  public boolean get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

    int blockIndex = index / BLOCK_SIZE;
    int mask = vector[blockIndex] & (1 << (index % BLOCK_SIZE));
    return mask != 0;
  }

  /**
   * @throws UnsupportedOperationException as the bit set is read-only
   */
  @Override
  public void set(int index, boolean value) {
    throw new UnsupportedOperationException("Read-only");
  }

  @Override
  public int size() {
    return size;
  }

  /*
   * Rank implementation.
   */

  @Override
  public int rank(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

     int rank = largeRankDirectory[index / LARGE_BLOCK_BIT_COUNT]; // index >> 8

    int blockIndex = index / BLOCK_SIZE; // index >> 5
    rank += smallRankDirectory[blockIndex] & 0xFF; // Upcast byte to int.

    int remainder = index % BLOCK_SIZE; // index & 31
    int remainingBits = (-1 >>> (31 - remainder)) & vector[blockIndex];
    rank += Integer.bitCount(remainingBits);

    return rank;
  }

  @Override
  public int rank() {
    return largeRankDirectory[largeRankDirectory.length - 1];
  }
}
