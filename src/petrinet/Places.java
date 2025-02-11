
package petrinet;

import java.util.HashMap;
import java.util.Map;
import utils.Logger;
import utils.TransitionNotifier;

/**
 * Represents the set of places in the Petri net.
 * Manages token counts and provides methods for token manipulation.
 * 
 * This version uses the ReentrantLock from TransitionNotifier for all
 * synchronization,
 * avoiding the deadlock that occurred by mixing intrinsic (synchronized) locks
 * and explicit locks.
 */
public class Places {
    // Map to store tokens for each place (placeId -> token count)
    private final Map<Integer, Integer> tokens;
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
    public void addPlace(int placeId, int initialTokens) {
        TransitionNotifier.lock.lock();
        try {
            tokens.put(placeId, initialTokens);
            logger.info("Added place " + placeId + " with initial tokens: " + initialTokens);
        } finally {
            TransitionNotifier.lock.unlock();
        }
    }

    /**
     * Retrieves the token count for a specific place.
     *
     * @param placeId the identifier for the place
     * @return the number of tokens in the place
     */
    public int getTokenCount(int placeId) {
        TransitionNotifier.lock.lock();
        try {
            return tokens.getOrDefault(placeId, 0);
        } finally {
            TransitionNotifier.lock.unlock();
        }
    }

    /**
     * Adds tokens to a specific place.
     *
     * @param placeId the identifier for the place
     * @param count   number of tokens to add
     */
    public void addTokens(int placeId, int count) {
        TransitionNotifier.lock.lock();
        try {
            int current = getTokenCountWithoutLock(placeId);
            int newCount = current + count;
            tokens.put(placeId, newCount);
            // logger.info("Added " + count + " tokens to place " + placeId + ". New count:
            // " + newCount);
            // Signal that the token state has changed.
            TransitionNotifier.transitionsEnabled.signalAll();
        } finally {
            TransitionNotifier.lock.unlock();
        }
    }

    /**
     * Removes tokens from a specific place.
     *
     * @param placeId the identifier for the place
     * @param count   number of tokens to remove
     */
    public void removeTokens(int placeId, int count) {
        TransitionNotifier.lock.lock();
        try {
            int current = getTokenCountWithoutLock(placeId);
            if (current < count) {
                logger.error(
                        "Not enough tokens in place " + placeId + ". Required: " + count + ", available: " + current);
                throw new IllegalStateException("Not enough tokens in place " + placeId +
                        ". Required: " + count + ", available: " + current);
            }
            int newCount = current - count;
            tokens.put(placeId, newCount);
            // logger.info("Removed " + count + " tokens from place " + placeId + ". New
            // count: " + newCount);
            // Signal that the token state has changed.
            TransitionNotifier.transitionsEnabled.signalAll();
        } finally {
            TransitionNotifier.lock.unlock();
        }
    }

    /**
     * Helper method to get token count without locking.
     * This method should only be called when the lock is already held.
     */
    private int getTokenCountWithoutLock(int placeId) {
        return tokens.getOrDefault(placeId, 0);
    }

    /**
     * Checks the invariants of the Petri net places.
     *
     * @return true if all invariants are satisfied, false otherwise.
     */
    public boolean checkInvariants() {
        TransitionNotifier.lock.lock();
        try {
            for (Map.Entry<Integer, Integer> entry : tokens.entrySet()) {
                if (entry.getValue() < 0) {
                    logger.error("Invariant violation: Place " + entry.getKey() + " has negative tokens: "
                            + entry.getValue());
                    return false;
                }
            }
            return true;
        } finally {
            TransitionNotifier.lock.unlock();
        }
    }
}
