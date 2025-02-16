
package petrinet;

import monitor.MonitorInterface;
import monitor.Monitor;
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
     * Checks whether this segment has at least one transition that is both enabled
     * (i.e., has the required tokens) and allowed by the policy.
     *
     * @return true if at least one transition is ready to be fired; false otherwise.
     */
    private boolean hasEnabledAndAllowedTransition() {
        // Cast the monitor to our concrete Monitor to access the policy.
        if (monitor instanceof Monitor) {
            Monitor concreteMonitor = (Monitor) monitor;
            for (Transition transition : transitions) {
                if (transition.isEnabled(places) &&
                    concreteMonitor.getPolicy().allowTransition(transition.getId(), places)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if this segment can be scheduled.
     * It is available for scheduling if it is not already running and it has at
     * least one transition that is both enabled and allowed by the policy.
     *
     * @return true if the segment can be scheduled; false otherwise.
     */
    public synchronized boolean canBeScheduled() {
        return !isRunning && hasEnabledAndAllowedTransition();
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
        boolean firedAnyTransition = false;
        for (Transition transition : transitions) {
            if (transition.isEnabled(places)) {
                boolean fired = monitor.fireTransition(transition.getId());
                if (fired) {
                    firedAnyTransition = true;
                }
            }
        }
        // Clear the running flag.
        synchronized (this) {
            isRunning = false;
        }
    }
}
