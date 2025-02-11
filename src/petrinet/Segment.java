package petrinet;

import monitor.MonitorInterface;
import java.util.List;
import utils.Logger;

/**
 * Represents a segment of the Petri net execution.
 * Each segment is responsible for a subset of transitions and is scheduled
 * by a central scheduler to run when one of its transitions is enabled.
 */
public class Segment implements Runnable {
    private final String segmentName;
    private final List<Transition> transitions;
    private final MonitorInterface monitor;
    private final Places places;
    // Flag to prevent concurrent execution of the same segment.
    private volatile boolean isRunning = false;

    private static final Logger logger = Logger.getInstance();

    public Segment(String segmentName, List<Transition> transitions, MonitorInterface monitor, Places places) {
        this.segmentName = segmentName;
        this.transitions = transitions;
        this.monitor = monitor;
        this.places = places;
        logger.info(segmentName + " initialized with " + transitions.size() + " transitions.");
    }

    /**
     * Checks whether any transition in this segment is enabled.
     * 
     * @return true if at least one transition is enabled; false otherwise.
     */
    public boolean hasEnabledTransition() {
        for (Transition transition : transitions) {
            if (transition.isEnabled(places)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if this segment can be scheduled.
     * It is available for scheduling if it is not already running and it has at
     * least one enabled transition.
     * 
     * @return true if the segment can be scheduled; false otherwise.
     */
    public synchronized boolean canBeScheduled() {
        return !isRunning && hasEnabledTransition();
    }

    /**
     * Returns the name of this segment.
     */
    public String getSegmentName() {
        return segmentName;
    }

    /**
     * Executes one cycle: iterates over its transitions and attempts to fire any
     * enabled ones.
     */
    @Override
    public void run() {
        // Set the running flag to prevent concurrent execution.
        synchronized (this) {
            if (isRunning) {
                return;
            }
            isRunning = true;
        }
        // logger.info(segmentName + " execution started.");
        boolean firedAnyTransition = false;
        for (Transition transition : transitions) {
            if (transition.isEnabled(places)) {
                boolean fired = monitor.fireTransition(transition.getId());
                if (fired) {
                    firedAnyTransition = true;
                    // logger.info(segmentName + " fired transition: " + transition.getId());
                }
            }
        }
        if (!firedAnyTransition) {
            // logger.info(segmentName + " had no enabled transitions.");
        }
        // logger.info(segmentName + " execution finished.");
        // Clear the running flag.
        synchronized (this) {
            isRunning = false;
        }
    }
}
