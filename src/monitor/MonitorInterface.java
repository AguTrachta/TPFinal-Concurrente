
package monitor;

/**
 * Interface for the Monitor.
 * The monitor exposes a single method to fire a transition.
 */
public interface MonitorInterface {
  /**
   * Attempts to fire the transition with the given identifier.
   * 
   * @param transition the identifier of the transition to fire.
   * @return true if the transition was successfully fired, false otherwise.
   */
  boolean fireTransition(int transition);
}
