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
 * An object that represents a set of the balanced parentheses and supports a number of the search
 * operations on them. The maximum number of parentheses that can be handled is
 * {@link Integer#MAX_VALUE}.
 *
 * @author Yauheni Shahun
 */
public interface BalancedParentheses {

  /**
   * Finds the position of the closed parenthesis that matches the given open parenthesis.
   *
   * @param openIndex the position (0-based) of the open parenthesis
   * @return 0-based index of the closed parenthesis
   */
  int findClose(int openIndex);

  /**
   * Finds the position of the open parenthesis that matches the given closed parenthesis.
   *
   * @param closeIndex the position (0-based) of the closed parenthesis
   * @return 0-based index of the open parenthesis
   */
  int findOpen(int closeIndex);

  /**
   * Finds the position of the open parenthesis that most tightly encloses the given open
   * parenthesis.
   *
   * @param index the position (0-based) of the open parenthesis
   * @return 0-based index of the enclosing open parenthesis, or {@code -1} if such parenthesis
   *         doesn't exist
   */
  int enclose(int index);
}
