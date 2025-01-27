
package petrinet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Segment {
    private String id;
    private List<Place> places; // Places created by this segment
    private List<Transition> transitions; // Transitions created by this segment
    private Set<Place> sharedPlaces; // Shared places from other segments

    public Segment(String id) {
        this.id = id;
        this.places = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.sharedPlaces = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    // Create and add a place to this segment
    public Place createPlace(String id, int maxTokens) {
        Place place = new Place(id, maxTokens);
        places.add(place);
        return place;
    }

    // Add a shared place from another segment
    public void addSharedPlace(Place place) {
        if (places.contains(place)) {
            places.add(place);
        }
    }

    // Create and add a transition to this segment
    public Transition createTransition(String id, List<Place> inputPlaces, List<Place> outputPlaces, boolean isTimed,
            long delay) {
        Transition transition = new Transition(id, inputPlaces, outputPlaces, isTimed, delay);
        transitions.add(transition);
        return transition;
    }

    // Execute all enabled transitions in this segment
    public void execute() throws InterruptedException {
        for (Transition transition : transitions) {
            if (transition.isEnabled()) {
                transition.fire();
            }
        }
    }

    public List<Place> getPlaces() {
        return places;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

}
