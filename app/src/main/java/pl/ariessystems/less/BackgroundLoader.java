package pl.ariessystems.less;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

class BackgroundLoader extends AsyncTask<InputStream, Void, Void> {
	private static final String TAG = BackgroundLoader.class.getName();
	private OnLoadCompleteListener onLoadCompleteListener = null;
	private OnLoadErrorListener onLoadErrorListener = null;

	BackgroundLoader setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
		this.onLoadCompleteListener = onLoadCompleteListener;
		return this;
	}

	BackgroundLoader setOnLoadErrorListener(OnLoadErrorListener onLoadErrorListener) {
		this.onLoadErrorListener = onLoadErrorListener;
		return this;
	}

	@Override
	protected Void doInBackground(InputStream... inputStreams) {
		if (inputStreams == null || inputStreams.length != 1) {
			throw new IllegalArgumentException();
		}

		readFile(inputStreams[0]);
		return null;
	}

	private void readFile(InputStream inputStream) {
		try {
			List<String> fileContent = new ArrayList<>();

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
			String line = reader.readLine();
			while (line != null) {
				fileContent.add(line);
				line = reader.readLine();
			}

			if (this.onLoadCompleteListener != null) {
				this.onLoadCompleteListener.loadComplete(fileContent);
			}
		} catch (IOException e) {
			Log.e(TAG, "Failed to load from stream " + inputStream, e);
			if (this.onLoadErrorListener != null) {
				this.onLoadErrorListener.handleError(e);
			}
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			Log.w(TAG, "Failed to close stream " + inputStream, e);
		}
	}

	public interface OnLoadCompleteListener {
		void loadComplete(List<String> loadedContent);
	}

	public interface OnLoadErrorListener {
		void handleError(Throwable throwable);
	}
}
