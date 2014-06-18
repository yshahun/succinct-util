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
 * {@link BalancedParentheses} implementation based on the range min-max tree (see K. Sadakane and
 * G. Navarro. Fully-Functional Succinct Trees). The parentheses are represented as an integer array
 * of bits where 1 bits correspond to the open parentheses and 0 bits correspond to the closed
 * parentheses.
 *
 * @author Yauheni Shahun
 */
public class RangeTreeParentheses implements BalancedParentheses {

  private static final int[] minExcessLookup = buildMinExcessLookup();
  private static final int[] maxExcessLookup = buildMaxExcessLookup();

  private static final int BLOCK_SIZE = 32;
  private static final int BLOCKS_PER_SUPERBLOCK_COUNT = 8;
  private static final int SUPER_BLOCK_SIZE = BLOCK_SIZE * BLOCKS_PER_SUPERBLOCK_COUNT;

  /**
   * The underlying bit array of the parentheses.
   */
  private final int[] vector;
  /**
   * The rank directory on the parentheses to answer the excess queries.
   */
  private final IntRank rank;
  /**
   * Minimum (local) excesses within the {@code int} blocks.
   */
  private final byte[] minExcesses;
  /**
   * Maximum (local) excesses within the {@code int} blocks.
   */
  private final byte[] maxExcesses;
  /**
   * Number of the superblocks.
   */
  private final int superBlockCount;
  /**
   * The range tree of the superblock minimum (global) excesses.
   */
  private final int[] minExcessTree;
  /**
   * The range tree of the superblock maximum (global) excesses.
   */
  private final int[] maxExcessTree;

  /**
   * Constructs the balanced parentheses' search structure. It's not verified that the underlying
   * parentheses are balanced. In case of the unbalanced parentheses the result of the search
   * operations is unpredictable.
   *
   * @param vector the array representation of the parentheses
   * @param size the number of the parentheses (bits)
   */
  public RangeTreeParentheses(int[] vector, int size) {
    this.vector = vector;
    this.rank = new IntRank(vector, size);
    this.minExcesses = new byte[vector.length];
    this.maxExcesses = new byte[vector.length];

    /*
     * Calculate the space required for the range trees. The range tree is a complete binary tree
     * similar to the heap. The superblock excess values correspond to the tree leaves, and upper
     * values are built on them. The virtual root node is added to support 1-based node indexing.
     */
    this.superBlockCount =
        (vector.length + BLOCKS_PER_SUPERBLOCK_COUNT - 1) / BLOCKS_PER_SUPERBLOCK_COUNT;
    int treeHeight = (int) Math.ceil(Math.log10(superBlockCount) / Math.log10(2));
    int treeNodeCount = ((int) Math.pow(2, treeHeight) - 1) /* tree intermediate nodes */
        + superBlockCount /* tree leaves */ + 1 /* tree root node */;

    this.minExcessTree = new int[treeNodeCount];
    this.maxExcessTree = new int[treeNodeCount];

    calculateBlockExcesses();
    buildMinMaxRangeTrees();
  }

  /**
   * Calculates the minimum and maximum local excesses within the {@code int} blocks.
   */
  private void calculateBlockExcesses() {
    for (int i = 0; i < vector.length; i++) {
      int block = vector[i];

      int byte1 = block & 0xFF;
      int minExcess = minExcessLookup[byte1];
      int maxExcess = maxExcessLookup[byte1];
      int rank = Integer.bitCount(byte1);
      int excess = rank * 2 - 8;

      int byte2 = (block >>> 8) & 0xFF;
      minExcess = Math.min(minExcess, excess + minExcessLookup[byte2]);
      maxExcess = Math.max(maxExcess, excess + maxExcessLookup[byte2]);
      rank += Integer.bitCount(byte2);
      excess = rank * 2 - 16;

      int byte3 = (block >>> 16) & 0xFF;
      minExcess = Math.min(minExcess, excess + minExcessLookup[byte3]);
      maxExcess = Math.max(maxExcess, excess + maxExcessLookup[byte3]);
      rank += Integer.bitCount(byte3);
      excess = rank * 2 - 24;

      int byte4 = block >>> 24;
      minExcess = Math.min(minExcess, excess + minExcessLookup[byte4]);
      maxExcess = Math.max(maxExcess, excess + maxExcessLookup[byte4]);

      minExcesses[i] = (byte) minExcess;
      maxExcesses[i] = (byte) maxExcess;
    }

    // Always consider "0" minimum excess in the beginning.
    minExcesses[0] = (byte) Math.min(0, minExcesses[0]);
  }

