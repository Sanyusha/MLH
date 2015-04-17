package android.mlh.ui;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
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
	
	/**
	 * Transforms dp to px in specific context.
	 */
	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	
	/**
	 * Returns the screen width in pixels
	 */
	public static float getDisplayWidth(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics();
	    display.getMetrics(outMetrics);

	    return outMetrics.widthPixels;
	}
	
	/**
	 * Returns the screen height in pixels
	 */
	public static float getDisplayHeight(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics();
	    display.getMetrics(outMetrics);

	    return outMetrics.heightPixels;
	}
	
	/**
     * Round to certain number of decimals
     * 
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
