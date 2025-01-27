package petrinet;

import java.util.List;

public class Segment {
    private final String name;
    private final List<Transition> transitions;

    // Constructor
    public Segment(String name, List<Transition> transitions) {
        this.name = name;
        this.transitions = transitions;
    }

    // Get the name of the segment
    public String getName() {
        return name;
    }

    // Execute the segment by firing its transitions in sequence
    public void execute() {
        for (Transition transition : transitions) {
            if (transition.isEnabled()) {
                transition.fire();
            } else {
                throw new IllegalStateException(
                    "Transition " + transition.getName() + " is not enabled."
                );
            }
        }
    }

    // Get the transitions in this segment
    public List<Transition> getTransitions() {
        return transitions;
    }
}
