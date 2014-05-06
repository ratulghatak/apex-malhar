/*
 * Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
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
package com.datatorrent.demos.uniquecountdemo;

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.InputOperator;
import com.datatorrent.lib.util.KeyHashValPair;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;

/*
    Generate random keys.
 */
public class RandomKeysGenerator implements InputOperator
{

  protected int numKeys = 100;
  protected int tuppleBlast = 1000;
  protected long sleepTime = 10;
  protected Map<Integer, MutableInt> history = new HashMap<Integer, MutableInt>();
  private Random random = new Random();
  private Date date = new Date();
  private long start;

  public transient DefaultOutputPort<Integer> outPort = new DefaultOutputPort<Integer>();
  public transient DefaultOutputPort<KeyHashValPair<Integer, Integer>> verificationPort =
      new DefaultOutputPort<KeyHashValPair<Integer, Integer>>();

  @Override
  public void emitTuples()
  {
    for (int i = 0; i < tuppleBlast; i++) {
      int key = random.nextInt(numKeys);

      // maintain history for later verification.
      MutableInt count = history.get(key);
      if (count == null) {
        count = new MutableInt(0);
        history.put(key, count);
      }
      count.increment();

      outPort.emit(key);
    }
    try {
      Thread.sleep(sleepTime);
    } catch (Exception ex) {

    }
  }

  public RandomKeysGenerator()
  {
    start = date.getTime();
  }

  @Override
  public void beginWindow(long l)
  {

  }

  @Override
  public void endWindow()
  {
    for (Map.Entry<Integer, MutableInt> e : history.entrySet()) {
      verificationPort.emit(new KeyHashValPair<Integer, Integer>(e.getKey(), e.getValue().toInteger()));
    }
    history.clear();
  }

  @Override
  public void setup(Context.OperatorContext operatorContext)
  {

  }

  @Override
  public void teardown()
  {

  }
}
