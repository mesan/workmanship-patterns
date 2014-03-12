package no.mesan.fag.patterns.timesheet.command;

import java.util.logging.Logger;


public final class AsyncTaskExecutor {

    private static final Logger LOG = Logger.getLogger(AsyncTaskExecutor.class.getName());

	public void executeTask(final AsyncTask task) {
		Thread thread = new Thread(new AsyncTaskThreadRunner(task));
		thread.start();
	}

	private class AsyncTaskThreadRunner implements Runnable {

		private final AsyncTask task;

		public AsyncTaskThreadRunner(final AsyncTask task) {
			this.task = task;
		}

		@Override
		public void run() {
			LOG.info("Running background task..");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			task.executeTask();
			LOG.info("Finished background task");
		}
	}
}
