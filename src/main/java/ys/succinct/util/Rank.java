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
 * An object that supports rank-based operations on the bit set that it wraps. Bit sets of the
 * maximum size {@link Integer#MAX_VALUE} can be handled.
 *
 * @author Yauheni Shahun
 */
public interface Rank {

  /**
   * Gets the difference between the number of 1s and the number of 0s from the beginning and up to
   * the given position (inclusively) in the bit set.
   *
   * @param index the bit position (0-based) in the bit set
   * @return excess value of the bit
   * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
   *         the size of the bit set
   */
  int excess(int index);

  /**
   * Gets the number of 1s from the beginning and up to the given position (inclusively) in the bit
   * set.
   *
   * @param index the bit position (0-based) in the bit set
   * @return rank of 1s of the bit
   * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
   *         the size of the bit set
   */
  int rank(int index);

  /**
   * Gets the total number of 1s in the bit set.
   *
   * @return rank of 1s of the bit set
   */
  int rank();

  /**
   * Gets the number of 0s from the beginning and up to the given position (inclusively) in the bit
   * set.
   *
   * @param index the bit position (0-based) in the bit set
   * @return rank of 0s of the bit
   * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
   *         the size of the bit set
   */
  int rank0(int index);
}
