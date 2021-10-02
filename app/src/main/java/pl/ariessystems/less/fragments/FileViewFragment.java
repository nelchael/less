package pl.ariessystems.less.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.ariessystems.less.MainActivity;
import pl.ariessystems.less.R;
import pl.ariessystems.less.adapters.NumberedAdapter;
import pl.ariessystems.less.adapters.SimpleAdapter;

public class FileViewFragment extends Fragment {
	private long lastClickTime = 0;
	private List<String> text = new ArrayList<>();

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView textView = (TextView)inflater.inflate(R.layout.list_item_simple, container, false);
		float defaultTextSize = textView.getTextSize();

		boolean showLineNumbers = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(MainActivity.PREF_LINE_NUMBERS, false);

		ListView listView = (ListView)inflater.inflate(R.layout.fragment_file_view, container, false);
		if (showLineNumbers) {
			listView.setAdapter(new NumberedAdapter(getActivity(), text, inflater, defaultTextSize));
		} else {
			listView.setAdapter(new SimpleAdapter(getActivity(), text, inflater, defaultTextSize));
		}

		listView.setOnItemClickListener((parent, view, position, id) -> {
			if (System.currentTimeMillis() >= lastClickTime + 1000) {
				lastClickTime = System.currentTimeMillis();
				return;
			}
			lastClickTime = 0;

			MainActivity mainActivity = (MainActivity)getActivity();
			if (mainActivity != null) {
				mainActivity.toggleFullscreen();
			}
		});

		return listView;
	}

	public void setText(List<String> text) {
		this.text = text;
	}
}