  /**
   * Builds the minimum and maximum range trees of the superblock excesses.
   */
  private void buildMinMaxRangeTrees() {
    int superBlockBaseIndex = minExcessTree.length - superBlockCount;

    // Calculate the global excesses within the superblocks, and place them as the tree leaves.
    for (int i = 0; i < superBlockCount; i++) {
      int startBlockIndex = i * BLOCKS_PER_SUPERBLOCK_COUNT;
      int endBlockIndex = Math.min(minExcesses.length, (i + 1) * BLOCKS_PER_SUPERBLOCK_COUNT);

      int minExcess = Integer.MAX_VALUE;
      int maxExcess = Integer.MIN_VALUE;

      for (int j = startBlockIndex; j < endBlockIndex; j++) {
        int excess = 0;
        if (j > 0) {
          // Excess up to the j-th block.
          excess = rank.excess(j * BLOCK_SIZE - 1);
        }
        minExcess = Math.min(minExcess, excess + minExcesses[j]);
        maxExcess = Math.max(maxExcess, excess + maxExcesses[j]);
      }

      int superBlockIndex = superBlockBaseIndex + i;
      minExcessTree[superBlockIndex] = minExcess;
      maxExcessTree[superBlockIndex] = maxExcess;
    }

    // Calculate the excesses for the upper tree nodes.
    for (int i = superBlockBaseIndex - 1; i > 0; i--) {
      int minExcess = Integer.MAX_VALUE;
      int maxExcess = Integer.MIN_VALUE;

      int childIndex = i * 2;
      if (childIndex < minExcessTree.length) { // left child
        minExcess = Math.min(minExcess, minExcessTree[childIndex]);
        maxExcess = Math.max(maxExcess, maxExcessTree[childIndex]);

        childIndex++;
        if (childIndex < minExcessTree.length) { // right child
          minExcess = Math.min(minExcess, minExcessTree[childIndex]);
          maxExcess = Math.max(maxExcess, maxExcessTree[childIndex]);
        }
      }

      minExcessTree[i] = minExcess;
      maxExcessTree[i] = maxExcess;
    }
  }

  @Override
  public int findClose(int openIndex) {
    return searchForward(openIndex, 0);
  }

  @Override
  public int findOpen(int closeIndex) {
    return searchBackward(closeIndex, 0);
  }

  @Override
  public int enclose(int index) {
    return searchBackward(index, 2);
  }

