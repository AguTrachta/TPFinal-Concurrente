package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monitor.Monitor;
import monitor.MonitorInterface;
import monitor.Policy;
import petrinet.Places;
import petrinet.Segment;
import petrinet.Transition;

/**
 * PetriNet encapsulates the construction of a Petri net.
 * It creates Places, Transitions, Segments, and the Monitor.
 */
public class PetriNet {
    private Places places;
    private Map<Integer, Transition> transitions;
    private List<Segment> segments;
    private MonitorInterface monitor;

    /**
     * Constructs a PetriNet using the specified Policy.
     *
     * @param policy the policy to be used by the Monitor.
     */
    public PetriNet(Policy policy) {
        buildNet(policy);
    }

    private void buildNet(Policy policy) {
        // Initialize Places.
        // We create 15 places: p0 to p14.
        // p0 and p4 have max 5 tokens; p1, p6, p7, and p10 have max 1 token; others start with 0.
        places = new Places();
        places.addPlace(0, 5);  // p0: 5 tokens (max 5)
        places.addPlace(1, 1);  // p1: 1 token (max 1)
        places.addPlace(2, 0);
        places.addPlace(3, 0);
        places.addPlace(4, 5);  // p4: 5 tokens (max 5)
        places.addPlace(5, 0);
        places.addPlace(6, 1);  // p6: 1 token (max 1)
        places.addPlace(7, 1);  // p7: 1 token (max 1)
        places.addPlace(8, 0);
        places.addPlace(9, 0);
        places.addPlace(10, 1); // p10: 1 token (max 1)
        places.addPlace(11, 0);
        places.addPlace(12, 0);
        places.addPlace(13, 0);
        places.addPlace(14, 0);

        // Create Transitions.
        transitions = new HashMap<>();

        // T0: takes from p0, p1, p4; sends 1 token to p2.
        Map<Integer, Integer> preT0 = new HashMap<>();
        preT0.put(0, 1);
        preT0.put(1, 1);
        preT0.put(4, 1);
        Map<Integer, Integer> postT0 = new HashMap<>();
        postT0.put(2, 1);
        Transition t0 = new Transition(0, preT0, postT0);

        // T1: takes from p2; sends 1 token to p1 and 1 token to p3; 200ms delay.
        Map<Integer, Integer> preT1 = new HashMap<>();
        preT1.put(2, 1);
        Map<Integer, Integer> postT1 = new HashMap<>();
        postT1.put(1, 1);
        postT1.put(3, 1);
        Transition t1 = new Transition(1, preT1, postT1, true, 5);

        // T2: takes from p3 and p6; sends 1 token to p5 and 1 token to p4.
        Map<Integer, Integer> preT2 = new HashMap<>();
        preT2.put(3, 1);
        preT2.put(6, 1);
        Map<Integer, Integer> postT2 = new HashMap<>();
        postT2.put(5, 1);
        postT2.put(4, 1);
        Transition t2 = new Transition(2, preT2, postT2);

        // T3: takes from p7 and p3; sends 1 token to p8 and 1 token to p4.
        Map<Integer, Integer> preT3 = new HashMap<>();
        preT3.put(7, 1);
        preT3.put(3, 1);
        Map<Integer, Integer> postT3 = new HashMap<>();
        postT3.put(8, 1);
        postT3.put(4, 1);
        Transition t3 = new Transition(3, preT3, postT3);

        // T4: takes from p8; sends 1 token to p7 and 1 token to p9; 200ms delay.
        Map<Integer, Integer> preT4 = new HashMap<>();
        preT4.put(8, 1);
        Map<Integer, Integer> postT4 = new HashMap<>();
        postT4.put(7, 1);
        postT4.put(9, 1);
        Transition t4 = new Transition(4, preT4, postT4, true, 50);

        // T5: takes from p5; sends 1 token to p6 and 1 token to p9; 200ms delay.
        Map<Integer, Integer> preT5 = new HashMap<>();
        preT5.put(5, 1);
        Map<Integer, Integer> postT5 = new HashMap<>();
        postT5.put(6, 1);
        postT5.put(9, 1);
        Transition t5 = new Transition(5, preT5, postT5, true, 5);

        // T6: takes from p9 and p10; sends 1 token to p11.
        Map<Integer, Integer> preT6 = new HashMap<>();
        preT6.put(9, 1);
        preT6.put(10, 1);
        Map<Integer, Integer> postT6 = new HashMap<>();
        postT6.put(11, 1);
        Transition t6 = new Transition(6, preT6, postT6);

        // T7: takes from p9 and p10; sends 1 token to p12.
        Map<Integer, Integer> preT7 = new HashMap<>();
        preT7.put(9, 1);
        preT7.put(10, 1);
        Map<Integer, Integer> postT7 = new HashMap<>();
        postT7.put(12, 1);
        Transition t7 = new Transition(7, preT7, postT7);

        // T8: takes from p12; sends 1 token to p10 and 1 token to p14; 100ms delay.
        Map<Integer, Integer> preT8 = new HashMap<>();
        preT8.put(12, 1);
        Map<Integer, Integer> postT8 = new HashMap<>();
        postT8.put(10, 1);
        postT8.put(14, 1);
        Transition t8 = new Transition(8, preT8, postT8, true, 50);

        // T9: takes from p11; sends 1 token to p13; 100ms delay.
        Map<Integer, Integer> preT9 = new HashMap<>();
        preT9.put(11, 1);
        Map<Integer, Integer> postT9 = new HashMap<>();
        postT9.put(13, 1);
        Transition t9 = new Transition(9, preT9, postT9, true, 5);

        // T10: takes from p13; sends 1 token to p10 and 1 token to p14; 100ms delay.
        Map<Integer, Integer> preT10 = new HashMap<>();
        preT10.put(13, 1);
        Map<Integer, Integer> postT10 = new HashMap<>();
        postT10.put(10, 1);
        postT10.put(14, 1);
        Transition t10 = new Transition(10, preT10, postT10, true, 5);

        // T11: takes from p14; sends 1 token to p0; no delay.
        Map<Integer, Integer> preT11 = new HashMap<>();
        preT11.put(14, 1);
        Map<Integer, Integer> postT11 = new HashMap<>();
        postT11.put(0, 1);
        Transition t11 = new Transition(11, preT11, postT11);

        // Store transitions.
        transitions.put(t0.getId(), t0);
        transitions.put(t1.getId(), t1);
        transitions.put(t2.getId(), t2);
        transitions.put(t3.getId(), t3);
        transitions.put(t4.getId(), t4);
        transitions.put(t5.getId(), t5);
        transitions.put(t6.getId(), t6);
        transitions.put(t7.getId(), t7);
        transitions.put(t8.getId(), t8);
        transitions.put(t9.getId(), t9);
        transitions.put(t10.getId(), t10);
        transitions.put(t11.getId(), t11);

        // Create the Monitor using the provided policy.
        monitor = new Monitor(places, transitions, policy);

        // Create Segments according to the specification.
        segments = new ArrayList<>();

        // SegmentA handles T0 and T1.
        List<Transition> segA = new ArrayList<>();
        segA.add(t0);
        segA.add(t1);
        Segment segmentA = new Segment("SegmentA", segA, monitor, places);

        // SegmentB handles T2 and T5.
        List<Transition> segB = new ArrayList<>();
        segB.add(t2);
        segB.add(t5);
        Segment segmentB = new Segment("SegmentB", segB, monitor, places);

        // SegmentC handles T3 and T4.
        List<Transition> segC = new ArrayList<>();
        segC.add(t3);
        segC.add(t4);
        Segment segmentC = new Segment("SegmentC", segC, monitor, places);

        // SegmentD handles T6, T9 and T10.
        List<Transition> segD = new ArrayList<>();
        segD.add(t6);
        segD.add(t9);
        segD.add(t10);
        Segment segmentD = new Segment("SegmentD", segD, monitor, places);

        // SegmentE handles T7 and T8.
        List<Transition> segE = new ArrayList<>();
        segE.add(t7);
        segE.add(t8);
        Segment segmentE = new Segment("SegmentE", segE, monitor, places);

        // SegmentF handles T11 only.
        List<Transition> segF = new ArrayList<>();
        segF.add(t11);
        Segment segmentF = new Segment("SegmentF", segF, monitor, places);

        segments.add(segmentA);
        segments.add(segmentB);
        segments.add(segmentC);
        segments.add(segmentD);
        segments.add(segmentE);
        segments.add(segmentF);
    }

    public Places getPlaces() {
        return places;
    }

    public Map<Integer, Transition> getTransitions() {
        return transitions;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public MonitorInterface getMonitor() {
        return monitor;
    }
}