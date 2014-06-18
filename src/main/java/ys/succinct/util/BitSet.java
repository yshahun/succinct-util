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
 * An object that represents a set of bits and allows read/write operations on them. It can handle
 * up to {@link Integer#MAX_VALUE} bits.
 *
 * @author Yauheni Shahun
 */
public interface BitSet {

  /**
   * Gets the bit value at the given position.
   *
   * @param index the 0-based index of the bit
   * @return {@code true} if the bit is set to 1, or {@code false} if it's set to 0
   * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
   *         the size of the bit set
   */
  boolean get(int index);

  /**
   * Sets the bit value at the given position.
   *
   * @param index the 0-based index of the bit
   * @param value the boolean value that indicates 1 if it's {@code true} and 0 otherwise
   * @throws IndexOutOfBoundsException if {@code index} is less than 0 or greater than or equal to
   *         the size of the bit set
   */
  void set(int index, boolean value);

  /**
   * Returns the number of bits in the bit set.
   */
  int size();
}
