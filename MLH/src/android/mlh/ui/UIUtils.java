package android.mlh.ui;

import android.view.View;
import android.widget.ListView;

public class UIUtils {
	
	/**
	 * Returns the item view from the listview by its position.
	 * Explanation (I hope that it's related to the implementation 
	 * because I took the explanation from one place and the implementation from the other): 
	 * You can get only visible View from ListView because row views in ListView are reuseable.
	 * If you use mListView.getChildAt(0) you get first visible view. 
	 * This view is associated with item from adapter at position mListView.getFirstVisiblePosition().
	 */
	public static View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition ) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}
}
