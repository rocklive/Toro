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

package im.ene.lab.toro.sample.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import com.squareup.picasso.Picasso;
import im.ene.lab.toro.ToroAdapter;
import im.ene.lab.toro.sample.R;
import im.ene.lab.toro.sample.util.GetPlaylistAsyncTask;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 2/9/16.
 */
public class YoutubeVideosFragment extends Fragment {

  public static final String TAG = "YoutubeVideosFragment";

  private static final DecimalFormat sFormatter = new DecimalFormat("#,###,###");
  @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

  public static YoutubeVideosFragment newInstance() {
    return new YoutubeVideosFragment();
  }

  private static String parseDuration(String in) {
    boolean hasSeconds = in.indexOf('S') > 0;
    boolean hasMinutes = in.indexOf('M') > 0;

    String s;
    if (hasSeconds) {
      s = in.substring(2, in.length() - 1);
    } else {
      s = in.substring(2, in.length());
    }

    String minutes = "0";
    String seconds = "00";

    if (hasMinutes && hasSeconds) {
      String[] split = s.split("M");
      minutes = split[0];
      seconds = split[1];
    } else if (hasMinutes) {
      minutes = s.substring(0, s.indexOf('M'));
    } else if (hasSeconds) {
      seconds = s;
    }

    // pad seconds with a 0 if less than 2 digits
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }

    return minutes + ":" + seconds;
  }

  private PlaylistCardAdapter mAdapter;
  private Playlist mPlaylist;
  private YouTube mYouTubeDataApi;
  private final GsonFactory mJsonFactory = new GsonFactory();
  private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();

  private static final String YOUTUBE_PLAYLIST = "PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0";

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mYouTubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null).setApplicationName(
        getResources().getString(R.string.app_name)).build();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.generic_recycler_view, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (mPlaylist != null) {
      initAdapter(mPlaylist);
    } else {
      // otherwise create an empty playlist
      mPlaylist = new Playlist(YOUTUBE_PLAYLIST);
      // populate an empty UI
      initAdapter(mPlaylist);
      // and start fetching the playlist contents
      new GetPlaylistAsyncTask(mYouTubeDataApi) {
        @Override public void onPostExecute(Pair<String, List<Video>> result) {
          handleGetPlaylistResult(mPlaylist, result);
        }
      }.execute(mPlaylist.playlistId, mPlaylist.getNextPageToken());
    }
  }

  private void initAdapter(final Playlist playlist) {
    // create the adapter with our playlist and a callback to handle when we reached the last item
    mAdapter = new PlaylistCardAdapter(playlist, new LastItemReachedListener() {
      @Override public void onLastItem(int position, String nextPageToken) {
        new GetPlaylistAsyncTask(mYouTubeDataApi) {
          @Override public void onPostExecute(Pair<String, List<Video>> result) {
            handleGetPlaylistResult(playlist, result);
          }
        }.execute(playlist.playlistId, playlist.getNextPageToken());
      }
    });
    mRecyclerView.setAdapter(mAdapter);
  }

  private void handleGetPlaylistResult(Playlist playlist, Pair<String, List<Video>> result) {
    if (result == null) return;
    final int positionStart = playlist.size();
    playlist.setNextPageToken(result.first);
    playlist.addAll(result.second);
    mAdapter.notifyItemRangeInserted(positionStart, result.second.size());
  }

  /**
   * Interface used by the {@link PlaylistCardAdapter} to inform us that we reached the last item
   * in
   * the list.
   */
  public interface LastItemReachedListener {
    void onLastItem(int position, String nextPageToken);
  }

  static class ViewHolder extends ToroAdapter.ViewHolder {

    public final Context mContext;
    @Bind(R.id.video_title) TextView mTitleText;
    @Bind(R.id.video_description) TextView mDescriptionText;
    @Bind(R.id.video_thumbnail) ImageView mThumbnailImage;
    @Bind(R.id.video_share) ImageView mShareIcon;
    @Bind(R.id.video_share_text) TextView mShareText;
    @Bind(R.id.video_dutation_text) TextView mDurationText;
    @Bind(R.id.video_view_count) TextView mViewCountText;
    @Bind(R.id.video_like_count) TextView mLikeCountText;
    @Bind(R.id.video_dislike_count) TextView mDislikeCountText;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      mContext = itemView.getContext();
    }

    @Override public void bind(@Nullable Object object) {
      CardItem item = (CardItem) object;
      mTitleText.setText(item.videoSnippet.getTitle());
      mDescriptionText.setText(item.videoSnippet.getDescription());

      // load the video thumbnail image
      Picasso.with(mContext)
          .load(item.videoSnippet.getThumbnails().getHigh().getUrl())
          .placeholder(R.drawable.toro_place_holder)
          .into(mThumbnailImage);

      // set the video duration text
      mDurationText.setText(parseDuration(item.videoContentDetails.getDuration()));
      // set the video statistics
      mViewCountText.setText(sFormatter.format(item.videoStatistics.getViewCount()));
      mLikeCountText.setText(sFormatter.format(item.videoStatistics.getLikeCount()));
      mDislikeCountText.setText(sFormatter.format(item.videoStatistics.getDislikeCount()));
    }
  }

  private static class PlaylistCardAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final Playlist mPlaylist;
    private final LastItemReachedListener mListener;

    public PlaylistCardAdapter(Playlist playlist, LastItemReachedListener listener) {
      this.mPlaylist = playlist;
      this.mListener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      // inflate a card layout
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.youtube_video_card, parent, false);
      // populate the viewholder
      return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, final int position) {
      final Video video = mPlaylist.get(position);
      final VideoSnippet videoSnippet = video.getSnippet();
      final VideoContentDetails videoContentDetails = video.getContentDetails();
      final VideoStatistics videoStatistics = video.getStatistics();
      holder.bind(new CardItem(video, videoSnippet, videoContentDetails, videoStatistics));
      if (mListener != null) {
        // get the next playlist page if we're at the end of the current page and we have another page to get
        final String nextPageToken = mPlaylist.getNextPageToken();
        if (!isEmpty(nextPageToken) && position == mPlaylist.size() - 1) {
          holder.itemView.post(new Runnable() {
            @Override public void run() {
              mListener.onLastItem(position, nextPageToken);
            }
          });
        }
      }
    }

    private boolean isEmpty(String s) {
      if (s == null || s.length() == 0) {
        return true;
      }
      return false;
    }

    @Override public int getItemCount() {
      return mPlaylist.size();
    }
  }

  private static class CardItem {
    final Video video;
    final VideoSnippet videoSnippet;
    final VideoContentDetails videoContentDetails;
    final VideoStatistics videoStatistics;

    public CardItem(Video video, VideoSnippet videoSnippet, VideoContentDetails videoContentDetails,
        VideoStatistics videoStatistics) {
      this.video = video;
      this.videoSnippet = videoSnippet;
      this.videoContentDetails = videoContentDetails;
      this.videoStatistics = videoStatistics;
    }
  }

  public static class Playlist extends ArrayList<Video> {
    public final String playlistId;
    private String mNextPageToken;

    public Playlist(String id) {
      playlistId = id;
    }

    public String getNextPageToken() {
      return mNextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
      mNextPageToken = nextPageToken;
    }
  }
}
