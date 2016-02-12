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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
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

  static class ViewHolder extends ToroViewHolder implements YouTubePlayer.OnInitializedListener {

    private static final int LAYOUT_RES = R.layout.vh_youtube_video;

    private final Adapter mParent;

    @Bind(R.id.title) TextView mTitle;
    private ViewGroup mContainer;
    private int mVideoViewId;
    private SimpleVideoObject mItem;
    private YouTubePlayerSupportFragment mYoutubeFragment;
    private YouTubePlayer mPlayer;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ViewHolder(Adapter adapter, View itemView) {
      super(itemView);
      mContainer = (ViewGroup) itemView;
      mParent = adapter;
      ButterKnife.bind(this, itemView);
      mVideoViewId = View.generateViewId();
    }

    @Override public boolean wantsToPlay() {
      return mPlayer != null && super.visibleAreaOffset() >= 0.9;
    }

    @Override public boolean isAbleToPlay() {
      return mPlayer != null;
    }

    @Nullable @Override public String getVideoId() {
      return mItem.video;
    }

    @NonNull @Override public View getVideoView() {
      View view = mYoutubeFragment == null ? mContainer : mYoutubeFragment.getView();
      return view != null ? view : mContainer;
    }

    @Override public void start() {
      if (mPlayer != null) {
        mPlayer.play();
      }
    }

    @Override public void pause() {
      if (mPlayer != null) {
        mPlayer.pause();
      }
    }

    @Override public int getDuration() {
      return mPlayer != null ? mPlayer.getDurationMillis() : -1;
    }

    @Override public int getCurrentPosition() {
      return mPlayer != null ? mPlayer.getCurrentTimeMillis() : 0;
    }

    @Override public void seekTo(int pos) {
      if (mPlayer != null) {
        mPlayer.seekToMillis(pos);
      }
    }

    @Override public boolean isPlaying() {
      return mPlayer != null && mPlayer.isPlaying();
    }

    @Override public void bind(@Nullable Object object) {
      if (object == null || !(object instanceof SimpleVideoObject)) {
        throw new IllegalArgumentException("Illegal");
      }

      mItem = (SimpleVideoObject) object;
      mTitle.setText(mItem.video);

      if ((mYoutubeFragment =
          (YouTubePlayerSupportFragment) mParent.mFragmentManager.findFragmentById(mVideoViewId))
          == null) {
        mYoutubeFragment = YouTubePlayerSupportFragment.newInstance();
        // Create new youtube view holder
        View videoView = mContainer.findViewById(mVideoViewId);
        if (videoView == null) {
          if (mContainer.getChildAt(1) != null) {
            mContainer.removeViewAt(1);
          }

          videoView = new FrameLayout(mContainer.getContext());
          LinearLayout.LayoutParams params =
              new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT);
          videoView.setLayoutParams(params);
          videoView.setMinimumHeight(Util.dpToPx(mContainer.getContext(), 120.f));
          videoView.setId(mVideoViewId);
          mContainer.addView(videoView);
        }

        mParent.mFragmentManager.beginTransaction()
            .replace(mVideoViewId, mYoutubeFragment)
            .commit();
      }
    }

    @Override public void onViewHolderBound() {
      super.onViewHolderBound();
      if (mYoutubeFragment != null) {
        mYoutubeFragment.initialize(BuildConfig.YOUTUBE_API_KEY, this);
      }
    }

    @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
        YouTubePlayer youTubePlayer, boolean wasRestore) {
      mPlayer = youTubePlayer;
      youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
      if (!wasRestore) {
        youTubePlayer.cueVideo(mItem.video);
        youTubePlayer.pause();
      }
    }

    @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
        YouTubeInitializationResult youTubeInitializationResult) {
      mPlayer = null;
    }
  }
}
