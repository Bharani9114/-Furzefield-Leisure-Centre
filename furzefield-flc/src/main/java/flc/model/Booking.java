package flc.model;

import java.util.Objects;

public final class Booking {
    private final String bookingId;
    private final Member member;
    private Lesson lesson;
    private BookingStatus status;
    private Review review;

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = Objects.requireNonNull(bookingId, "bookingId must not be null");
        this.member = Objects.requireNonNull(member, "member must not be null");
        this.lesson = Objects.requireNonNull(lesson, "lesson must not be null");
        this.status = BookingStatus.BOOKED;

        member.addBooking(this);
        lesson.addBooking(this);
    }

    public String getBookingId() {
        return bookingId;
    }

    public Member getMember() {
        return member;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Review getReview() {
        return review;
    }

    public void attend(String reviewText, int rating) {
        this.review = new Review(reviewText, rating);
        this.status = BookingStatus.ATTENDED;
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            return;
        }
        lesson.removeBooking(this);
        status = BookingStatus.CANCELLED;
    }

    public void changeTo(Lesson newLesson) {
        Objects.requireNonNull(newLesson, "newLesson must not be null");
        if (lesson.equals(newLesson)) {
            return;
        }

        lesson.removeBooking(this);
        lesson = newLesson;
        lesson.addBooking(this);
        status = BookingStatus.CHANGED;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Booking booking)) {
            return false;
        }
        return bookingId.equals(booking.bookingId);
    }

    @Override
    public int hashCode() {
        return bookingId.hashCode();
    }
}