  /**
   * Searches the position with the immediate excess that differs from the excess at the given
   * position by the given delta value in the forward direction.
   *
   * @param index the 0-based position in the bit array that the search starts from
   * @param excessDelta the excess difference to find
   * @return the 0-based position with the search excess
   */
  private int searchForward(int index, int excessDelta) {
    int searchExcess = ((index == 0) ? 0 : rank.excess(index - 1)) + excessDelta;
    int blockIndex = index / BLOCK_SIZE;
    int bitIndex = index % BLOCK_SIZE;

    int closeIndex;
    if (bitIndex < 31) {
      // Search forward in the current block.
      closeIndex = ParenthesesUtils.getForwardExcessIndex(
          vector[blockIndex], bitIndex + 1, rank.excess(index + 1), searchExcess);
      if (closeIndex != 32) {
        return blockIndex * BLOCK_SIZE + closeIndex;
      }
    }

    int superBlockIndex = index / SUPER_BLOCK_SIZE;
    int endBlockIndex =
        Math.min(minExcesses.length, (superBlockIndex + 1) * BLOCKS_PER_SUPERBLOCK_COUNT);

    // Search forward in the current superblock.
    closeIndex = searchForwardInSuperBlock(blockIndex + 1, endBlockIndex, searchExcess);
    if (closeIndex != -1) {
      return closeIndex;
    }

    // Ascend the tree in the forward direction up to the range that contains the search excess.
    int treeIndex = minExcessTree.length - superBlockCount + superBlockIndex;
    boolean isRight;
    do {
      isRight = (treeIndex % 2) == 1;
      treeIndex = isRight ? (treeIndex / 2) : (treeIndex + 1);
    } while (isRight || searchExcess < minExcessTree[treeIndex]
        || searchExcess > maxExcessTree[treeIndex]);


    // Descend the tree to the particular superblock that contains the search excess.
    int childIndex = treeIndex * 2;
    while (childIndex < minExcessTree.length) {
      if (searchExcess >= minExcessTree[childIndex]
          && searchExcess <= maxExcessTree[childIndex]) {
        treeIndex = childIndex;
      } else {
        treeIndex = childIndex + 1;
      }
      childIndex = treeIndex * 2;
    }

    superBlockIndex = superBlockCount - minExcessTree.length + treeIndex;
    endBlockIndex =
        Math.min(minExcesses.length, (superBlockIndex + 1) * BLOCKS_PER_SUPERBLOCK_COUNT);

    // Search forward in the particular superblock.
    return searchForwardInSuperBlock(
        superBlockIndex * BLOCKS_PER_SUPERBLOCK_COUNT, endBlockIndex, searchExcess);
  }

  /**
   * Searches the position with the given excess within the superblock in the forward direction.
   *
   * @param beginBlockIndex index of the block within the superblock that the search starts from
   * @param endBlockIndex index of the block that the superblock is adjacent to (exclusive)
   * @param searchExcess the excess to find
   * @return 0-based position with the search excess, or {@code -1} if no such excess is found
   *         within the superblock
   */
  private int searchForwardInSuperBlock(int beginBlockIndex, int endBlockIndex, int searchExcess) {
    for (int i = beginBlockIndex; i < endBlockIndex; i++) {
      int excess = rank.excess(i * BLOCK_SIZE - 1);
      int minExcess = excess + minExcesses[i];
      int maxExcess = excess + maxExcesses[i];

      if (searchExcess >= minExcess && searchExcess <= maxExcess) {
        int closeIndex = ParenthesesUtils.getForwardExcessIndex(
            vector[i], rank.excess(i * BLOCK_SIZE), searchExcess);
        return i * BLOCK_SIZE + closeIndex;
      }
    }
    return -1;
  }

