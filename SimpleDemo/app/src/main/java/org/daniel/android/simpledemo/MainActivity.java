package org.daniel.android.simpledemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
	private MainAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView listView = new ListView(getApplicationContext());
		setContentView(listView);
		mAdapter = new MainAdapter();
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(mAdapter);
		String thisName = getComponentName().getClassName();

		try {
			PackageInfo info = getPackageManager().getPackageInfo(getComponentName().getPackageName(), PackageManager.GET_ACTIVITIES);
			for (ActivityInfo i : info.activities) {
				if (!thisName.equals(i.name)) {
					mAdapter.add(i.name);
				}
			}

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private class MainAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
		private List<String> mActivityList = new ArrayList<>();
		private final float TEXT_SIZE = getResources().getDimension(R.dimen.test_font_size);

		private final int PADDING = getResources().getDimensionPixelSize(R.dimen.test_padding);

		public void add(String activity) {

			if (!mActivityList.contains(activity)) {
				mActivityList.add(activity);
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mActivityList.size();
		}

		@Override
		public String getItem(int position) {
			return mActivityList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = (TextView) convertView;

			if (tv == null) {
				tv = new TextView(getApplicationContext());
				tv.setTextColor(0xff000000);
				tv.setTextSize(TEXT_SIZE);
				tv.setPadding(PADDING, 0, PADDING, 0);
			}

			String name = getItem(position);
			tv.setText(name.substring(name.lastIndexOf('.') + 1));

			return tv;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent();
			intent.setClassName(getApplicationContext(), getItem(position));
			startActivity(intent);
		}
	}

}