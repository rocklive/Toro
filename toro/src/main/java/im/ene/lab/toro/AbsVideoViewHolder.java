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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by eneim on 2/1/16.
 */
public abstract class AbsVideoViewHolder extends AbsViewHolder implements ToroPlayer {

  public AbsVideoViewHolder(View itemView) {
    super(itemView);
  }

  @Override public boolean wantsToPlay(@Nullable Rect parentRect, @NonNull Rect childRect) {
    return false;
  }

  @Override public float visibleAreaOffset() {
    return 0;
  }

  @Nullable @Override public Long getVideoId() {
    return null;
  }

  @Override public int getPositionInAdapter() {
    return 0;
  }

  @Override public void onActivityPaused() {

  }

  @Override public void onActivityResumed() {

  }

  @Override public void start() {

  }

  @Override public void pause() {

  }

  @Override public int getDuration() {
    return 0;
  }

  @Override public int getCurrentPosition() {
    return 0;
  }

  @Override public void seekTo(int pos) {

  }

  @Override public boolean isPlaying() {
    return false;
  }

  @Override public int getBufferPercentage() {
    return 0;
  }

  @Override public boolean canPause() {
    return false;
  }

  @Override public boolean canSeekBackward() {
    return false;
  }

  @Override public boolean canSeekForward() {
    return false;
  }

  @Override public int getAudioSessionId() {
    return 0;
  }
}
