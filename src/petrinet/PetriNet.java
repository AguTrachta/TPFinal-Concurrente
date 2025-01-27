package petrinet;

import java.util.List;

public interface PetriNet {

    // Adds a new place to the Petri net
    void addPlace(Place place);

    // Adds a new transition to the Petri net
    void addTransition(Transition transition);

    // Fires a specified transition, updating tokens in places
    void fireTransition(Transition transition);

    // Returns the number of tokens in a specified place
    int getTokens(Place place);

    // Adds tokens to a specified place
    void addTokens(Place place, int tokens);

    // Removes tokens from a specified place
    void removeTokens(Place place, int tokens);

    // Returns all enabled transitions (those that can fire)
    List<Transition> getEnabledTransitions();

    // Checks if a specific transition is enabled
    boolean isTransitionEnabled(Transition transition);

    // Returns a list of all places in the Petri net
    List<Place> getPlaces();

    // Returns a list of all transitions in the Petri net
    List<Transition> getTransitions();
}
