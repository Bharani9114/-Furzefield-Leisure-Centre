package flc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Lesson {
    public static final int DEFAULT_CAPACITY = 4;

    private final String lessonId;
    private final ExerciseType exerciseType;
    private final Day day;
    private final TimeSlot timeSlot;
    private final int weekNumber;
    private final int month;
    private final int capacity;
    private final List<Booking> bookings;

    public Lesson(String lessonId,
                  ExerciseType exerciseType,
                  Day day,
                  TimeSlot timeSlot,
                  int weekNumber,
                  int month) {
        this(lessonId, exerciseType, day, timeSlot, weekNumber, month, DEFAULT_CAPACITY);
    }

    public Lesson(String lessonId,
                  ExerciseType exerciseType,
                  Day day,
                  TimeSlot timeSlot,
                  int weekNumber,
                  int month,
                  int capacity) {
        this.lessonId = Objects.requireNonNull(lessonId, "lessonId must not be null");
        this.exerciseType = Objects.requireNonNull(exerciseType, "exerciseType must not be null");
        this.day = Objects.requireNonNull(day, "day must not be null");
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot must not be null");
        if (weekNumber < 1) {
            throw new IllegalArgumentException("weekNumber must be positive");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be between 1 and 12");
        }
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.weekNumber = weekNumber;
        this.month = month;
        this.capacity = capacity;
        this.bookings = new ArrayList<>();
    }

    public String getLessonId() {
        return lessonId;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public Day getDay() {
        return day;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public int getMonth() {
        return month;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    public int getAvailableSpots() {
        return Math.max(0, capacity - bookings.size());
    }

    public boolean isFull() {
        return bookings.size() >= capacity;
    }

    public int getAttendedCount() {
        return (int) bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.ATTENDED)
                .count();
    }

    public double getAverageRating() {
        return bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.ATTENDED)
                .map(Booking::getReview)
                .filter(Objects::nonNull)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public double getIncome() {
        return getAttendedCount() * exerciseType.getPrice();
    }

    void addBooking(Booking booking) {
        Objects.requireNonNull(booking, "booking must not be null");
        if (!bookings.contains(booking)) {
            bookings.add(booking);
        }
    }

    void removeBooking(Booking booking) {
        Objects.requireNonNull(booking, "booking must not be null");
        bookings.remove(booking);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Lesson lesson)) {
            return false;
        }
        return lessonId.equals(lesson.lessonId);
    }

    @Override
    public int hashCode() {
        return lessonId.hashCode();
    }
}
