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
 * Utilities for handling the balanced parentheses.
 *
 * @author Yauheni Shahun
 */
public final class ParenthesesUtils {

  private ParenthesesUtils() {}

  /**
   * Searches the position with the given excess within the {@code int} block in the forward
   * direction (from the lowest bit to the highest bit).
   *
   * @param block the 32-bit block
   * @param startIndex the 0-based position that the search starts from
   * @param excess the excess up to the starting position (inclusively)
   * @param searchExcess the excess to find
   * @return 0-based position with the required excess, or {@code 32} if no such excess is found
   *         within the block
   */
  public static int getForwardExcessIndex(int block, int startIndex, int excess, int searchExcess) {
    if (startIndex < 0 || startIndex > 31) {
      throw new IndexOutOfBoundsException("Invalid startIndex: " + startIndex);
    }
    if (searchExcess == excess) {
      return startIndex;
    }

    int index = startIndex;
    int mask = 1 << startIndex;
    while (mask != 0 && excess != searchExcess) {
      mask <<= 1;
      excess += ((block & mask) == 0) ? -1 : 1;
      index++;
    }
    return index;
  }

  /**
   * Searches the position with the given excess within the entire {@code int} block in the forward
   * direction (from the lowest bit to the highest bit).
   *
   * @see #getForwardExcessIndex(int, int, int, int)
   */
  public static int getForwardExcessIndex(int block, int excess, int searchExcess) {
    return getForwardExcessIndex(block, 0, excess, searchExcess);
  }

  /**
   * Searches the position with the given excess within the {@code int} block in the backward
   * direction (from the highest bit to the lowest bit).
   *
   * @param block the 32-bit block
   * @param startIndex the 0-based position that the search starts from
   * @param excess the excess up to the starting position (inclusively)
   * @param searchExcess the excess to find
   * @return 0-based position within the block, or {@code -1} if this position immediately precedes
   *         the block, or {@code -2} if no such excess is found
   */
  public static int getBackwardExcessIndex(int block, int startIndex, int excess, int searchExcess) {
    if (startIndex < 0 || startIndex > 31) {
      throw new IndexOutOfBoundsException("Invalid startIndex: " + startIndex);
    }
    if (searchExcess == excess) {
      return startIndex;
    }

    int index = startIndex - 1;
    int mask = 1 << startIndex;
    excess += ((block & mask) == 0) ? 1 : -1;
    while (mask != 0 && excess != searchExcess) {
      mask >>>= 1;
      excess += ((block & mask) == 0) ? 1 : -1;
      index--;
    }
    return index;
  }

  /**
   * Searches the position with the given excess within the entire {@code int} block in the backward
   * direction (from the highest bit to the lowest bit).
   *
   * @see #getBackwardExcessIndex(int, int, int, int)
   */
  public static int getBackwardExcessIndex(int block, int excess, int searchExcess) {
    return getBackwardExcessIndex(block, 31, excess, searchExcess);
  }
}
