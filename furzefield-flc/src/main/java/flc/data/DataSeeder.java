package flc.data;

import flc.model.Booking;
import flc.model.Lesson;
import flc.model.Member;
import flc.system.FLCSystem;

import java.util.List;
import java.util.Objects;

public final class DataSeeder {
    private static final String[] REVIEW_TEXTS = {
            "Great energy from start to finish.",
            "Really clear instruction and a fun pace.",
            "Tough session but worth it.",
            "Loved the structure and the atmosphere.",
            "Good workout and easy to follow.",
            "The coach kept everyone engaged.",
            "A solid class with plenty of variety.",
            "Felt welcoming and well organised.",
            "Excellent tempo and clear demonstrations.",
            "Would happily book this again."
    };

    private DataSeeder() {
    }

    public static void seed(FLCSystem system) {
        Objects.requireNonNull(system, "system must not be null");
        if (!system.getBookings().isEmpty()) {
            return;
        }

        List<Member> members = system.getMembers();
        List<Lesson> lessons = system.getTimetable().getLessons();
        if (members.isEmpty() || lessons.isEmpty()) {
            return;
        }

        int seededCount = 0;
        for (int index = 0; index < lessons.size(); index += 2) {
            Member member = members.get(seededCount % members.size());
            Lesson lesson = lessons.get(index);
            Booking booking = system.bookLesson(member.getMemberId(), lesson.getLessonId());
            if (booking == null) {
                continue;
            }

            String reviewText = REVIEW_TEXTS[seededCount % REVIEW_TEXTS.length];
            int rating = (seededCount % 5) + 1;
            system.attendLesson(booking.getBookingId(), reviewText, rating);
            seededCount++;
        }
    }
}
