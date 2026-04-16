package flc;

import flc.model.Booking;
import flc.model.BookingStatus;
import flc.system.FLCSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AttendTest {

    @Test
    void attendLessonMarksBookingAsAttendedAndAddsAReview() {
        FLCSystem system = new FLCSystem();
        Booking booking = system.bookLesson("M001", "L001");

        assertNotNull(booking);

        boolean attended = system.attendLesson(booking.getBookingId(), "Great session", 5);

        assertTrue(attended);
        assertEquals(BookingStatus.ATTENDED, booking.getStatus());
        assertNotNull(booking.getReview());
        assertEquals("Great session", booking.getReview().getText());
        assertEquals(5, booking.getReview().getRating());
    }
}
