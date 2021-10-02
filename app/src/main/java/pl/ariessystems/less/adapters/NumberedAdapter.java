package pl.ariessystems.less.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.ariessystems.less.MainActivity;
import pl.ariessystems.less.R;

public class NumberedAdapter extends BaseAdapter {
	private static final int OFFSET = 10;
	private final int lineNumberWidth;

	public NumberedAdapter(Context context, List<String> text, LayoutInflater inflater, float defaultTextSize) {
		super(context, text, inflater, defaultTextSize);

		int dps = OFFSET + 8 * String.valueOf(text.size()).length();
		this.lineNumberWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, context.getResources().getDisplayMetrics()));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_numbered, parent, false);
		}

		TextView lineContent = convertView.findViewById(R.id.line_content);
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.PREF_FIXED_WIDTH, false)) {
			lineContent.setTypeface(Typeface.MONOSPACE);
			lineContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize * 0.9f);
		} else {
			lineContent.setTypeface(Typeface.DEFAULT);
			lineContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize);
		}
		lineContent.setText(text.get(position));

		TextView lineNumber = convertView.findViewById(R.id.line_number);
		ViewGroup.LayoutParams layoutParams = lineNumber.getLayoutParams();
		layoutParams.width = lineNumberWidth;
		lineNumber.setLayoutParams(layoutParams);
		lineNumber.setText(String.valueOf(position + 1));

		return convertView;
	}
}
