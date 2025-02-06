package monitor;

import petrinet.Places;
import petrinet.Transition;
import java.util.Map;

/**
 * The Monitor coordinates the firing of transitions.
 * It implements synchronization, checks if transitions are enabled,
 * executes the transition, and enforces invariants.
 */
public class Monitor implements MonitorInterface {

  private final Places places;
  // A map of transition id to Transition object.
  private final Map<Integer, Transition> transitions;

  /**
   * Constructs a Monitor with the given Places and Transitions.
   * 
   * @param places      the shared Places object representing the Petri net state.
   * @param transitions a mapping from transition IDs to Transition objects.
   */
  public Monitor(Places places, Map<Integer, Transition> transitions) {
    this.places = places;
    this.transitions = transitions;
  }

  /**
   * Attempts to fire the specified transition.
   * The method is synchronized to ensure only one transition is fired at a time.
   *
   * @param transitionId the identifier of the transition to fire.
   * @return true if the transition was successfully fired, false otherwise.
   */
  @Override
  public synchronized boolean fireTransition(int transitionId) {
    Transition transition = transitions.get(transitionId);
    if (transition == null) {
      System.out.println("Transition " + transitionId + " not found.");
      return false;
    }

    // Check if the transition is enabled (sufficient tokens, etc.)
    if (!transition.isEnabled(places)) {
      System.out.println("Transition " + transitionId + " is not enabled.");
      return false;
    }

    try {
      // Fire the transition.
      transition.fire(places);

      // Check invariants after firing.
      if (!places.checkInvariants()) {
        System.out.println("Invariants violated after firing transition " + transitionId + ".");
        return false;
      }

      System.out.println("Transition " + transitionId + " fired successfully.");
      return true;
    } catch (InterruptedException e) {
      // Restore the interrupted status and log the interruption.
      Thread.currentThread().interrupt();
      System.out.println("Transition " + transitionId + " firing was interrupted.");
      return false;
    } catch (Exception e) {
      // Catch other exceptions, such as IllegalStateException from token operations.
      System.out.println("Error firing transition " + transitionId + ": " + e.getMessage());
      return false;
    }
  }
}
