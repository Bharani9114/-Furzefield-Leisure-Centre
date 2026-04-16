package flc;

import flc.model.Booking;
import flc.system.FLCSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

final class CapacityTest {

    @Test
    void bookLessonReturnsNullWhenLessonIsFull() {
        FLCSystem system = new FLCSystem();

        assertNotNull(system.bookLesson("M001", "L001"));
        assertNotNull(system.bookLesson("M002", "L001"));
        assertNotNull(system.bookLesson("M003", "L001"));
        assertNotNull(system.bookLesson("M004", "L001"));

        Booking rejectedBooking = system.bookLesson("M005", "L001");

        assertNull(rejectedBooking);
    }
}