  /**
   * Searches the position with the immediate excess that differs from the excess at the given
   * position by the given delta value in the backward direction.
   *
   * @param index the 0-based position in the bit array that the search starts from
   * @param excessDelta the excess difference to find
   * @return the 0-based position with the search excess
   */
  private int searchBackward(int index, int excessDelta) {
    int searchExcess = rank.excess(index) - excessDelta;
    if (searchExcess < 0) {
      return -1;
    }

    int blockIndex = index / BLOCK_SIZE;
    int bitIndex = index % BLOCK_SIZE;

    int openIndex;
    if (bitIndex > 0) {
      // Search backward in the current block.
      openIndex = ParenthesesUtils.getBackwardExcessIndex(
          vector[blockIndex], bitIndex - 1, rank.excess(index - 1), searchExcess);
      if (openIndex > -2) {
        return blockIndex * BLOCK_SIZE + openIndex + 1;
      }
    }

    int superBlockIndex = index / SUPER_BLOCK_SIZE;

    // Search backward in the current superblock.
    openIndex = searchBackwardInSuperBlock(
        blockIndex - 1, superBlockIndex * BLOCKS_PER_SUPERBLOCK_COUNT, searchExcess);
    if (openIndex != -1) {
      return openIndex;
    }

    // Ascend the tree in the backward direction up to the range that contains the search excess.
    int treeIndex = minExcessTree.length - superBlockCount + superBlockIndex;
    boolean isLeft;
    do {
      isLeft = (treeIndex % 2) == 0;
      treeIndex = isLeft ? (treeIndex / 2) : (treeIndex - 1);
    } while (isLeft || searchExcess < minExcessTree[treeIndex]
        || searchExcess > maxExcessTree[treeIndex]);

    // Descend the tree to the particular superblock that contains the search excess.
    int childIndex = treeIndex * 2;
    while (childIndex < minExcessTree.length) { // Test that any child exists.
      childIndex++; // Examine the right child first i.e. backward.
      if (childIndex < minExcessTree.length
          && searchExcess >= minExcessTree[childIndex]
          && searchExcess <= maxExcessTree[childIndex]) {
        treeIndex = childIndex;
      } else {
        treeIndex = childIndex - 1;
      }
      childIndex = treeIndex * 2;
    }

    superBlockIndex = superBlockCount - minExcessTree.length + treeIndex;
    int beginBlockIndex = (superBlockIndex + 1) * BLOCKS_PER_SUPERBLOCK_COUNT - 1;

    // Search backward in the particular superblock.
    return searchBackwardInSuperBlock(
        beginBlockIndex, superBlockIndex * BLOCKS_PER_SUPERBLOCK_COUNT, searchExcess);
  }

  /**
   * Searches the position with the given excess within the superblock in the backward direction.
   *
   * @param beginBlockIndex index of the block within the superblock that the search starts from
   * @param endBlockIndex index of the block that the superblock starts from (inclusive)
   * @param searchExcess the excess to find
   * @return 0-based position with the search excess, or {@code -1} if no such excess is found
   *         within the superblock
   */
  private int searchBackwardInSuperBlock(int beginBlockIndex, int endBlockIndex, int searchExcess) {
    for (int i = beginBlockIndex; i >= endBlockIndex; i--) {
      int excess = (i == 0) ? 0 : rank.excess(i * BLOCK_SIZE - 1);
      int minExcess = excess + minExcesses[i];
      int maxExcess = excess + maxExcesses[i];

      if (searchExcess >= minExcess && searchExcess <= maxExcess) {
        int openIndex = ParenthesesUtils.getBackwardExcessIndex(
            vector[i], rank.excess((i + 1) * BLOCK_SIZE - 1), searchExcess);
        return i * BLOCK_SIZE + openIndex + 1;
      }
    }
    return -1;
  }

  /*
   * Helper methods.
   */

  private static int[] buildMinExcessLookup() {
    int[] lookup = new int[256];
    for (int i = 0; i < lookup.length; i++) {
      lookup[i] = getMinByteExcess(i);
    }
    return lookup;
  }

  private static int[] buildMaxExcessLookup() {
    int[] lookup = new int[256];
    for (int i = 0; i < lookup.length; i++) {
      lookup[i] = getMaxByteExcess(i);
    }
    return lookup;
  }

  private static int getMinByteExcess(int byteValue) {
    int excess = 0;
    int minExcess = Integer.MAX_VALUE;
    for (int i = 0; i < 8; i++) {
      int mask = byteValue & (1 << i);
      excess += (mask != 0) ? 1 : -1;
      minExcess = Math.min(minExcess, excess);
    }
    return minExcess;
  }

  private static int getMaxByteExcess(int byteValue) {
    int excess = 0;
    int maxExcess = Integer.MIN_VALUE;
    for (int i = 0; i < 8; i++) {
      int mask = byteValue & (1 << i);
      excess += (mask != 0) ? 1 : -1;
      maxExcess = Math.max(maxExcess, excess);
    }
    return maxExcess;
  }
}
