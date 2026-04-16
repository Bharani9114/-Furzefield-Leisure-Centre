package flc;

import flc.model.Booking;
import flc.system.FLCSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

final class BookingTest {

    @Test
    void bookLessonReturnsNullForDuplicateBooking() {
        FLCSystem system = new FLCSystem();

        Booking firstBooking = system.bookLesson("M001", "L001");
        Booking duplicateBooking = system.bookLesson("M001", "L001");

        assertNotNull(firstBooking);
        assertNull(duplicateBooking);
    }
}
