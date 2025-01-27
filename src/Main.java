
import petrinet.Place;
import petrinet.Transition;
import petrinet.Segment;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    try {
      // Create a segment
      Segment segmentA = new Segment("A");

      // Create places in Segment A
      Place P1 = segmentA.createPlace("P1", 5); // Place with max 5 tokens
      Place P2 = segmentA.createPlace("P2", 2); // Place with max 2 tokens

      // Add initial tokens
      P1.addTokens(3); // Add 3 tokens to P1
      System.out.println("Initial P1 tokens: " + P1.getTokens());
      System.out.println("Initial P2 tokens: " + P2.getTokens());

      // Create a transition in Segment A
      Transition T1 = segmentA.createTransition(
          "T1", // Transition ID
          Arrays.asList(P1), // Input places
          Arrays.asList(P2), // Output places
          false, // Not timed
          0 // No delay
      );

      // Execute Segment A
      System.out.println("\nExecuting Segment A...");
      segmentA.execute();

      // Print the state of places after execution
      System.out.println("After firing T1:");
      System.out.println("P1 tokens: " + P1.getTokens());
      System.out.println("P2 tokens: " + P2.getTokens());

      // Create a second segment (to test shared places)
      Segment segmentB = new Segment("B");
      Place P3 = segmentB.createPlace("P3", 3); // Place with max 3 tokens
      segmentB.addSharedPlace(P2); // Share P2 with Segment B

      // Create a timed transition in Segment B
      Transition T2 = segmentB.createTransition(
          "T2", // Transition ID
          Arrays.asList(P2), // Input places (shared)
          Arrays.asList(P3), // Output places
          true, // Timed transition
          2000 // 2-second delay
      );

      // Execute Segment B
      System.out.println("\nExecuting Segment B...");
      segmentB.execute();

      // Print the state of places after Segment B execution
      System.out.println("After firing T2:");
      System.out.println("P2 tokens: " + P2.getTokens());
      System.out.println("P3 tokens: " + P3.getTokens());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
