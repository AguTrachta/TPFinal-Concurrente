package pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * PoolManager encapsulates the management of a thread pool.
 * It is responsible for task submission and pool shutdown.
 */
public class PoolManager {
  private final ExecutorService executorService;

  /**
   * Initializes the thread pool with the specified maximum number of threads and
   * a custom ThreadFactory.
   *
   * @param maxThreads    the maximum number of concurrent threads allowed.
   * @param threadFactory the custom ThreadFactory to use for creating threads.
   */
  public PoolManager(int maxThreads, MyThreadFactory threadFactory) {
    executorService = Executors.newFixedThreadPool(maxThreads, threadFactory);
  }

  /**
   * Submits a Runnable task to the thread pool.
   *
   * @param task the Runnable task to execute.
   */
  public void submitTask(Runnable task) {
    executorService.submit(task);
  }

  /**
   * Gracefully shuts down the thread pool, waiting for tasks to complete.
   */
  public void shutdown() {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Immediately shuts down the thread pool by cancelling running tasks.
   */
  public void shutdownNow() {
    executorService.shutdownNow();
  }
}

