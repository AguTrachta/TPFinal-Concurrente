package pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PoolManager encapsulates the management of a thread pool.
 * It is responsible for task submission and pool shutdown.
 * Además, permite medir la concurrencia real (máximo de tareas ejecutándose simultáneamente).
 */
public class PoolManager {
  private final ExecutorService executorService;
  
  // Contador de tareas en ejecución actualmente.
  private final AtomicInteger currentRunningTasks = new AtomicInteger(0);
  // Valor máximo de tareas ejecutándose simultáneamente.
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
   * Se envuelve la tarea para medir la cantidad de tareas ejecutándose en paralelo.
   *
   * @param task the Runnable task to execute.
   */
  public void submitTask(Runnable task) {
    executorService.submit(() -> {
      // Incrementamos el contador de tareas en ejecución.
      int running = currentRunningTasks.incrementAndGet();
      // Actualizamos el máximo si corresponde.
      maxConcurrentTasks.updateAndGet(max -> Math.max(max, running));
      try {
        task.run();
      } finally {
        // Al finalizar la tarea, decrementamos el contador.
        currentRunningTasks.decrementAndGet();
      }
    });
  }

  /**
   * Retorna el número máximo de tareas que se ejecutaron simultáneamente.
   *
   * @return el máximo de tareas concurrentes.
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
   * Immediately shuts down the thread pool by cancelling running tasks.
   */
  public void shutdownNow() {
    executorService.shutdownNow();
  }
}
