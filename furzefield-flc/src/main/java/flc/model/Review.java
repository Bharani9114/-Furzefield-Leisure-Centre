package flc.model;

import java.util.Objects;

public final class Review {
    private final String text;
    private final int rating;

    public Review(String text, int rating) {
        this.text = Objects.requireNonNull(text, "text must not be null");
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }
}
