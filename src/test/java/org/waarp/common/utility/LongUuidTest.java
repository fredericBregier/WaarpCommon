/*******************************************************************************
 * This file is part of Waarp Project (named also Waarp or GG).
 *
 *  Copyright (c) 2019, Waarp SAS, and individual contributors by the @author
 *  tags. See the COPYRIGHT.txt in the distribution for a full listing of
 *  individual contributors.
 *
 *  All Waarp Project is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at your
 *  option) any later version.
 *
 *  Waarp is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 *  A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  Waarp . If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.waarp.common.utility;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class LongUuidTest {
  private static final int NB = 1000000;

  @Test
  public void testStructure() {
    LongUuid id = new LongUuid();
    String str = id.toString();

    assertEquals(16, str.length());
  }

  @Test
  public void testParsing() {
    LongUuid id1 = new LongUuid();
    LongUuid id2 = new LongUuid(id1.toString());
    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(id1.getLong(), id2.getLong());

    LongUuid id3 = new LongUuid(id1.getBytes());
    assertEquals(id1, id3);
    LongUuid id4 = new LongUuid(id1.getLong());
    assertEquals(id1, id4);
  }

  @Test
  public void testNonSequentialValue() {
    final int n = NB;
    long[] ids = new long[n];

    for (int i = 0; i < n; i++) {
      ids[i] = new LongUuid().getLong();
    }

    for (int i = 1; i < n; i++) {
      assertTrue(ids[i - 1] != ids[i]);
    }
  }

  @Test
  public void testGetBytesImmutability() {
    LongUuid id = new LongUuid();
    byte[] bytes = id.getBytes();
    byte[] original = Arrays.copyOf(bytes, bytes.length);
    bytes[0] = 0;
    bytes[1] = 0;
    bytes[2] = 0;

    assertArrayEquals(id.getBytes(), original);
  }

  @Test
  public void testConstructorImmutability() {
    LongUuid id = new LongUuid();
    byte[] bytes = id.getBytes();
    byte[] original = Arrays.copyOf(bytes, bytes.length);

    LongUuid id2 = new LongUuid(bytes);
    bytes[0] = 0;
    bytes[1] = 0;

    assertArrayEquals(id2.getBytes(), original);
  }

  @Test
  public void testPIDField() throws Exception {
    LongUuid id = new LongUuid();

    assertEquals(LongUuid.jvmProcessId(), id.getProcessId());
  }

  @Test
  public void testForDuplicates() {
    int n = NB;
    Set<Long> uuids = new HashSet<Long>();
    LongUuid[] uuidArray = new LongUuid[n];

    long start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      uuidArray[i] = new LongUuid();
    }
    long stop = System.currentTimeMillis();
    System.out.println(
        "Time = " + (stop - start) + " so " + n * 1000 / (stop - start) +
        " Uuids/s");

    for (int i = 0; i < n; i++) {
      uuids.add(uuidArray[i].getLong());
    }

    System.out.println("Create " + n + " and get: " + uuids.size());
    assertEquals(n, uuids.size());
    int i = 1;
    int largest = 0;
    for (; i < n; i++) {
      if (uuidArray[i].getTimestamp() != uuidArray[i - 1].getTimestamp()) {
        int j = i + 1;
        long time = uuidArray[i].getTimestamp();
        for (; j < n; j++) {
          if (uuidArray[j].getTimestamp() != time) {
            if (largest < j - i + 1) {
              largest = j - i + 1;
            }
            i = j;
            break;
          }
        }
      }
    }
    if (largest == 0) {
      largest = n;
    }
    System.out.println(uuidArray[0] + "(" + uuidArray[0].getTimestamp() + ":" +
                       uuidArray[0].getLong() + ") - " +
                       uuidArray[n - 1] + "(" +
                       uuidArray[n - 1].getTimestamp() + ":" +
                       uuidArray[n - 1].getLong() + ") = "
                       + (uuidArray[n - 1].getLong() - uuidArray[0].getLong() +
                          1));
    System.out.println(largest + " different consecutive elements");
  }

  @Test
  public void concurrentGeneration() throws Exception {
    int numThreads = 10;
    Thread[] threads = new Thread[numThreads];
    int n = NB;
    LongUuid[] uuids = new LongUuid[n];

    long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new Generator(n / numThreads, uuids, i, numThreads);
      threads[i].start();
    }

    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    long stop = System.currentTimeMillis();
    System.out.println(
        "Time = " + (stop - start) + " so " + n * 1000 / (stop - start) +
        " Uuids/s");

    Set<LongUuid> uuidSet = new HashSet<LongUuid>();

    int effectiveN = n / numThreads * numThreads;
    for (int i = 0; i < effectiveN; i++) {
      uuidSet.add(uuids[i]);
    }

    assertEquals(effectiveN, uuidSet.size());
  }

  private static class Generator extends Thread {
    private final LongUuid[] uuids;
    int id;
    int n;
    int numThreads;

    public Generator(int n, LongUuid[] uuids, int id, int numThreads) {
      this.n = n;
      this.uuids = uuids;
      this.id = id;
      this.numThreads = numThreads;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[numThreads * i + id] = new LongUuid();
      }
    }
  }
}