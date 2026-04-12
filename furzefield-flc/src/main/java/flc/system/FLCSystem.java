package flc.system;

import flc.data.MemberData;
import flc.data.TimetableData;
import flc.model.Booking;
import flc.model.BookingStatus;
import flc.model.Day;
import flc.model.Lesson;
import flc.model.Member;
import flc.model.Timetable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FLCSystem {
    private final Timetable timetable;
    private final List<Member> members;
    private final List<Booking> bookings;
    private int bookingIdCounter;

    public FLCSystem() {
        this(TimetableData.buildTimetable(), MemberData.buildMembers());
    }

    public FLCSystem(Timetable timetable, List<Member> members) {
        this.timetable = Objects.requireNonNull(timetable, "timetable must not be null");
        this.members = new ArrayList<>(Objects.requireNonNull(members, "members must not be null"));
        this.bookings = new ArrayList<>();
        this.bookingIdCounter = 1;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public List<Member> getMembers() {
        return List.copyOf(members);
    }

    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    public Member findMemberById(String memberId) {
        String normalizedMemberId = normalizeId(memberId);
        if (normalizedMemberId == null) {
            return null;
        }
        return members.stream()
                .filter(member -> member.getMemberId().equalsIgnoreCase(normalizedMemberId))
                .findFirst()
                .orElse(null);
    }

    public Lesson findLessonById(String lessonId) {
        String normalizedLessonId = normalizeId(lessonId);
        if (normalizedLessonId == null) {
            return null;
        }
        return timetable.getLessons().stream()
                .filter(lesson -> lesson.getLessonId().equalsIgnoreCase(normalizedLessonId))
                .findFirst()
                .orElse(null);
    }

    public Booking findBookingById(String bookingId) {
        String normalizedBookingId = normalizeId(bookingId);
        if (normalizedBookingId == null) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getBookingId().equalsIgnoreCase(normalizedBookingId))
                .findFirst()
                .orElse(null);
    }

    public List<Lesson> getTimetableByDay(Day day) {
        if (day == null) {
            return List.of();
        }
        return timetable.getLessonsByDay(day);
    }

    public List<Lesson> getTimetableByExercise(String exerciseName) {
        if (normalizeId(exerciseName) == null) {
            return List.of();
        }
        return timetable.getLessonsByExercise(exerciseName);
    }

    public Booking bookLesson(String memberId, String lessonId) {
        Member member = findMemberById(memberId);
        Lesson lesson = findLessonById(lessonId);

        if (member == null || lesson == null || lesson.isFull() || member.hasBookingFor(lesson)) {
            return null;
        }

        Booking booking = new Booking(nextBookingId(), member, lesson);
        bookings.add(booking);
        bookingIdCounter++;
        return booking;
    }

    public Booking changeBooking(String bookingId, String newLessonId) {
        Booking booking = findBookingById(bookingId);
        Lesson newLesson = findLessonById(newLessonId);

        if (booking == null || newLesson == null || !canChange(booking, newLesson)) {
            return null;
        }

        booking.changeTo(newLesson);
        return booking;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null || !isActiveBooking(booking)) {
            return false;
        }

        booking.cancel();
        return true;
    }

    public boolean attendLesson(String bookingId, String reviewText, int rating) {
        Booking booking = findBookingById(bookingId);
        if (booking == null || !isActiveBooking(booking) || reviewText == null || rating < 1 || rating > 5) {
            return false;
        }

        booking.attend(reviewText, rating);
        return true;
    }

    private boolean canChange(Booking booking, Lesson newLesson) {
        return isActiveBooking(booking)
                && !booking.getLesson().equals(newLesson)
                && !newLesson.isFull()
                && !hasActiveBookingFor(booking.getMember(), newLesson, booking);
    }

    private boolean hasActiveBookingFor(Member member, Lesson lesson, Booking excludedBooking) {
        return member.getBookings().stream()
                .filter(booking -> !booking.equals(excludedBooking))
                .anyMatch(booking -> booking.getStatus() != BookingStatus.CANCELLED
                        && lesson.equals(booking.getLesson()));
    }

    private boolean isActiveBooking(Booking booking) {
        return booking.getStatus() == BookingStatus.BOOKED
                || booking.getStatus() == BookingStatus.CHANGED;
    }

    private String nextBookingId() {
        return String.format("B%03d", bookingIdCounter);
    }

    private static String normalizeId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return id.trim();
    }
}
