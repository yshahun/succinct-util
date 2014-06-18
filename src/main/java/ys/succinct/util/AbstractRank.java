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
 * An abstract implementation of {@link Rank} that expresses some operations via the underlying rank
 * operation.
 *
 * @author Yauheni Shahun
 */
public abstract class AbstractRank implements Rank {

  @Override
  public int excess(int index) {
    return rank(index) * 2 - index - 1;
  }

  @Override
  public int rank0(int index) {
    return index - rank(index) + 1;
  }
}
