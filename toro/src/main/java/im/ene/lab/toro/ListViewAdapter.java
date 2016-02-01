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

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

/**
 * Created by eneim on 2/1/16.
 */
public abstract class ListViewAdapter<VH extends AbsViewHolder> extends BaseAdapter {

  public static final int TAG_KEY = R.integer.tag_key_view_holder;

  @Override public boolean hasStableIds() {
    return true;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    final VH viewHolder;
    if (convertView == null) {
      convertView = createView(parent, getItemViewType(position));
    }

    viewHolder = createViewHolder(convertView);
    // Set current tag to this ViewHolder, or replace old one with new one
    convertView.setTag(TAG_KEY, viewHolder);
    bindViewHolder(viewHolder, position);
    return convertView;
  }

  public abstract View createView(ViewGroup parent, int viewType);

  public abstract VH createViewHolder(View view);

  public abstract void bindViewHolder(VH viewHolder, int position);

  public VH findViewHolderForPosition(AbsListView parent, int position) {
    int firstVisiblePosition = parent.getFirstVisiblePosition();
    int lastVisiblePosition = parent.getLastVisiblePosition();
    if (position < firstVisiblePosition || position > lastVisiblePosition) {
      return null;
    }

    int index = position - firstVisiblePosition;  // >= 0
    View view = parent.getChildAt(index);
    // TODO FIXME need to check
    return createViewHolder(view);
  }
}
