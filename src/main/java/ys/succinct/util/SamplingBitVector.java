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
 * A plain bit vector that provides the rank/select operations. The rank queries are supported by
 * {@link IntRank}. The select operation is implemented by using the sampling technique (single
 * directory).
 *
 * @author Yauheni Shahun
 */
public class SamplingBitVector extends IntRank implements BitVector {

  /**
   * The step of sampling.
   */
  private static final int SELECT_SAMPLE_RANGE = 256;

  /**
   * Array of the select answers for each {@code k}-th bit where {@code k} is defined by
   * {@link #SELECT_SAMPLE_RANGE} constant.
   */
  private final int[] selectSamples;

  /**
   * Constructs a bit vector from the given bit set.
   *
   * @param vector the array representation of the bit set
   * @param size the number of bits in the set
   */
  public SamplingBitVector(int[] vector, int size) {
    super(vector, size);
    selectSamples = new int[rank() / SELECT_SAMPLE_RANGE + 1];
    sampleSelect();
  }

  /**
   * Does sampling to support the select operation.
   */
  private void sampleSelect() {
    int sampleIndex = 1;
    int sampleRank = SELECT_SAMPLE_RANGE;

    for (int i = 0; i < largeRankDirectory.length - 1; i++) {
      int largeRank = largeRankDirectory[i];
      int nextBlockIndex = Math.min(smallRankDirectory.length, (i + 1) * SMALL_BLOCK_COUNT);
      for (int j = i * SMALL_BLOCK_COUNT; j < nextBlockIndex; j++) {
        int rank = largeRank + (smallRankDirectory[j] & 0xFF);
        while (sampleRank <= rank) {
          selectSamples[sampleIndex++] = j - 1; // Save the index of the small block.
          sampleRank += SELECT_SAMPLE_RANGE;
        }
      }
    }

    // Check the last block in the small rank directory that isn't captured.
    int totalBits = rank();
    while (sampleRank <= totalBits) {
      selectSamples[sampleIndex++] = smallRankDirectory.length - 1;
      sampleRank += SELECT_SAMPLE_RANGE;
    }
  }

  @Override
  public int select(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Bad index: " + index);
    }
    if (index >= rank()) {
      return -1;
    }

    int rank = index + 1;

    // Scan in the large rank directory.
    int largeBlockIndex = selectSamples[rank / SELECT_SAMPLE_RANGE] / SMALL_BLOCK_COUNT;
    while (rank > largeRankDirectory[++largeBlockIndex]) {}
    rank -= largeRankDirectory[--largeBlockIndex];

    // Scan within the particular large block.
    int smallBlockIndex = largeBlockIndex * SMALL_BLOCK_COUNT + rank / BLOCK_SIZE + 1;
    int boundaryBlockIndex =
        Math.min(smallRankDirectory.length, (largeBlockIndex + 1) * SMALL_BLOCK_COUNT);
    while ((smallBlockIndex < boundaryBlockIndex)
        && ((smallRankDirectory[smallBlockIndex] & 0xFF) < rank)) {
      smallBlockIndex++;
    }
    rank -= smallRankDirectory[--smallBlockIndex] & 0xFF;

    // Scan within the particular small block.
    int block = vector[smallBlockIndex];
    while (rank > 1) {
      block = (block - 1) & block;
      rank--;
    }

    return smallBlockIndex * BLOCK_SIZE + Integer.numberOfTrailingZeros(block);
  }
}
