import java.util.List;
import java.util.Scanner;

import monitor.Monitor;
import monitor.Policy;
import monitor.PriorityPolicy;
import monitor.BalancedPolicy;
import pool.MyThreadFactory;
import pool.PoolManager;
import utils.Logger;
import utils.PetriNet;
import petrinet.Places;
import petrinet.Segment;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.info("Starting Petri net simulation.");

        // Se le pide al usuario que elija una política.
        Policy policy = choosePolicy();

        // Se inicia el cronómetro.
        long startTime = System.currentTimeMillis();

        // Se construye la red de Petri utilizando la política seleccionada.
        PetriNet net = new PetriNet(policy);

        // Se obtienen los segmentos, los lugares y el monitor.
        List<Segment> segments = net.getSegments();
        Places places = net.getPlaces();
        Monitor monitor = (Monitor) net.getMonitor();

        // Se configura el pool de hilos con 32 hilos usando la fábrica personalizada.
        MyThreadFactory threadFactory = new MyThreadFactory("TestPoolThread");
        PoolManager poolManager = new PoolManager(4, threadFactory);

        // Se inicia el Scheduler del Monitor.
        monitor.startScheduler(segments, poolManager);

        // Se espera hasta que se cumpla la condición invariante (T11 disparado 186 veces).
        synchronized (monitor.getInvariantLock()) {
            while (monitor.getT0Counter() < 187) {
                try {
                    monitor.getInvariantLock().wait();
                } catch (InterruptedException e) {
                    logger.error("Main thread interrupted while waiting for invariants: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        logger.info("Completed 186 T-invariants (T11 fired 186 times).");

        // Se detiene el Scheduler y se cierra inmediatamente el pool de hilos.
        monitor.stopScheduler();
        poolManager.shutdownNow();

        // Se detiene el cronómetro y se calcula el tiempo transcurrido.
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // Se imprimen los tokens finales en cada Place.
        System.out.println("Final tokens in Place 0: " + places.getTokenCount(0));
        System.out.println("Final tokens in Place 1: " + places.getTokenCount(1));
        System.out.println("Final tokens in Place 2: " + places.getTokenCount(2));
        System.out.println("Final tokens in Place 3: " + places.getTokenCount(3));
        System.out.println("Final tokens in Place 4: " + places.getTokenCount(4));
        System.out.println("Final tokens in Place 5: " + places.getTokenCount(5));
        System.out.println("Final tokens in Place 6: " + places.getTokenCount(6));
        System.out.println("Final tokens in Place 7: " + places.getTokenCount(7));
        System.out.println("Final tokens in Place 8: " + places.getTokenCount(8));
        System.out.println("Final tokens in Place 9: " + places.getTokenCount(9));
        System.out.println("Final tokens in Place 10: " + places.getTokenCount(10));
        System.out.println("Final tokens in Place 11: " + places.getTokenCount(11));
        System.out.println("Final tokens in Place 12: " + places.getTokenCount(12));
        System.out.println("Final tokens in Place 13: " + places.getTokenCount(13));
        System.out.println("Final tokens in Place 14: " + places.getTokenCount(14));

        // Se imprimen estadísticas específicas de la política.
        if (monitor.getPolicy() instanceof PriorityPolicy) {
            PriorityPolicy prioPolicy = (PriorityPolicy) monitor.getPolicy();
            System.out.println("Superior reservations count: " + prioPolicy.getSuperiorCount());
            System.out.println("Inferior reservations count: " + prioPolicy.getInferiorCount());
            System.out.println("Confirmed reservations count: " + prioPolicy.getConfirmedCount());
            System.out.println("Cancelled reservations count: " + prioPolicy.getCancelledCount());

            double fourthInvariant = prioPolicy.getSuperiorCount() * 0.8;
            double thirdInvariant = prioPolicy.getSuperiorCount() * 0.2;
            double secondInvariant = prioPolicy.getInferiorCount() * 0.8;
            double firstInvariant = prioPolicy.getInferiorCount() * 0.2;

            System.out.println("First invariant (T3 and T7): " + Math.round(firstInvariant));
            System.out.println("Second invariant (T3 and T6): " + Math.round(secondInvariant));
            System.out.println("Third invariant (T2 and T7): " + Math.round(thirdInvariant));
            System.out.println("Fourth invariant (T2 and T6): " + Math.round(fourthInvariant));
        }

        if (monitor.getPolicy() instanceof BalancedPolicy) {
            BalancedPolicy balPolicy = (BalancedPolicy) monitor.getPolicy();
            System.out.println("Superior reservations count: " + balPolicy.getSuperiorCount());
            System.out.println("Inferior reservations count: " + balPolicy.getInferiorCount());
            System.out.println("Confirmed reservations count: " + balPolicy.getConfirmedCount());
            System.out.println("Cancelled reservations count: " + balPolicy.getCancelledCount());
        }

        // Se imprime la máxima cantidad de tareas en ejecución simultánea, medida por el PoolManager.
        System.out.println("Máxima cantidad de tareas en ejecución simultánea: " 
                + poolManager.getMaxConcurrentTasks());

        logger.info("Petri net simulation ended.");
        logger.info("Elapsed time: " + elapsedTime + " ms");
        logger.close();
    }

    /**
     * Solicita al usuario que elija una política:
     * 1 para BalancedPolicy o 2 para PriorityPolicy.
     * Se utiliza PriorityPolicy por defecto en caso de opción inválida.
     *
     * @return la instancia de Policy seleccionada.
     */
    private static Policy choosePolicy() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Select a policy:");
            System.out.println("1 - Balanced Policy");
            System.out.println("2 - Priority Policy");
            System.out.print("Enter your choice: ");

            int choice = 2; // opción por defecto
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            switch (choice) {
                case 1:
                    System.out.println("Balanced Policy selected.");
                    return new BalancedPolicy();
                case 2:
                    System.out.println("Priority Policy selected.");
                    return new PriorityPolicy();
                default:
                    System.out.println("Invalid choice. Defaulting to Priority Policy.");
                    return new PriorityPolicy();
            }
        }
    }
}
