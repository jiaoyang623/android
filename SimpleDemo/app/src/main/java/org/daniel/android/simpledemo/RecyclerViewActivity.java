package org.daniel.android.simpledemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jiaoyang on 2016/2/29.
 */
public class RecyclerViewActivity extends AppCompatActivity {
	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recycler_view);
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(new RVAdapter());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
	}

	private class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, null);
			return new ViewHolder(v);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.title.setText("No. " + position);
		}

		@Override
		public int getItemCount() {
			return 50;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			public TextView title;
			public ImageView image;

			public ViewHolder(View itemView) {
				super(itemView);
				title = (TextView) itemView.findViewById(R.id.title);
				image = (ImageView) itemView.findViewById(R.id.image);
			}
		}
	}

	private class MyLayoutManager extends RecyclerView.LayoutManager{

		@Override
		public RecyclerView.LayoutParams generateDefaultLayoutParams() {
			return null;
		}
	}

}
