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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.lab.toro.Toro;
import im.ene.lab.toro.sample.R;
import im.ene.lab.toro.sample.adapter.SampleListViewAdapter;
import im.ene.lab.toro.widget.ToroListView;

/**
 * Created by eneim on 2/1/16.
 */
public class ListViewFragment extends Fragment {

  public static ListViewFragment newInstance() {
    Bundle args = new Bundle();
    ListViewFragment fragment = new ListViewFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.generic_list_view, container, false);
  }

  private static final String TAG = "ListViewFragment";

  private ToroListView mListView;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mListView = (ToroListView) view.findViewById(R.id.list_view);
    SampleListViewAdapter adapter = new SampleListViewAdapter();
    mListView.setAdapter(adapter);

    Toro.register(mListView);
  }

  @Override public void onDestroyView() {
    Toro.unregister(mListView);
    super.onDestroyView();
  }
}
