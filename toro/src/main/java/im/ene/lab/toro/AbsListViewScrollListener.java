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

import android.graphics.Rect;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by eneim on 1/31/16.
 *
 * @hide
 */
public class AbsListViewScrollListener implements AbsListView.OnScrollListener, ToroScrollHelper {

  private int mLastVideoPosition;

  private final int VALUE_INVALID = -1;

  private int mFirstVisibleItem = VALUE_INVALID;
  private int mLastVisibleItem = VALUE_INVALID;
  private int mVisibleItemCount = VALUE_INVALID;
  private int mTotalItemCount = VALUE_INVALID;

  private Rect mParentRect;
  private Rect mChildRect;

  protected final ToroManager mManager;

  public AbsListViewScrollListener(ToroManager manager) {
    this.mManager = manager;

    mLastVideoPosition = -1;
    mParentRect = new Rect();
    mChildRect = new Rect();
  }

  @Override public void onScrollStateChanged(AbsListView parent, int scrollState) {
    if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
      return;
    }

    mLastVisibleItem = parent.getListPaddingBottom();

    if (mTotalItemCount == VALUE_INVALID
        || mVisibleItemCount == VALUE_INVALID
        || mFirstVisibleItem == VALUE_INVALID
        || mLastVisibleItem == VALUE_INVALID) {
      return;
    }

    if (mFirstVisibleItem > mLastVisibleItem) {
      // Illegal state
      return;
    }

    ListAdapter _adapter = parent.getAdapter();
    if (!(_adapter instanceof ListViewAdapter)) {
      return;
    }

    final ListViewAdapter adapter = (ListViewAdapter) _adapter;

    List<ToroPlayer> candidates = new ArrayList<>();
    ToroPlayer lastVideo = mManager.getPlayer();
    if (lastVideo != null) {
      mLastVideoPosition = lastVideo.getPositionInAdapter();
      AbsViewHolder viewHolder = adapter.findViewHolderForPosition(parent, mLastVideoPosition);
      // Re-calculate the rectangles
      if (viewHolder != null) {
        parent.getLocalVisibleRect(mParentRect);
        viewHolder.itemView.getLocalVisibleRect(mChildRect);
        if (lastVideo.wantsToPlay(mParentRect, mChildRect)) {
          candidates.add(lastVideo);
        }
      }
    }

    int videoPosition = -1;
    if (parent.getAdapter() instanceof ListViewAdapter) {
      ToroPlayer player;
      for (int idx = mFirstVisibleItem; idx <= mLastVisibleItem; idx++) {
        AbsViewHolder viewHolder =
            ((ListViewAdapter) parent.getAdapter()).findViewHolderForPosition(parent, idx);
        if (viewHolder != null && viewHolder instanceof ToroPlayer) {
          player = (ToroPlayer) viewHolder;
          parent.getLocalVisibleRect(mParentRect);
          viewHolder.itemView.getLocalVisibleRect(mChildRect);
          // check that view position
          if (player.wantsToPlay(mParentRect, mChildRect)) {
            if (!candidates.contains(player)) {
              candidates.add(player);
            }
          }
        }
      }

      if (Toro.getPolicy().requireCompletelyVisible()) {
        for (Iterator<ToroPlayer> iterator = candidates.iterator(); iterator.hasNext(); ) {
          if (iterator.next().visibleAreaOffset() < 1.f) {
            iterator.remove();
          }
        }
      }

      player = Toro.getPolicy().getPlayer(candidates);

      if (player == null) {
        return;
      }

      for (ToroPlayer candidate : candidates) {
        if (candidate == player) {
          videoPosition = candidate.getPositionInAdapter();
          break;
        }
      }

      if (videoPosition == mLastVideoPosition) {  // Nothing changes, keep going
        if (lastVideo != null && !lastVideo.isPlaying()) {
          mManager.startVideo(lastVideo);
        }
        return;
      }

      if (lastVideo != null) {
        mManager.saveVideoState(lastVideo.getVideoId(), lastVideo.getCurrentPosition(),
            lastVideo.getDuration());
        if (lastVideo.isPlaying()) {
          mManager.pauseVideo(lastVideo);
        }
      }

      // Switch video
      lastVideo = player;
      mLastVideoPosition = videoPosition;

      mManager.setPlayer(lastVideo);
      mManager.restoreVideoState(lastVideo, lastVideo.getVideoId());
      mManager.startVideo(lastVideo);
    }
  }

  @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    mFirstVisibleItem = firstVisibleItem;
    mVisibleItemCount = visibleItemCount;
    mTotalItemCount = totalItemCount;
  }

  @Override public ToroManager getManager() {
    return mManager;
  }
}
