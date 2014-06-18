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

import java.util.Arrays;

/**
 * A mutable bit set that either can have the fixed size or can grow as needed.
 * <p>
 * Internally, the bit set is backed by an integer array. During resizing the array grows by using
 * the doubling technique. Therefore, typical scenario is to call {@link #toIntArray(int)} later to
 * truncate the underlying array to some known optimal size. Attempt to grow the fixed bit set
 * raises a runtime exception.
 *
 * @author Yauheni Shahun
 */
public class IntBitSet implements BitSet {

  private static final int BLOCK_SIZE = 32;
  private static final int INITIAL_BLOCK_COUNT = 8;
  /**
   * Maximum count of the full {@code int} blocks of 32 bits.
   */
  private static final int MAX_BLOCK_COUNT = Integer.MAX_VALUE / BLOCK_SIZE;

  /**
   * Bits packed by the integer blocks.
   */
  private int[] blocks;
  /**
   * Number of bits that the set holds.
   */
  private int size;
  /**
   * Specifies whether the bit set can be resized.
   */
  private final boolean isDynamic;

  /**
   * Constructs an empty bit set that can be dynamically resized.
   */
  public IntBitSet() {
    this.blocks = new int[INITIAL_BLOCK_COUNT];
    this.size = INITIAL_BLOCK_COUNT * BLOCK_SIZE;
    this.isDynamic = true;
  }

  /**
   * Constructs an empty bit set of the fixed size.
   *
   * @param size the number of bits that the set can handle
   * @throws IllegalArgumentException if the size is less than or equal to 0
   */
  public IntBitSet(int size) {
    if (size <= 0) {
      throw new IllegalArgumentException("Invalid size: " + size);
    }

    this.blocks = new int[divAndCeil(size, BLOCK_SIZE)];
    this.size = size;
    this.isDynamic = false;
  }

  /**
   * Constructs a bit set of the fixed size.
   *
   * @param vector the existing array representation of the bit set
   * @param size the number of bits in the set
   * @throws IllegalArgumentException if the size is less than or equal to 0, or the size is greater
   *         than the number of bits in the given array
   */
  public IntBitSet(int[] vector, int size) {
    if (size <= 0 || size > vector.length * BLOCK_SIZE) {
      throw new IllegalArgumentException("Invalid vector or size.");
    }

    this.blocks = vector;
    this.size = size;
    this.isDynamic = false;
  }

  /*
   * BitSet API.
   */

  @Override
  public boolean get(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

    int blockIndex = index / BLOCK_SIZE;
    int mask = blocks[blockIndex] & (1 << (index % BLOCK_SIZE));
    return mask != 0;
  }

  /**
   * Sets the bit value at the given position. If the bit index is out of bound of the current size,
   * two scenarios are possible:
   * <ul>
   * <li>The internal array is resized first if the set is dynamic</li>
   * <li>Runtime exception is raised if the set has the fixed size.</li>
   * </ul>
   *
   * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
   *         the size of the fixed bit set
   */
  @Override
  public void set(int index, boolean value) {
    if (index < 0 || index == Integer.MAX_VALUE) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

    ensureCapacity(index);

    int blockIndex = index / BLOCK_SIZE;
    if (value) {
      blocks[blockIndex] |= 1 << (index % BLOCK_SIZE); // Set 1 bit.
    } else {
      blocks[blockIndex] &= ~(1 << (index % BLOCK_SIZE)); // Set 0 bit.
    }
  }

  /**
   * Returns the number of bits in the bit set. In case of the dynamic set the size is the multiple
   * of the internal array's length and depends on the pattern by what the array grows.
   */
  @Override
  public int size() {
    return size;
  }

  /*
   * Custom API.
   */

  /**
   * Sets the bit at the given position to 1.
   */
  public void set(int index) {
    set(index, true);
  }

  /**
   * Sets the bits as the {@code int} block at the given position.
   * <p>
   * With this method the bit set can be used as a dynamic array of {@code int}s.
   *
   * @param blockIndex the index in the internal array
   * @param value the {@code int} value as the set of 32 bits.
   */
  public void setInt(int blockIndex, int value) {
    if (blockIndex < 0 || blockIndex >= MAX_BLOCK_COUNT) {
      throw new IndexOutOfBoundsException("Invalid blockIndex: " + blockIndex);
    }
    ensureCapacity((blockIndex + 1) * BLOCK_SIZE - 1);
    blocks[blockIndex] = value;
  }

  /**
   * Checks whether the internal array has enough capacity for the bits. Expands it if it's required
   * and allowed. Raises {@link IndexOutOfBoundsException} if the capacity is not enough and the set
   * is fixed.
   */
  private void ensureCapacity(int index) {
    if (index >= size) {
      if (!isDynamic) {
        throw new IndexOutOfBoundsException("Invalid index: " + index);
      }

      int blockCount = Math.max(blocks.length * 2, divAndCeil(index + 1, BLOCK_SIZE));
      blocks = Arrays.copyOf(blocks, blockCount);
      size = (int) Math.min((long) blockCount * BLOCK_SIZE, Integer.MAX_VALUE);
    }
  }

  /**
   * Returns the internal bit array as is.
   */
  public int[] intArray() {
    return blocks;
  }

  /**
   * Returns a copy of the bit array truncated or expanded (padded with 0s) to the given size.
   *
   * @param newSize the number of bits in the new array
   */
  public int[] toIntArray(int newSize) {
    int newBlockCount = divAndCeil(newSize, BLOCK_SIZE);
    int[] copy = Arrays.copyOf(blocks, newBlockCount);
    int remainingBits = newSize % BLOCK_SIZE;
    if (remainingBits > 0) {
      // Clear out the bits in the tail block.
      copy[newBlockCount - 1] = copy[newBlockCount - 1] & ((1 << remainingBits) - 1);
    }
    return copy;
  }

  private static int divAndCeil(int x, int y) {
    return (int) (((long) x + y - 1) / y);
  }
}
