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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.lab.toro.ListViewAdapter;
import im.ene.lab.toro.sample.data.SimpleVideoObject;
import im.ene.lab.toro.sample.data.VideoSource;
import im.ene.lab.toro.sample.viewholder.SampleListViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 2/1/16.
 */
public class SampleListViewAdapter extends ListViewAdapter<SampleListViewHolder> {

  private List<SimpleVideoObject> mVideos = new ArrayList<>();

  public SampleListViewAdapter() {
    super();
    for (String item : VideoSource.SOURCES) {
      mVideos.add(new SimpleVideoObject(item));
    }
  }

  @Override public View createView(ViewGroup parent, int viewType) {
    return LayoutInflater.from(parent.getContext())
        .inflate(SampleListViewHolder.LAYOUT_RES, parent, false);
  }

  @Override public SampleListViewHolder createViewHolder(View view) {
    SampleListViewHolder viewHolder = (SampleListViewHolder) view.getTag(TAG_KEY);
    if (viewHolder == null) {
      viewHolder = new SampleListViewHolder(view);
    }
    return viewHolder;
  }

  @Override public void bindViewHolder(SampleListViewHolder viewHolder, int position) {
    viewHolder.bind(getItem(position));
  }

  @Override public int getCount() {
    return 100;
  }

  @Override public Object getItem(int position) {
    return mVideos.get(position % mVideos.size());
  }

  @Override public long getItemId(int position) {
    return getItem(position).hashCode();
  }
}
