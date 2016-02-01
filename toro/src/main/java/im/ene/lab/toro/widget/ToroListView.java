package im.ene.lab.toro.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by eneim on 1/31/16.
 * <p/>
 * A ListView, with fixed OnScrollListener
 */
public class ToroListView extends ListView {

  private final ListViewScrollHelper mHelper;

  public ToroListView(Context context) {
    this(context, null);
  }

  public ToroListView(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.listViewStyle);
  }

  public ToroListView(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ToroListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
