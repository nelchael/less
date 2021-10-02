package pl.ariessystems.less;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import pl.ariessystems.less.fragments.EmptyListFragment;
import pl.ariessystems.less.fragments.FileViewFragment;
import pl.ariessystems.less.fragments.LoadingFragment;

public class MainActivity extends AppCompatActivity {
	public static final String PREF_FIXED_WIDTH = "fixedWidth";
	public static final String PREF_LINE_NUMBERS = "lineNumbers";
	private static final int FILE_CHOOSER_REQUEST_CODE = 101;
	private static final String SAVE_FILE_KEY = "loadedFileUri";
	private static final String TAG = MainActivity.class.getName();
	private boolean fullScreen = false;
	private Uri loadedFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_FILE_KEY)) {
			loadedFile = savedInstanceState.getParcelable(SAVE_FILE_KEY);
		}

		Intent intent = getIntent();
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW) && intent.getData() != null) {
			loadedFile = intent.getData();
		}
		loadFile(loadedFile);
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		if (loadedFile != null) {
			outState.putParcelable(SAVE_FILE_KEY, loadedFile);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.use_fixed_width_font).setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_FIXED_WIDTH, false));
		menu.findItem(R.id.show_line_numbers).setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_LINE_NUMBERS, false));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (fullScreen) {
			toggleFullscreen();
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("CodeBlock2Expr")
	private void loadFile(@Nullable Uri uri) {
		Log.i(TAG, "Loading: " + uri);
		if (uri == null) {
			String version = "unknown";
			try {
				PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
				version = pi.versionName;
			} catch (PackageManager.NameNotFoundException e) {
				Log.w(TAG, "Failed to get package info", e);
			}

			FileViewFragment welcomeFragment = new FileViewFragment();
			List<String> welcomeText = Arrays.asList(getString(R.string.no_content, version).split("\n"));
			welcomeFragment.setText(welcomeText);
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment, welcomeFragment).commitAllowingStateLoss();
			return;
		}

		InputStream inputStream = null;
		try {
			inputStream = getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException for " + uri);
			Toast.makeText(MainActivity.this, getString(R.string.failed_to_read, uri.toString()), Toast.LENGTH_LONG).show();
		}

		getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new LoadingFragment()).commitAllowingStateLoss();
		new BackgroundLoader().setOnLoadCompleteListener(loadedContent -> {
			runOnUiThread(() -> {
				loadedFile = uri;
				if (loadedContent.isEmpty()) {
					MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new EmptyListFragment()).commit();
				} else {
					Toast.makeText(MainActivity.this, getResources().getQuantityString(R.plurals.loaded_lines, loadedContent.size(),
						loadedContent.size()), Toast.LENGTH_LONG).show();

					FileViewFragment fileViewFragment = new FileViewFragment();
					fileViewFragment.setText(loadedContent);
					MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fileViewFragment).commit();
				}

				ActionBar actionBar = getSupportActionBar();
				if (actionBar != null) {
					actionBar.setSubtitle(uri.getPath());
				}
			});
		}).setOnLoadErrorListener(throwable -> {
			runOnUiThread(() -> Toast.makeText(MainActivity.this, getString(R.string.failed_to_read, uri.toString()), Toast.LENGTH_LONG).show());
		}).execute(inputStream);
	}

	public void toggleFullscreen() {
		ActionBar actionBar = getSupportActionBar();
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		if (fullScreen) {
			layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
			if (actionBar != null) {
				actionBar.show();
			}
		} else {
			layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			if (actionBar != null) {
				actionBar.hide();
			}
		}
		getWindow().setAttributes(layoutParams);
		fullScreen = !fullScreen;
	}

	public void onActionOpen(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == FILE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			if (data != null) {
				loadFile(data.getData());
			}
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onFixedFontClick(MenuItem item) {
		boolean useFixedWidthFont = !item.isChecked();
		item.setChecked(useFixedWidthFont);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(PREF_FIXED_WIDTH, useFixedWidthFont);
		editor.apply();

		loadFile(loadedFile);
	}

	public void onLineNumbersClick(MenuItem item) {
		boolean showLineNumbers = !item.isChecked();
		item.setChecked(showLineNumbers);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(PREF_LINE_NUMBERS, showLineNumbers);
		editor.apply();

		loadFile(loadedFile);
	}
}
