package pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PoolManager encapsulates the management of a thread pool.
 * It is responsible for task submission and pool shutdown.
 * Additionally, it allows measuring actual concurrency (maximum number of tasks running simultaneously).
 */
public class PoolManager {
  private final ExecutorService executorService;
  
  // Counter for currently running tasks.
  private final AtomicInteger currentRunningTasks = new AtomicInteger(0);
  // Maximum number of tasks running simultaneously.
  private final AtomicInteger maxConcurrentTasks = new AtomicInteger(0);

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
   * The task is wrapped to measure the number of tasks running in parallel.
   *
   * @param task the Runnable task to execute.
   */
  public void submitTask(Runnable task) {
    executorService.submit(() -> {
      // Increment the counter of running tasks.
      int running = currentRunningTasks.incrementAndGet();
      // Update the maximum if necessary.
      maxConcurrentTasks.updateAndGet(max -> Math.max(max, running));
      try {
        task.run();
      } finally {
        // Decrement the counter when the task finishes.
        currentRunningTasks.decrementAndGet();
      }
    });
  }

  /**
   * Returns the maximum number of tasks that ran simultaneously.
   *
   * @return the maximum number of concurrent tasks.
   */
  public int getMaxConcurrentTasks() {
    return maxConcurrentTasks.get();
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
   * Immediately shuts down the thread pool by canceling running tasks.
   */
  public void shutdownNow() {
    executorService.shutdownNow();
  }
}
