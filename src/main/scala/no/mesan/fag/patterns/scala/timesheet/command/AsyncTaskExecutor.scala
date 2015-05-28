package no.mesan.fag.patterns.scala.timesheet.command

import java.util.logging.Logger
import scala.util.Random

/** Kjører kommandoer på oppfordring. */
class AsyncTaskExecutor {
  private val Log = Logger.getLogger(classOf[AsyncTaskExecutor].getName)
  private val random= Random

  /**
   * Kjør et antall oppgaver.
   * @param tasks oppgaveliste
   */
  def executeTasks(tasks: AsyncTask*) = for (task <- tasks)
    new Thread(new AsyncTaskThreadRunner(task)).start

  /** Her skjer selve eksekveringen. */
  private class AsyncTaskThreadRunner(task: AsyncTask) extends Runnable {
    override def run {
      Log.info("Running background task " + task.whoAmI + "...")
      Thread.sleep(random.nextInt(2000) + 500)
      task.executeTask
      Log.info("Finished background task " + task.whoAmI)
    }
  }
}
