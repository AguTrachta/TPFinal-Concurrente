package petrinet;

import java.util.HashMap;
import java.util.Map;
import utils.Logger;

/**
 * Represents the set of places in the Petri net.
 * Manages token counts and provides methods for token manipulation.
 */
public class Places {
    // Map to store tokens for each place (placeId -> token count)
    private final Map<Integer, Integer> tokens;

    // Logger instance for tracing events in Places
    private static final Logger logger = Logger.getInstance();

    public Places() {
        tokens = new HashMap<>();
        logger.info("Places object created.");
    }

    /**
     * Adds a new place with an initial token count.
     *
     * @param placeId       the identifier for the place
     * @param initialTokens the initial number of tokens in this place
     */
    public synchronized void addPlace(int placeId, int initialTokens) {
        tokens.put(placeId, initialTokens);
        logger.info("Added place " + placeId + " with initial tokens: " + initialTokens);
    }

    /**
     * Retrieves the token count for a specific place.
     *
     * @param placeId the identifier for the place
     * @return the number of tokens in the place
     */
    public synchronized int getTokenCount(int placeId) {
        int count = tokens.getOrDefault(placeId, 0);
        // Removed debug logging here to avoid high-frequency log entries.
        // logger.debug("Retrieved token count for place " + placeId + ": " + count);
        return count;
    }

    /**
     * Adds tokens to a specific place.
     *
     * @param placeId the identifier for the place
     * @param count   number of tokens to add
     */
    public synchronized void addTokens(int placeId, int count) {
        int current = getTokenCount(placeId);
        int newCount = current + count;
        tokens.put(placeId, newCount);
        logger.info("Added " + count + " tokens to place " + placeId + ". New count: " + newCount);
    }

    /**
     * Removes tokens from a specific place.
     *
     * @param placeId the identifier for the place
     * @param count   number of tokens to remove
     */
    public synchronized void removeTokens(int placeId, int count) {
        int current = getTokenCount(placeId);
        if (current < count) {
            logger.error("Not enough tokens in place " + placeId + ". Required: " + count + ", available: " + current);
            throw new IllegalStateException("Not enough tokens in place " + placeId +
                    ". Required: " + count + ", available: " + current);
        }
        int newCount = current - count;
        tokens.put(placeId, newCount);
        logger.info("Removed " + count + " tokens from place " + placeId + ". New count: " + newCount);
    }

    /**
     * Checks the invariants of the Petri net places.
     * (This method can be expanded with additional invariants as needed.)
     *
     * @return true if all invariants are satisfied, false otherwise.
     */
    public synchronized boolean checkInvariants() {
        // Removed debug logging inside the loop to avoid excessive logging.
        // logger.debug("Checking invariants for Places.");
        for (Map.Entry<Integer, Integer> entry : tokens.entrySet()) {
            if (entry.getValue() < 0) {
                logger.error(
                        "Invariant violation: Place " + entry.getKey() + " has negative tokens: " + entry.getValue());
                return false;
            }
        }
        // logger.debug("All invariants satisfied.");
        return true;
    }
}

