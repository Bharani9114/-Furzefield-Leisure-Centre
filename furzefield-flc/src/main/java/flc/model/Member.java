package flc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Member {
    private final String memberId;
    private final String name;
    private final List<Booking> bookings;

    public Member(String memberId, String name) {
        this.memberId = Objects.requireNonNull(memberId, "memberId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.bookings = new ArrayList<>();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    public boolean hasBookingFor(Lesson lesson) {
        Objects.requireNonNull(lesson, "lesson must not be null");
        return bookings.stream()
                .anyMatch(booking -> lesson.equals(booking.getLesson())
                        && booking.getStatus() != BookingStatus.CANCELLED);
    }

    void addBooking(Booking booking) {
        Objects.requireNonNull(booking, "booking must not be null");
        if (!bookings.contains(booking)) {
            bookings.add(booking);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Member member)) {
            return false;
        }
        return memberId.equals(member.memberId);
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }
}
