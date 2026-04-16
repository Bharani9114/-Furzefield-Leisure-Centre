package flc;

import flc.model.Booking;
import flc.model.BookingStatus;
import flc.model.Lesson;
import flc.system.FLCSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ChangeBookingTest {

    @Test
    void changeBookingMovesTheBookingAndReleasesTheOldLessonSlot() {
        FLCSystem system = new FLCSystem();
        Lesson originalLesson = system.findLessonById("L001");
        Lesson newLesson = system.findLessonById("L002");
        Booking booking = system.bookLesson("M001", "L001");

        assertNotNull(originalLesson);
        assertNotNull(newLesson);
        assertNotNull(booking);
        assertEquals(3, originalLesson.getAvailableSpots());

        Booking changedBooking = system.changeBooking(booking.getBookingId(), "L002");

        assertNotNull(changedBooking);
        assertSame(booking, changedBooking);
        assertEquals(BookingStatus.CHANGED, changedBooking.getStatus());
        assertEquals("L002", changedBooking.getLesson().getLessonId());
        assertEquals(4, originalLesson.getAvailableSpots());
        assertEquals(3, newLesson.getAvailableSpots());
        assertTrue(originalLesson.getBookings().isEmpty());
    }
}
