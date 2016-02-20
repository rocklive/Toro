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

import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewParent;

/**
 * Created by eneim on 2/19/16.
 */
public abstract class BaseViewHolderHelper implements VideoViewHolderHelper {

  @Override public void onPrepared(ToroPlayer player, View itemView, ViewParent parent,
      MediaPlayer mediaPlayer) {
    Toro.checkNotNull();
    Toro.sInstance.onPrepared(player, itemView, parent, mediaPlayer);
  }

  @Override public void onCompletion(ToroPlayer player, MediaPlayer mp) {
    Toro.checkNotNull();
    Toro.sInstance.onCompletion(player, mp);
  }

  @Override public boolean onError(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    Toro.checkNotNull();
    return Toro.sInstance.onError(player, mp, what, extra);
  }

  @Override public boolean onInfo(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    Toro.checkNotNull();
    return Toro.sInstance.onInfo(player, mp, what, extra);
  }

  @Override public void onSeekComplete(ToroPlayer player, MediaPlayer mp) {
    Toro.checkNotNull();
    Toro.sInstance.onSeekComplete(player, mp);
  }
}
