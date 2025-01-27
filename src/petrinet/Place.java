
package petrinet;

public class Place {
    private String id;
    private int tokens;
    private int maxTokens;

    public Place(String id, int maxTokens) {
        this.id = id;
        this.tokens = 0; // Default to 0 tokens initially
        this.maxTokens = maxTokens;
    }

    public String getId() {
        return id;
    }

    public int getTokens() {
        return tokens;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void addTokens(int n) {
        if (tokens + n > maxTokens) {
            throw new IllegalArgumentException("Cannot add tokens. Exceeds maxTokens for place " + id);
        }
        tokens += n;
    }

    public void removeTokens(int n) {
        if (n > tokens) {
            throw new IllegalArgumentException("Cannot remove tokens. Not enough tokens in place " + id);
        }
        tokens -= n;
    }

    public void setTokens(int tokens) {
        if (tokens > maxTokens) {
            throw new IllegalArgumentException("Tokens cannot exceed maxTokens for place " + id);
        }
        this.tokens = tokens;
    }
}
