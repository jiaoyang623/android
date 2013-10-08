package org.daniel.android.workbanch.model;

import java.util.List;

import android.os.Handler;
import android.os.Looper;

/**
 * 数据提供类，实现了非阻塞数据操作，ui线程调用，ui线程返回。
 * 
 * @author jiaoyang<br>
 *         email: yangjiao623@gmail.com
 * @date Sep 29, 2013 6:48:50 PM
 * @version 1.0
 */
public abstract class AbsDataProvider<T> {
	public static final int STATUS_OK = 0;

	private static final int THREAD_POOL_SIZE = 2;
	private static int mInstanceCount = 0;
	private static boolean mIsWorkerHandlersReady = false;
	private final int mInstanceId;
	private static Handler[] mWorkerHandlers = new Handler[THREAD_POOL_SIZE];

	private Handler mHandler = new Handler(Looper.getMainLooper());
	private List<T> mDataList;

	private static final int INVALID_POSITION = -1;

	protected abstract void createItem(T item);

	protected abstract void removeItem(T item);

	protected abstract void updateItem(T item);

	protected abstract List<T> queryItems();

	static {
		for (int i = 0; i < THREAD_POOL_SIZE; i++) {
			final int index = i;
			new Thread() {
				public void run() {
					Looper.prepare();
					mWorkerHandlers[index] = new Handler();
					// 检测初始化完成
					synchronized (AbsDataProvider.class) {
						boolean isReady = true;
						for (Handler handler : mWorkerHandlers) {
							if (handler == null) {
								isReady = false;
								break;
							}
						}
						mIsWorkerHandlersReady = isReady;
					}

					Looper.loop();
				};
			}.start();
		}
	}

	public AbsDataProvider() {
		while (!mIsWorkerHandlersReady) {
			// 等待handlers初始化完成之后再创建实例
			Thread.yield();
		}

		mInstanceId = mInstanceCount;
		mInstanceCount++;
	}

	private void postTask(Runnable runnable) {
		mWorkerHandlers[mInstanceId % THREAD_POOL_SIZE].post(runnable);
	}

	public void create(final List<T> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		postTask(new Runnable() {
			@Override
			public void run() {
				ensureCache();

				for (T t : list) {
					if (!mDataList.contains(t)) {
						// add item
						mDataList.add(t);
						createItem(t);
					}
				}
			}
		});
	}

	public void remove(final List<T> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		postTask(new Runnable() {

			@Override
			public void run() {
				ensureCache();

				for (T t : list) {
					int pos = mDataList.indexOf(t);
					if (pos != INVALID_POSITION) {
						mDataList.remove(pos);
						removeItem(t);
					}
				}
			}
		});
	}

	public void update(final List<T> list) {
		if (list == null || list.size() == 0) {
			return;
		}

		postTask(new Runnable() {

			@Override
			public void run() {
				ensureCache();

				for (T t : list) {
					int pos = mDataList.indexOf(t);
					if (pos != INVALID_POSITION) {
						mDataList.set(pos, t);
						updateItem(t);
					}
				}
			}
		});
	}

	public void query(final DataCallBack<T> callback, final int id) {

		if (callback == null) {
			return;
		}

		postTask(new Runnable() {

			@Override
			public void run() {
				ensureCache();
				callback(callback, id, STATUS_OK);
			}
		});
	}

	private void ensureCache() {
		if (mDataList == null) {
			mDataList = queryItems();
		}
	}

	private void callback(final DataCallBack<T> callback, final int id, final int status) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				callback.onCallback(mDataList, id, status);
			}
		});
	}

	public interface DataCallBack<T> {
		void onCallback(List<T> list, int id, int STATUS);
	}
}
