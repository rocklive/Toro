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

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.View;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by eneim on 2/15/16.
 */
public abstract class YoutubeViewHolder extends ToroViewHolder
    implements YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener {

  private static final String TAG = "YoutubeViewHolder";

  /**
   * This setup will offer {@link YouTubePlayer.PlayerStyle#CHROMELESS} to youtube player
   */
  protected static final int CHROMELESS = 0b01;

  /**
   * This setup will offer {@link YouTubePlayer.PlayerStyle#MINIMAL} to youtube player
   */
  protected static final int MINIMUM = 0b10;

  /**
   * Parent Adapter which holds some important controllers
   */
  protected final YoutubeListAdapter mParent;

  /**
   * Id for {@link YouTubePlayerSupportFragment}, will be generated manually
   */
  protected final int mFragmentId;

  private final YoutubeViewHolderHelper mHelper;
  protected YouTubePlayerSupportFragment mYoutubeFragment;
  private int seekPosition = 0;
  private boolean isSeeking = false;
  private boolean isStarting = false;

  public YoutubeViewHolder(View itemView, YoutubeListAdapter parent) {
    super(itemView);
    this.mHelper = new YoutubeViewHolderHelper();
    this.mParent = parent;
    if (this.mParent.mFragmentManager == null) {
      throw new IllegalArgumentException(
          "This View requires a YoutubeListAdapter parent which holds a non-null FragmentManager");
    }
    this.mFragmentId = ToroUtils.generateViewId();
  }

  @Override public final boolean wantsToPlay() {
    Log.d(TAG, toString() + "#wantsToPlay() called with: " + "");
    return super.visibleAreaOffset() >= 1.0f;  // Youtube will decide again
  }

  final boolean isStarting() {
    return this.isStarting;
  }

  @Override public final boolean isAbleToPlay() {
    Log.d(TAG, toString() + "#isAbleToPlay() called with: " + "");
    return true;
  }

  @CallSuper @Override public void onViewHolderBound() {
    Log.d(TAG, toString() + "#onViewHolderBound() called with: " + "");
    super.onViewHolderBound();
    if (itemView.findViewById(mFragmentId) == null) {
      throw new RuntimeException("View with Id: " + mFragmentId + " must be setup");
    }

    if ((mYoutubeFragment =
        (YouTubePlayerSupportFragment) mParent.mFragmentManager.findFragmentById(mFragmentId))
        == null) {
      mYoutubeFragment = YouTubePlayerSupportFragment.newInstance();
      // Create new youtube view holder
      mParent.mFragmentManager.beginTransaction().replace(mFragmentId, mYoutubeFragment).commit();
    }
  }

  @CallSuper @Override public void start() {
    Log.d(TAG, toString() + "#start() called with: " + "");
    isStarting = true;
    // Release current youtube player first. Prevent resource conflict
    if (mParent.mYoutubePlayer != null) {
      mParent.mYoutubePlayer.release();
    }
    // Re-setup the Player. This is annoying though.
    if (mYoutubeFragment != null) {
      mYoutubeFragment.initialize(Youtoro.sYoutubeApiKey,
          new YouTubePlayer.OnInitializedListener() {
            @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
                YouTubePlayer youTubePlayer, boolean isRecover) {
              mHelper.onYoutubePlayerChanged(youTubePlayer);
              mParent.mYoutubePlayer = youTubePlayer;
              youTubePlayer.setPlayerStateChangeListener(YoutubeViewHolder.this);
              youTubePlayer.setPlaybackEventListener(YoutubeViewHolder.this);
              // Force player style
              youTubePlayer.setPlayerStyle(
                  getPlayerStyle() == CHROMELESS ? YouTubePlayer.PlayerStyle.CHROMELESS
                      : YouTubePlayer.PlayerStyle.MINIMAL);
              if (!isRecover) {
                if (isSeeking) {
                  isSeeking = false;
                  youTubePlayer.loadVideo(getYoutubeVideoId(), seekPosition);
                } else {
                  youTubePlayer.loadVideo(getYoutubeVideoId());
                }
              }
            }

            @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
                YouTubeInitializationResult youTubeInitializationResult) {

            }
          });
    }
  }

  @CallSuper @Override public void pause() {
    Log.d(TAG, toString() + "#pause() called with: " + "");
    isStarting = false;
    if (mParent.mYoutubePlayer != null) {
      try {
        mParent.mYoutubePlayer.pause();
      } catch (IllegalStateException er) {
        er.printStackTrace();
      }
    }
  }

  @Override public int getDuration() {
    try {
      return mParent.mYoutubePlayer != null ? mParent.mYoutubePlayer.getDurationMillis() : -1;
    } catch (IllegalStateException er) {
      er.printStackTrace();
      return -1;
    }
  }

  @Override public int getCurrentPosition() {
    try {
      return mParent.mYoutubePlayer != null ? mParent.mYoutubePlayer.getCurrentTimeMillis() : 0;
    } catch (IllegalStateException er) {
      er.printStackTrace();
      return 0;
    }
  }

  @CallSuper @Override public void seekTo(int pos) {
    isSeeking = true;
    seekPosition = pos;
    Log.d(TAG, toString() + "#seekTo() called with: " + "pos = [" + pos + "]");
  }

  @Override public boolean isPlaying() {
    Log.d(TAG, toString() + "#isPlaying() called with: " + "");
    try {
      // is loading the video or playing it
      return isStarting || (mParent.mYoutubePlayer != null && mParent.mYoutubePlayer.isPlaying());
    } catch (IllegalStateException er) {
      er.printStackTrace();
      return isStarting;
    }
  }

  // Youtube video id for this view. This method should be used dynamically
  public abstract String getYoutubeVideoId();

  @CallSuper @Override public void onLoading() {
    mHelper.onLoading();
    Log.d(TAG, toString() + "#onLoading() called with: " + "");
  }

  @CallSuper @Override public void onLoaded(String videoId) {
    mHelper.onLoaded(videoId);
    mHelper.onPrepared(this, itemView, itemView.getParent(), null);
    Log.d(TAG, toString() + "#onLoaded() called with: " + "videoId = [" + videoId + "]");
  }

  @CallSuper @Override public void onAdStarted() {
    mHelper.onAdStarted();
    Log.d(TAG, toString() + "#onAdStarted() called with: " + "");
  }

  @CallSuper @Override public void onVideoStarted() {
    mHelper.onVideoStarted();
    Log.d(TAG, toString() + "#onVideoStarted() called with: " + "");
  }

  @CallSuper @Override public void onVideoEnded() {
    mHelper.onCompletion(this, null);
    mHelper.onVideoEnded();
    Log.d(TAG, toString() + "#onVideoEnded() called with: " + "");
  }

  @CallSuper @Override public void onError(YouTubePlayer.ErrorReason errorReason) {
    mHelper.onError(this, null, 0, 0);
    mHelper.onError(errorReason);
    Log.d(TAG, toString() + "#onError() called with: " + "errorReason = [" + errorReason + "]");
  }

  @CallSuper @Override public final void onPlaying() {
    isStarting = false;
    mHelper.onPlaying();
    Log.d(TAG, toString() + "#onPlaying() called with: " + "");
  }

  // Paused by API's button. Should not dispatch any custom behavior.
  @CallSuper @Override public final void onPaused() {
    mHelper.onPaused();
    Log.d(TAG, toString() + "#onPaused() called with: " + "");
  }

  @CallSuper @Override public final void onStopped() {
    mHelper.onStopped();
    Log.d(TAG, toString() + "#onStopped() called with: " + "");
  }

  @CallSuper @Override public void onBuffering(boolean isBuffering) {
    mHelper.onBuffering(isBuffering);
    Log.d(TAG, toString() + "#onBuffering() called with: " + "isBuffering = [" + isBuffering + "]");
  }

  // Called internal. Youtube's Playback event is internally called by API, so User should not
  // dispatch them
  @CallSuper @Override public final void onSeekTo(int position) {
    seekPosition = position;
    isSeeking = true;
    Log.d(TAG, toString() + "#onSeekTo() called with: " + "position = [" + position + "]");
  }

  /**
   * This library will force user to use either {@link YouTubePlayer.PlayerStyle#MINIMAL} or {@link
   * YouTubePlayer.PlayerStyle#CHROMELESS}. User should override this to provide her expected UI
   */
  @PlayerStyle protected int getPlayerStyle() {
    return MINIMUM;
  }

  @IntDef({
      CHROMELESS, MINIMUM
  }) @Retention(RetentionPolicy.SOURCE) public @interface PlayerStyle {
  }
}
