import petrinet.*;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    try {
      // Create Segment A
      Segment segmentA = new Segment("Segment A");

      // Define Places in Segment A
      Place P0 = segmentA.createPlace("P0", 5); // Max tokens: 5
      Place P1 = segmentA.createPlace("P1", 1); // Max tokens: 1
      Place P2 = segmentA.createPlace("P2", 1); // Max tokens: 1
      Place P3 = segmentA.createPlace("P3", 1); // Max tokens: 1
      Place P4 = segmentA.createPlace("P4", 5); // Max tokens: 1

      // Add initial tokens
      P0.addTokens(5); // Start with 5 tokens in P0
      P1.addTokens(1); // Start with 1 token in P1
      P4.addTokens(5);

      // Define Transitions in Segment A
      Transition T0 = segmentA.createTransition(
          "T0", // Transition ID
          Arrays.asList(P0, P1, P4), // Input places
          Arrays.asList(P2), // Output places
          false, // Not timed
          0 // No delay
      );

      Transition T1 = segmentA.createTransition(
          "T1", // Transition ID
          Arrays.asList(P2), // Input places
          Arrays.asList(P3, P1), // Output places
          false, // Not timed
          0 // No delay
      );

      // Display initial state
      System.out.println("Initial state:");
      System.out.println("P0 tokens: " + P0.getTokens());
      System.out.println("P1 tokens: " + P1.getTokens());
      System.out.println("P2 tokens: " + P2.getTokens());
      System.out.println("P3 tokens: " + P3.getTokens());
      System.out.println("P4 tokens: " + P4.getTokens());

      // Execute Segment A
      System.out.println("\nExecuting Segment A...");
      segmentA.execute();

      // Display state after execution
      System.out.println("\nState after executing Segment A:");
      System.out.println("P0 tokens: " + P0.getTokens());
      System.out.println("P1 tokens: " + P1.getTokens());
      System.out.println("P2 tokens: " + P2.getTokens());
      System.out.println("P3 tokens: " + P3.getTokens());
      System.out.println("P4 tokens: " + P4.getTokens());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
