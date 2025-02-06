package petrinet;

import monitor.MonitorInterface;
import java.util.List;
import utils.Logger;

/**
 * Represents a segment of the Petri net execution.
 * Each segment is responsible for a subset of transitions and operates as a
 * Runnable.
 */
public class Segment implements Runnable {
    private final String segmentName;
    private final List<Transition> transitions;
    private final MonitorInterface monitor;
    private final Places places;
    private volatile boolean running = true;

    // Logger instance for tracing events in Segment
    private static final Logger logger = Logger.getInstance();

    /**
     * Constructs a Segment.
     *
     * @param segmentName a name/identifier for the segment (e.g., "Segment A")
     * @param transitions the list of transitions that this segment will handle
     * @param monitor     the shared MonitorInterface to fire transitions
     * @param places      the shared Places object representing the state of the
     *                    Petri net
     */
    public Segment(String segmentName, List<Transition> transitions, MonitorInterface monitor, Places places) {
        this.segmentName = segmentName;
        this.transitions = transitions;
        this.monitor = monitor;
        this.places = places;
        logger.info(segmentName + " initialized with " + transitions.size() + " transitions.");
    }

    @Override
    public void run() {
        logger.info(segmentName + " started running.");
        while (running) {
            boolean firedAnyTransition = false;
            // Iterate through the transitions managed by this segment
            for (Transition transition : transitions) {
                // Check if the transition is enabled. We avoid logging every check.
                if (transition.isEnabled(places)) {
                    // Only log when a transition is attempted to be fired.
                    boolean fired = monitor.fireTransition(transition.getId());
                    if (fired) {
                        firedAnyTransition = true;
                        logger.info(segmentName + " fired transition: " + transition.getId());
                    }
                }
            }
            // If no transition was fired, pause briefly to avoid busy waiting.
            if (!firedAnyTransition) {
                try {
                    Thread.sleep(10); // Sleep for 10 milliseconds
                } catch (InterruptedException e) {
                    logger.warn(segmentName + " interrupted while sleeping.");
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
        logger.info(segmentName + " stopped running.");
    }

    /**
     * Stops the segment's execution loop.
     */
    public void stop() {
        running = false;
        logger.info(segmentName + " stop signal received.");
    }

    public String getSegmentName() {
        return segmentName;
    }
}

