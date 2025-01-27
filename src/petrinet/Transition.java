package petrinet;

import java.util.List;

public class Transition {
    private final String name;
    private final List<Place> inputPlaces;
    private final List<Place> outputPlaces;

    // Constructor
    public Transition(String name, List<Place> inputPlaces, List<Place> outputPlaces) {
        this.name = name;
        this.inputPlaces = inputPlaces;
        this.outputPlaces = outputPlaces;
    }

    // Get the name of the transition
    public String getName() {
        return name;
    }

    // Check if the transition is enabled
    public boolean isEnabled() {
        return inputPlaces.stream().allMatch(place -> place.hasTokens(1));
    }

    // Fire the transition
    public void fire() {
        if (!isEnabled()) {
            throw new IllegalStateException("Transition is not enabled.");
        }

        // Remove tokens from input places
        inputPlaces.forEach(place -> place.removeTokens(1));

        // Add tokens to output places
        outputPlaces.forEach(place -> place.addTokens(1));
    }

    // Get the input places
    public List<Place> getInputPlaces() {
        return inputPlaces;
    }

    // Get the output places
    public List<Place> getOutputPlaces() {
        return outputPlaces;
    }
}
