package org.daniel.android.simpledemo;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by jiaoyang on 2016/3/23.
 */
public class CpuInfoActivity extends AppCompatActivity {
	private TextView mText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cpu_info);
		mText = (TextView) findViewById(R.id.text);

		mText.setText(getValues());

	}

	private String getValues() {
		StringBuilder builder = new StringBuilder();
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			builder.append(field.getName()).append(" = ");
			try {
				Object data = field.get(null);
				if (data == null) {
					continue;
				}
				if (data instanceof String[]) {
					builder.append(Arrays.toString((String[]) data));
				} else {
					builder.append(data);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			builder.append('\n');
		}

		return builder.toString();
	}
}
