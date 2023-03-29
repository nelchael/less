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

public class SimpleAdapter extends BaseAdapter {
    public SimpleAdapter(Context context, List<String> text, LayoutInflater inflater, float defaultTextSize) {
        super(context, text, inflater, defaultTextSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_simple, parent, false);
        }

        TextView textView = (TextView) convertView;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.PREF_FIXED_WIDTH, false)) {
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize * 0.9f);
        } else {
            textView.setTypeface(Typeface.DEFAULT);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize);
        }

        textView.setText(text.get(position));
        return textView;
    }
}
