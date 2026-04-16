package flc;

import flc.model.Lesson;
import flc.system.FLCSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ReportTest {

    @Test
    void lessonAverageRatingUsesOnlyAttendedBookings() {
        FLCSystem system = new FLCSystem();
        Lesson lesson = system.findLessonById("L001");

        assertNotNull(lesson);
        assertNotNull(system.bookLesson("M001", "L001"));
        assertNotNull(system.bookLesson("M002", "L001"));
        assertNotNull(system.bookLesson("M003", "L001"));
        assertTrue(system.attendLesson("B001", "Strong class", 5));
        assertTrue(system.attendLesson("B002", "Well paced", 3));

        assertEquals(4.0, lesson.getAverageRating(), 0.0001);
    }
}
