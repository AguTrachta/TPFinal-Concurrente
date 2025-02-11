
package utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TransitionNotifier holds a ReentrantLock and a Condition used to signal
 * changes in the Petri net state (for example, when tokens change).
 */
public class TransitionNotifier {
  public static final ReentrantLock lock = new ReentrantLock();
  public static final Condition transitionsEnabled = lock.newCondition();
}
