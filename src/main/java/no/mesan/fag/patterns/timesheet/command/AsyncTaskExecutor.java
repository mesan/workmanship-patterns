package no.mesan.fag.patterns.timesheet.command;

import java.util.Random;
import java.util.logging.Logger;

/** Kjører kommandoer på oppfordring. */
public final class AsyncTaskExecutor {

    private static final Logger LOG = Logger.getLogger(AsyncTaskExecutor.class.getName());
    private static final Random random = new Random();

    /**
     * Kjør et antall oppgaver.
     * @param tasks
     */
	public void executeTasks(final AsyncTask ... tasks) {
        for (AsyncTask task : tasks) {
            final Thread thread = new Thread(new AsyncTaskThreadRunner(task));
    		thread.start();
        }
	}

    /** Her skjer selve eksekveringen. */
	private class AsyncTaskThreadRunner implements Runnable {

		private final AsyncTask task;

		AsyncTaskThreadRunner(final AsyncTask task) {
			this.task = task;
		}

		@Override
		public void run() {
			LOG.info("Running background task " + task.whoAmI() + "...");
			try {
				Thread.sleep(random.nextInt(2000)+500);
			}
            catch (final InterruptedException e) {
				e.printStackTrace(); // Ikke si det til noen
			}
            task.executeTask();
			LOG.info("Finished background task " + task.whoAmI());
		}
	}
}
