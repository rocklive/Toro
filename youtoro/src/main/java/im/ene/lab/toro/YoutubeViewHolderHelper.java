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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import com.google.android.youtube.player.YouTubePlayer;

/**
 * Created by eneim on 2/15/16.
 */
final class YoutubeViewHolderHelper
    implements VideoViewHolderHelper, YouTubePlayer.PlayerStateChangeListener,
    YouTubePlayer.PlaybackEventListener {

  private static final String TAG = "YoutubeViewHolderHelper";

  YoutubeViewHolderHelper() {

  }

  /**
   * Callback from {@link RecyclerView.Adapter#onViewAttachedToWindow(RecyclerView.ViewHolder)}
   *
   * @param player the {@link ToroPlayer} which is attached to current ViewHolder
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   */
  @Override public void onAttachedToParent(ToroPlayer player, View itemView, ViewParent parent) {

  }

  /**
   * Callback from {@link RecyclerView.Adapter#onViewDetachedFromWindow(RecyclerView.ViewHolder)}
   *
   * @param player the {@link ToroPlayer} which is attached to current ViewHolder
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   */
  @Override public void onDetachedFromParent(ToroPlayer player, View itemView, ViewParent parent) {

  }

  /**
   * Support long press on Video, called by {@link View.OnLongClickListener#onLongClick(View)}
   *
   * @param player the {@link ToroPlayer} which is attached to current ViewHolder
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   * @return boolean response to {@link View.OnLongClickListener#onLongClick(View)}
   */
  @Override public boolean onItemLongClick(ToroPlayer player, View itemView, ViewParent parent) {
    return false;
  }

  /**
   * Callback from {@link MediaPlayer.OnPreparedListener#onPrepared(MediaPlayer)}
   *
   * @param player current ToroPlayer instance
   * @param itemView main View of current ViewHolder
   * @param parent parent which holds current ViewHolder
   * @param mediaPlayer current MediaPlayer
   */
  @Override public void onPrepared(ToroPlayer player, View itemView, ViewParent parent,
      @Nullable MediaPlayer mediaPlayer) {

  }

  /**
   * Callback from {@link MediaPlayer.OnCompletionListener#onCompletion(MediaPlayer)}
   *
   * @param player current ToroPlayer instance
   * @param mp completed MediaPlayer
   */
  @Override public void onCompletion(ToroPlayer player, MediaPlayer mp) {

  }

  /**
   * Callback from {@link MediaPlayer.OnErrorListener#onError(MediaPlayer, int, int)}
   *
   * @param player current ToroPlayer instance
   * @param mp current MediaPlayer
   */
  @Override public boolean onError(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    return false;
  }

  /**
   * Callback from {@link MediaPlayer.OnInfoListener#onInfo(MediaPlayer, int, int)}
   *
   * @param player current ToroPlayer instance
   * @param mp current MediaPlayer
   */
  @Override public boolean onInfo(ToroPlayer player, MediaPlayer mp, int what, int extra) {
    return false;
  }

  /**
   * Callback from {@link MediaPlayer.OnSeekCompleteListener#onSeekComplete(MediaPlayer)}
   */
  @Override public void onSeekComplete(ToroPlayer player, MediaPlayer mp) {
    Log.d(TAG, "onSeekComplete() called with: " + "player = [" + player + "], mp = [" + mp + "]");
  }

  // From YoutubePlayer
  @Override public void onPlaying() {
    Log.d(TAG, "onPlaying() called with: " + "");
  }

  @Override public void onPaused() {
    Log.d(TAG, "onPaused() called with: " + "");
  }

  @Override public void onStopped() {
    Log.d(TAG, "onStopped() called with: " + "");
  }

  @Override public void onBuffering(boolean b) {
    Log.d(TAG, "onBuffering() called with: " + "b = [" + b + "]");
  }

  @Override public void onSeekTo(int i) {
    Log.d(TAG, "onSeekTo() called with: " + "i = [" + i + "]");
  }

  @Override public void onLoading() {
    Log.d(TAG, "onLoading() called with: " + "");
  }

  @Override public void onLoaded(String s) {
    Log.d(TAG, "onLoaded() called with: " + "s = [" + s + "]");
  }

  @Override public void onAdStarted() {
    Log.d(TAG, "onAdStarted() called with: " + "");
  }

  @Override public void onVideoStarted() {
    Log.d(TAG, "onVideoStarted() called with: " + "");
  }

  @Override public void onVideoEnded() {
    Log.d(TAG, "onVideoEnded() called with: " + "");
  }

  @Override public void onError(YouTubePlayer.ErrorReason errorReason) {
    Log.d(TAG, "onError() called with: " + "errorReason = [" + errorReason + "]");
  }

  public void onYoutubePlayerChanged(YouTubePlayer newPlayer) {
    Log.e(TAG, "onYoutubePlayerChanged() called with: " + "newPlayer = [" + newPlayer + "]");
  }
}
