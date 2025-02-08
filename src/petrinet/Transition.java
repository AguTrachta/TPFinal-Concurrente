package petrinet;

import java.util.Map;
import utils.Logger;

/**
 * Represents a transition in the Petri net.
 * Contains the preconditions and postconditions (token changes) for firing.
 */
public class Transition {
    private final int id;
    private final Map<Integer, Integer> preconditions; // Required tokens: placeId -> tokens required
    private final Map<Integer, Integer> postconditions; // Tokens to add: placeId -> tokens to add
    private final boolean isTemporal;
    private final long delay; // delay in milliseconds for temporal transitions

    // Logger instance for tracing events in Transition
    private static final Logger logger = Logger.getInstance();

    /**
     * Constructor for non-temporal transitions.
     *
     * @param id             transition identifier
     * @param preconditions  map of preconditions (placeId -> required tokens)
     * @param postconditions map of postconditions (placeId -> tokens to add)
     */
    public Transition(int id, Map<Integer, Integer> preconditions, Map<Integer, Integer> postconditions) {
        this(id, preconditions, postconditions, false, 0);
        logger.info("Transition " + id + " created (non-temporal).");
    }

    /**
     * Constructor for transitions, including temporal information.
     *
     * @param id             transition identifier
     * @param preconditions  map of preconditions (placeId -> required tokens)
     * @param postconditions map of postconditions (placeId -> tokens to add)
     * @param isTemporal     indicates if the transition is temporal
     * @param delay          delay in milliseconds if temporal
     */
    public Transition(int id, Map<Integer, Integer> preconditions, Map<Integer, Integer> postconditions,
            boolean isTemporal, long delay) {
        this.id = id;
        this.preconditions = preconditions;
        this.postconditions = postconditions;
        this.isTemporal = isTemporal;
        this.delay = delay;
        if (isTemporal) {
            logger.info("Transition " + id + " created (temporal) with delay " + delay + " ms.");
        } else {
            logger.info("Transition " + id + " created (non-temporal).");
        }
    }

    public int getId() {
        return id;
    }

    public boolean isTemporal() {
        return isTemporal;
    }

    public long getDelay() {
        return delay;
    }

    /**
     * Checks if the transition is enabled based on the current state of places.
     *
     * @param places the Places object containing current token counts.
     * @return true if all preconditions are met, false otherwise.
     */
    public boolean isEnabled(Places places) {
        boolean enabled = true;
        for (Map.Entry<Integer, Integer> entry : preconditions.entrySet()) {
            int placeId = entry.getKey();
            int requiredTokens = entry.getValue();
            if (places.getTokenCount(placeId) < requiredTokens) {
                enabled = false;
                // Removed per-iteration debug logging to reduce log volume.
                // logger.debug("Transition " + id + " is not enabled because place " + placeId
                // +
                // " has insufficient tokens.");
                break;
            }
        }
        // Removed logging here as well to avoid frequent logs in loop.
        // if (enabled) {
        // logger.debug("Transition " + id + " is enabled.");
        // }
        return enabled;
    }

    /**
     * Fires the transition: applies preconditions and postconditions to the Places.
     *
     * @param places the Places object to update.
     * @throws InterruptedException if the thread is interrupted during a temporal
     *                              delay.
     */
    public void fire(Places places) throws InterruptedException {
        logger.info("Attempting to fire Transition " + id);
        if (!isEnabled(places)) {
            logger.error("Transition " + id + " is not enabled and cannot be fired.");
            throw new IllegalStateException("Transition " + id + " is not enabled.");
        }
        if (isTemporal) {
            logger.info("Transition " + id + " is temporal. Sleeping for " + delay + " ms.");
            Thread.sleep(delay);
        }
        // Remove tokens from input places (preconditions)
        for (Map.Entry<Integer, Integer> entry : preconditions.entrySet()) {
            int placeId = entry.getKey();
            int tokensToRemove = entry.getValue();
            // Removing logging in the inner loop to reduce frequency.
            // logger.debug("Transition " + id + " removing " + tokensToRemove + " tokens
            // from place " + placeId);
            places.removeTokens(placeId, tokensToRemove);
        }
        // Add tokens to output places (postconditions)
        for (Map.Entry<Integer, Integer> entry : postconditions.entrySet()) {
            int placeId = entry.getKey();
            int tokensToAdd = entry.getValue();
            // Removing logging in the inner loop.
            // logger.debug("Transition " + id + " adding " + tokensToAdd + " tokens to
            // place " + placeId);
            places.addTokens(placeId, tokensToAdd);
        }
        logger.info("Transition " + id + " fired successfully.");
    }
}

