package org.daniel.android.simpledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by jiaoyang on 2016/3/15.
 */
public class CoverFlowActivity extends AppCompatActivity {
	private ListView mList;
	private CoverFlowAdapter mAdapter;
	private static final int[] COLOR_ARRAY = {
			0x66aaff11, 0x66ffaa11, 0x6611aaff, 0x66ff11aa, 0x6611ffaa, 0x66aa11ff};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coverflow);
		mList = (ListView) findViewById(R.id.list);
		mAdapter = new CoverFlowAdapter();
		mList.setAdapter(mAdapter);
	}

	private class CoverFlowAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 60;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView img = (ImageView) convertView;
			if (img == null) {
				img = new ImageView(parent.getContext());
			}

			img.setBackgroundColor(COLOR_ARRAY[position % COLOR_ARRAY.length]);

			if (position % 2 == 0) {
				img.setPadding(20, -40, 20, -40);
				img.setImageResource(R.mipmap.ic_launcher);
			} else {
				img.setPadding(0, 0, 0, 0);
				img.setImageResource(R.mipmap.ic_launcher);
			}

			return img;
		}
	}
}
