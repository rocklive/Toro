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

package im.ene.lab.toro.widget;

import android.widget.AbsListView;
import im.ene.lab.toro.AbsListViewScrollListener;
import java.util.ArrayList;

/**
 * Created by eneim on 2/1/16.
 *
 * Helper class for customized classes of ListView and GridView
 */
class ListViewScrollHelper {

  private final AbsListView mListView;

  private ArrayList<AbsListView.OnScrollListener> mListeners = new ArrayList<>();
  private AbsListView.OnScrollListener mLegacyOnScrollListener;

  ListViewScrollHelper(AbsListView mListView) {
    this.mListView = mListView;
  }

  void addOnScrollListener(final AbsListView.OnScrollListener listener) {
    if (listener == null) {
      return;
    }

    mLegacyOnScrollListener = listener;
    // prevent NPE
    if (mListeners == null) {
      mListeners = new ArrayList<>();
    }

    mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        listener.onScrollStateChanged(view, scrollState);
        for (AbsListView.OnScrollListener listener : mListeners) {
          if (listener instanceof AbsListViewScrollListener) {
            listener.onScrollStateChanged(view, scrollState);
          }
        }
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        for (AbsListView.OnScrollListener listener : mListeners) {
          if (listener instanceof AbsListViewScrollListener) {
            listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
          }
        }
      }
    });
  }

  void removeOnScrollListener(AbsListView.OnScrollListener listener) {
    if (mLegacyOnScrollListener == listener) {
      mLegacyOnScrollListener = null;
    }

    if (mListeners == null) {
      return;
    }
    mListeners.remove(listener);
    // Replace old listeners
    mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        for (AbsListView.OnScrollListener item : mListeners) {
          item.onScrollStateChanged(view, scrollState);
        }
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        for (AbsListView.OnScrollListener item : mListeners) {
          item.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
      }
    });
  }
}
