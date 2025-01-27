package petrinet;

public class Place {
    private final String name;
    private int tokens;

    // Constructor
    public Place(String name, int tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    // Get the name of the place
    public String getName() {
        return name;
    }

    // Get the current number of tokens
    public int getTokens() {
        return tokens;
    }

    // Add tokens to the place
    public void addTokens(int tokens) {
        this.tokens += tokens;
    }

    // Remove tokens from the place
    public void removeTokens(int tokens) {
        if (this.tokens >= tokens) {
            this.tokens -= tokens;
        } else {
            throw new IllegalArgumentException("Not enough tokens to remove.");
        }
    }

    // Check if the place has at least a certain number of tokens
    public boolean hasTokens(int requiredTokens) {
        return tokens >= requiredTokens;
    }
}
