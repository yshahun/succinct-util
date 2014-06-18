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
 * An object that supports the select operation on the bit set that it wraps. Bit sets of the
 * maximum size {@link Integer#MAX_VALUE} can be handled.
 *
 * @author Yauheni Shahun
 */
public interface Select {

  /**
   * Gets the position of the i-th 1 bit in the bit set.
   *
   * @param i the ordinal number of 1 bit (0-based)
   * @return 0-based index of the 1 bit or {@code -1} if {@code i} is greater than or equal to the
   *         rank of the bit set
   * @throws IndexOutOfBoundsException if {@code i} is less than 0 or greater than or equal to the
   *         size of the bit set
   */
  int select(int i);
}
