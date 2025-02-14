package pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom ThreadFactory to create threads with a specific naming convention
 * and track the maximum number of concurrently active threads.
 */
public class MyThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger threadCount = new AtomicInteger(0);
    // Contador de hilos actualmente activos.
    private final AtomicInteger currentActiveThreads = new AtomicInteger(0);
    // Valor máximo de hilos activos alcanzado en algún momento.
    private final AtomicInteger maxActiveThreads = new AtomicInteger(0);

    /**
     * Constructs a MyThreadFactory with the specified base name for threads.
     *
     * @param baseName the base name for threads created by this factory.
     */
    public MyThreadFactory(String baseName) {
        this.baseName = baseName;
    }

    /**
     * Creates a new thread with a custom name and wraps the Runnable to track
     * the cantidad de hilos activos.
     *
     * @param r the Runnable task to execute.
     * @return a new Thread instance with a custom name.
     */
    @Override
    public Thread newThread(Runnable r) {
        // Envolver el runnable para hacer tracking de hilos activos.
        Runnable wrappedRunnable = () -> {
            // Incrementamos el contador al iniciar.
            int active = currentActiveThreads.incrementAndGet();
            // Actualizamos el máximo si es necesario.
            maxActiveThreads.updateAndGet(max -> Math.max(max, active));
            try {
                r.run();
            } finally {
                // Decrementamos el contador al terminar.
                currentActiveThreads.decrementAndGet();
            }
        };

        Thread thread = new Thread(wrappedRunnable);
        thread.setName(baseName + "-" + threadCount.incrementAndGet());
        return thread;
    }

    /**
     * Retorna la cantidad máxima de hilos que estuvieron activos simultáneamente.
     *
     * @return el máximo de hilos activos.
     */
    public int getMaxActiveThreads() {
        return maxActiveThreads.get();
    }
}
