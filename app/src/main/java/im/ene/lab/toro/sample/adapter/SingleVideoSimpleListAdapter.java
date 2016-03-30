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

package im.ene.lab.toro.sample.adapter;

import android.support.annotation.Nullable;
import im.ene.lab.toro.sample.data.SimpleObject;
import im.ene.lab.toro.sample.data.SimpleVideoObject;

/**
 * Created by eneim on 2/3/16.
 */
public class SingleVideoSimpleListAdapter extends BaseSampleAdapter {

  public SingleVideoSimpleListAdapter() {
    super();
  }

  @Nullable @Override protected Object getItem(int position) {
    if (position == 1) {
      return mVideos.get(0);
    }
    return new SimpleObject();
  }

  @Override public int getItemViewType(int position) {
    Object item = getItem(position);
    return item instanceof SimpleVideoObject ? VIEW_TYPE_VIDEO_MIXED : VIEW_TYPE_NO_VIDEO;
  }
}
