/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.toro;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by eneim on 2/1/16.
 */
final class ToroUtils {

  private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

  private ToroUtils() {
    throw new AssertionError("Not supported");
  }

  static Integer[] asArray(int[] array) {
    if (array == null) {
      return null;
    }

    Integer[] result = new Integer[array.length];
    if (array.length > 0) {
      for (int i = 0; i < array.length; i++) {
        result[i] = array[i];
      }
    }

    return result;
  }

  static <T> List<T> asList(T[] array) {
    return Arrays.asList(array);
  }

  static List<Integer> asList(int[] array) {
    return asList(asArray(array));
  }

  /**
   * Generate a value suitable for use in {@link #setId(int)}.
   * This value will not collide with ID values generated at build time by aapt for R.id.
   *
   * @return a generated ID value
   */
  static int generateViewId() {
    for (; ; ) {
      final int result = sNextGeneratedId.get();
      // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
      int newValue = result + 1;
      if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
      if (sNextGeneratedId.compareAndSet(result, newValue)) {
        return result;
      }
    }
  }
}
