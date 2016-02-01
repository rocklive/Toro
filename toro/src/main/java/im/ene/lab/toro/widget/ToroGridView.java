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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by eneim on 2/1/16.
 */
public class ToroGridView extends GridView {

  private final ListViewScrollHelper mHelper;

  public ToroGridView(Context context) {
    this(context, null);
  }

  public ToroGridView(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.gridViewStyle);
  }

  public ToroGridView(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ToroGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    mHelper = new ListViewScrollHelper(this);
  }

  // Don't use this
  @CallSuper @Deprecated @Override public void setOnScrollListener(final OnScrollListener l) {
    super.setOnScrollListener(l);
  }

  @CallSuper public void addOnScrollListener(final OnScrollListener listener) {
    mHelper.addOnScrollListener(listener);
  }

  public void removeOnScrollListener(OnScrollListener listener) {
    mHelper.removeOnScrollListener(listener);
  }
}
