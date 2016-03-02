package org.daniel.android.simpledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;

/**
 * Created by jiaoyang on 2016/2/18.
 */
public class MinePlayerActivity extends AppCompatActivity implements View.OnClickListener, ExoPlayer.Listener {
	private ExoPlayer mPlayer;
	private PlayerControl mControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		findViewById(R.id.back).setOnClickListener(this);
		mPlayer = ExoPlayer.Factory.newInstance(2, 1000, 5000);
		mPlayer.addListener(this);
		mControl = new PlayerControl(mPlayer);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
		}
	}

	@Override
	public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

	}

	@Override
	public void onPlayWhenReadyCommitted() {

	}

	@Override
	public void onPlayerError(ExoPlaybackException error) {

	}
}
