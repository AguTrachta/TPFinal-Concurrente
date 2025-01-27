
package petrinet;

import java.util.List;

public class Transition {
    private String id;
    private List<Place> inputPlaces;
    private List<Place> outputPlaces;
    private boolean isTimed;
    private long delay; // Delay in milliseconds (for timed transitions)

    public Transition(String id, List<Place> inputPlaces, List<Place> outputPlaces, boolean isTimed, long delay) {
        this.id = id;
        this.inputPlaces = inputPlaces;
        this.outputPlaces = outputPlaces;
        this.isTimed = isTimed;
        this.delay = delay;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        // Check if all input places have enough tokens
        for (Place place : inputPlaces) {
            if (place.getTokens() <= 0) {
                return false;
            }
        }
        return true;
    }

    public void fire() throws InterruptedException {
        if (!isEnabled()) {
            throw new IllegalStateException("Transition " + id + " is not enabled!");
        }

        if (isTimed) {
            // Wait for the specified delay
            System.out.println("Transition " + id + " is waiting for " + delay + "ms");
            Thread.sleep(delay);
        }

        // Move tokens from input places to output places
        for (Place place : inputPlaces) {
            place.removeTokens(1); // Remove 1 token from each input place
        }
        for (Place place : outputPlaces) {
            place.addTokens(1); // Add 1 token to each output place
        }

        System.out.println("Transition " + id + " fired!");
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    public boolean isTimed() {
        return isTimed;
    }

    public void setTimed(boolean isTimed) {
        this.isTimed = isTimed;
    }
}
