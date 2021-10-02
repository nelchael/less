package pl.ariessystems.less.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.widget.ListAdapter;

import java.util.List;

abstract class BaseAdapter implements ListAdapter {
	final Context context;
	final List<String> text;
	final LayoutInflater inflater;
	final float defaultTextSize;

	BaseAdapter(Context context, List<String> text, LayoutInflater inflater, float defaultTextSize) {
		this.context = context;
		this.text = text;
		this.inflater = inflater;
		this.defaultTextSize = defaultTextSize;
	}

	public final boolean areAllItemsEnabled() {
		return true;
	}

	public final boolean isEnabled(int position) {
		return true;
	}

	public final void registerDataSetObserver(DataSetObserver observer) { }

	public final void unregisterDataSetObserver(DataSetObserver observer) { }

	public final int getCount() {
		return text.size();
	}

	public final Object getItem(int position) {
		return text.get(position);
	}

	public final long getItemId(int position) {
		return 0;
	}

	public final boolean hasStableIds() {
		return false;
	}

	public final int getItemViewType(int position) {
		return 1;
	}

	public final int getViewTypeCount() {
		return 1;
	}

	public final boolean isEmpty() {
		return text.isEmpty();
	}
}
