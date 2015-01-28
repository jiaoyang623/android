package org.daniel.android.workbanch.util;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.util.SparseArray;

/**
 * 可以以集合方式撤销执行的线程池
 * 
 * @author jiaoyang<br>
 *         email: yangjiao623@gmail.com
 * @date 2013-8-22 下午4:37:15
 * @version 1.0
 */
public class ThreadPool {
	private static final int POOL_SIZE = 5;
	private ExecutorService mExecutorService;
	private SparseArray<LinkedList<Future<?>>> mTaskMap;
	private static ThreadPool INSTANCE = null;

	public static synchronized ThreadPool getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ThreadPool();
		}

		return INSTANCE;
	}

	private ThreadPool() {
		mExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
		mTaskMap = new SparseArray<LinkedList<Future<?>>>();
	}

	/**
	 * 向线程池中提交任务
	 * 
	 * @param taskSetId
	 *            任务所属任务集的id，用这个id进行撤销。一般这个id写成activity实例的hashcode的
	 * */
	public void submit(int taskSetId, Runnable task) {
		Future<?> future = mExecutorService.submit(task);
		LinkedList<Future<?>> list = mTaskMap.get(taskSetId);
		if (list == null) {
			list = new LinkedList<Future<?>>();
			mTaskMap.put(taskSetId, list);
		}
		list.add(future);
	}

	/**
	 * 删除一个ID下的所有队列中的任务，同时给正在执行的线程发送Interrupt信号
	 * 
	 * @param taskSetId
	 *            任务集的ID
	 * */
	public void cancelTaskSet(int taskSetId) {
		LinkedList<Future<?>> taskList = mTaskMap.get(taskSetId);
		if (taskList != null) {
			while (taskList.size() > 0) {
				taskList.remove().cancel(true);
			}

			mTaskMap.remove(taskSetId);
		}
	}

}
