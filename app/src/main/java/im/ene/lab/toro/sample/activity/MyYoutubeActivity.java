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

package im.ene.lab.toro.sample.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import im.ene.lab.toro.ToroAdapter;
import im.ene.lab.toro.ToroViewHolder;
import im.ene.lab.toro.sample.BuildConfig;
import im.ene.lab.toro.sample.R;
import im.ene.lab.toro.sample.data.SimpleVideoObject;
import im.ene.lab.toro.sample.data.VideoSource;
import im.ene.lab.toro.sample.fragment.RecyclerViewFragment;
import im.ene.lab.toro.sample.util.Util;

/**
 * Created by eneim on 2/12/16.
 */
public class MyYoutubeActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    getLayoutInflater().setFactory(this);
    super.onCreate(savedInstanceState);
    if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(android.R.id.content, YoutubeListFragment.newInstance())
          .commit();
    }
  }

  public static class YoutubeListFragment extends RecyclerViewFragment {

    public static YoutubeListFragment newInstance() {
      return new YoutubeListFragment();
    }

    @NonNull @Override protected RecyclerView.LayoutManager getLayoutManager() {
      return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @NonNull @Override protected RecyclerView.Adapter getAdapter() {
      return new Adapter(getChildFragmentManager());
    }
  }

  private static class Adapter extends ToroAdapter<ViewHolder> {

    private final FragmentManager mFragmentManager;
    private YouTubePlayer mYoutubePlayer;

    public Adapter(FragmentManager fragmentManager) {
      this.mFragmentManager = fragmentManager;
    }

    @Nullable @Override protected Object getItem(int position) {
      return new SimpleVideoObject(VideoSource.YOUTUBES[position % VideoSource.YOUTUBES.length]);
    }

    @Override
    public MyYoutubeActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(MyYoutubeActivity.ViewHolder.LAYOUT_RES, parent, false);
      return new MyYoutubeActivity.ViewHolder(this, view);
    }

    @Override public int getItemCount() {
      return VideoSource.YOUTUBES.length * 10;
    }
  }

  static class ViewHolder extends ToroViewHolder
      implements YouTubeThumbnailView.OnInitializedListener,
      YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener {

    private static final int LAYOUT_RES = R.layout.vh_youtube_video;

    private final Adapter mParent;

    @Bind(R.id.thumbnail) YouTubeThumbnailView mThumbnail;
    @Bind(R.id.video_id) TextView mVideoId;
    @Bind(R.id.info) TextView mInfo;
    @Bind(R.id.container) FrameLayout mContainer;
    private int mVideoViewId;
    private SimpleVideoObject mItem;
    private YouTubePlayerSupportFragment mYoutubeFragment;
    // private YouTubePlayer mPlayer;
    private int seekPosition = 0;
    private boolean isSeeking = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ViewHolder(Adapter adapter, View itemView) {
      super(itemView);
      TAG = toString();
      ButterKnife.bind(this, itemView);
      mParent = adapter;
      mVideoViewId = View.generateViewId();
    }

    @Override public boolean wantsToPlay() {
      return super.visibleAreaOffset() >= 0.8f;
    }

    @Override public boolean isAbleToPlay() {
      return true;
    }

    @Nullable @Override public String getVideoId() {
      return mItem.video + " - " + getAdapterPosition();
    }

    @NonNull @Override public View getVideoView() {
      View view = mYoutubeFragment == null ? mContainer : mYoutubeFragment.getView();
      return view != null ? view : mContainer;
    }

    @Override public void onAttachedToParent() {
      super.onAttachedToParent();
      if (mParent != null && mYoutubeFragment != null) {
        mParent.mFragmentManager.beginTransaction().show(mYoutubeFragment).commit();
      }
    }

    @Override public void onDetachedFromParent() {
      super.onDetachedFromParent();
      if (mParent != null && mYoutubeFragment != null) {
        mParent.mFragmentManager.beginTransaction().hide(mYoutubeFragment).commit();
      }
    }

    @Override public void start() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.INVISIBLE);
      }

      if (mParent.mYoutubePlayer != null) {
        mParent.mYoutubePlayer.release();
      }

      if (mYoutubeFragment != null) {
        mYoutubeFragment.initialize(BuildConfig.YOUTUBE_API_KEY,
            new YouTubePlayer.OnInitializedListener() {
              @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
                  YouTubePlayer youTubePlayer, boolean b) {
                mParent.mYoutubePlayer = youTubePlayer;
                youTubePlayer.setPlayerStateChangeListener(ViewHolder.this);
                youTubePlayer.setPlaybackEventListener(ViewHolder.this);
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                if (!b) {
                  if (isSeeking) {
                    isSeeking = false;
                    youTubePlayer.loadVideo(mItem.video, seekPosition);
                  } else {
                    youTubePlayer.loadVideo(mItem.video);
                  }
                }
              }

              @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
                  YouTubeInitializationResult youTubeInitializationResult) {

              }
            });
      }
    }

    @Override public void pause() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }

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

    @Override public void onPlaybackPaused() {
      super.onPlaybackPaused();
    }

    @Override public void onPlaybackStopped() {
      super.onPlaybackStopped();
      if (mParent.mYoutubePlayer != null) {
        try {
          mParent.mYoutubePlayer.release();
        } catch (IllegalStateException er) {
          er.printStackTrace();
        }
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

    @Override public void seekTo(int pos) {
      seekPosition = pos;
      isSeeking = true;
    }

    @Override public boolean isPlaying() {
      try {
        return mParent.mYoutubePlayer != null && mParent.mYoutubePlayer.isPlaying();
      } catch (IllegalStateException er) {
        er.printStackTrace();
        return false;
      }
    }

    @Override public void bind(@Nullable Object object) {
      if (object == null || !(object instanceof SimpleVideoObject)) {
        throw new IllegalArgumentException("Illegal");
      }

      mItem = (SimpleVideoObject) object;

      mVideoId.setText(mItem.video);

      if ((mYoutubeFragment =
          (YouTubePlayerSupportFragment) mParent.mFragmentManager.findFragmentById(mVideoViewId))
          == null) {
        mYoutubeFragment = YouTubePlayerSupportFragment.newInstance();

        View videoView = mContainer.getChildAt(0);
        videoView.setId(mVideoViewId);

        // Create new youtube view holder
        mParent.mFragmentManager.beginTransaction()
            .replace(mVideoViewId, mYoutubeFragment)
            .commit();
      }
    }

    @Override public void onViewHolderBound() {
      super.onViewHolderBound();
      mInfo.setText("Bound");
      mThumbnail.initialize(BuildConfig.YOUTUBE_API_KEY, this);
    }

    @Override public void onPlaybackStarted() {
      super.onPlaybackStarted();
      mInfo.setText("Started");
    }

    @Override public void onPlaybackProgress(int position, int duration) {
      super.onPlaybackProgress(position, duration);
      mInfo.setText(Util.timeStamp(position, duration));
    }

    @Override public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
        YouTubeThumbnailLoader youTubeThumbnailLoader) {
      youTubeThumbnailLoader.setVideo(mItem.video);
    }

    @Override public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
        YouTubeInitializationResult youTubeInitializationResult) {

    }

    private final String TAG;

    @Override public void onLoading() {
      Log.d(TAG, "onLoading() called with: " + "");
    }

    @Override public void onLoaded(String s) {
      Log.d(TAG, "onLoaded() called with: " + "s = [" + s + "]");
    }

    @Override public void onAdStarted() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.INVISIBLE);
      }
    }

    @Override public void onVideoStarted() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.INVISIBLE);
      }
    }

    @Override public void onVideoEnded() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
    }

    @Override public void onError(YouTubePlayer.ErrorReason errorReason) {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
    }

    @Override public void onPlaying() {

    }

    @Override public void onPaused() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
    }

    @Override public void onStopped() {
      if (mThumbnail != null) {
        mThumbnail.setVisibility(View.VISIBLE);
      }
    }

    @Override public void onBuffering(boolean b) {

    }

    @Override public void onSeekTo(int i) {
      Log.d(TAG, "onSeekTo() called with: " + "i = [" + i + "]");
    }

    @Override public String toString() {
      return Integer.toHexString(hashCode()) + " position=" + getAdapterPosition();
    }
  }
}
