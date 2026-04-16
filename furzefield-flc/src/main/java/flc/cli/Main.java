package flc.cli;

import flc.data.DataSeeder;
import flc.model.Booking;
import flc.model.BookingStatus;
import flc.model.Day;
import flc.model.ExerciseType;
import flc.model.Lesson;
import flc.model.Member;
import flc.model.TimeSlot;
import flc.report.ReportGenerator;
import flc.system.FLCSystem;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class Main {
    private final FLCSystem system;
    private final ReportGenerator reportGenerator;
    private final Scanner scanner;

    public Main() {
        this.system = new FLCSystem();
        DataSeeder.seed(system);
        this.reportGenerator = new ReportGenerator(system);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            switch (readMenuSelection()) {
                case 1 -> handleBookLesson();
                case 2 -> handleChangeOrCancel();
                case 3 -> handleAttendLesson();
                case 4 -> reportGenerator.printMonthlyLessonReport(readMonthSelection());
                case 5 -> reportGenerator.printChampionReport(readMonthSelection());
                case 0 -> {
                    System.out.println("Goodbye.");
                    running = false;
                }
                default -> System.out.println("Please choose one of the listed options.");
            }

            if (running) {
                System.out.println();
            }
        }
    }

    private void printMainMenu() {
        System.out.println("=== Furzefield Leisure Centre ===");
        System.out.println("1. Book a lesson");
        System.out.println("2. Change/Cancel a booking");
        System.out.println("3. Attend a lesson");
        System.out.println("4. Monthly lesson report");
        System.out.println("5. Monthly champion report");
        System.out.println("0. Exit");
    }

    private void handleBookLesson() {
        printMembersTable();
        Member member = promptForMember();
        if (member == null) {
            return;
        }

        List<Lesson> lessons = promptForLessonSelectionList();
        if (lessons.isEmpty()) {
            System.out.println("No lessons match that selection.");
            return;
        }

        printLessonsTable(lessons);
        String lessonId = readRequiredText("Enter lesson ID to book: ");
        Lesson lesson = system.findLessonById(lessonId);
        if (lesson == null) {
            System.out.println("That lesson ID was not found.");
            return;
        }

        if (lesson.isFull()) {
            System.out.println("That lesson is already full.");
            return;
        }

        if (member.hasBookingFor(lesson)) {
            System.out.println("That member already has a booking for this lesson.");
            return;
        }

        Booking booking = system.bookLesson(member.getMemberId(), lesson.getLessonId());
        if (booking == null) {
            System.out.println("Booking could not be completed.");
            return;
        }

        System.out.printf("Booking created. Booking ID: %s%n", booking.getBookingId());
    }

    private void handleChangeOrCancel() {
        printMembersTable();
        Member member = promptForMember();
        if (member == null) {
            return;
        }

        List<Booking> memberBookings = getMemberBookings(member);
        if (memberBookings.isEmpty()) {
            System.out.println("This member has no bookings.");
            return;
        }

        printBookingsTable(memberBookings);
        String bookingId = readRequiredText("Enter booking ID: ");
        Booking booking = system.findBookingById(bookingId);
        if (booking == null || !booking.getMember().equals(member)) {
            System.out.println("That booking was not found for this member.");
            return;
        }

        int action = readBinarySelection("Choose action [1] Change  [2] Cancel: ");
        if (action == 2) {
            if (booking.getStatus() != BookingStatus.BOOKED && booking.getStatus() != BookingStatus.CHANGED) {
                System.out.println("Only booked or changed lessons can be cancelled.");
                return;
            }

            if (system.cancelBooking(booking.getBookingId())) {
                System.out.printf("Booking %s cancelled.%n", booking.getBookingId());
            } else {
                System.out.println("Cancellation could not be completed.");
            }
            return;
        }
        if (booking.getStatus() != BookingStatus.BOOKED && booking.getStatus() != BookingStatus.CHANGED) {
            System.out.println("Only booked or changed lessons can be changed.");
            return;
        }

        List<Lesson> lessons = promptForLessonSelectionList();
        if (lessons.isEmpty()) {
            System.out.println("No lessons match that selection.");
            return;
        }

        printLessonsTable(lessons);
        String newLessonId = readRequiredText("Enter new lesson ID: ");
        Lesson newLesson = system.findLessonById(newLessonId);
        if (newLesson == null) {
            System.out.println("That lesson ID was not found.");
            return;
        }

        if (booking.getLesson().equals(newLesson)) {
            System.out.println("Choose a different lesson to change this booking.");
            return;
        }

        if (newLesson.isFull()) {
            System.out.println("That lesson is already full.");
            return;
        }

        if (member.hasBookingFor(newLesson)) {
            System.out.println("That member already has a booking for this lesson.");
            return;
        }

        Booking changedBooking = system.changeBooking(booking.getBookingId(), newLesson.getLessonId());
        if (changedBooking == null) {
            System.out.println("Booking change could not be completed.");
            return;
        }

        Lesson updatedLesson = changedBooking.getLesson();
        System.out.printf(
                "Booking %s changed to %s (Week %d, %s %s, %s).%n",
                changedBooking.getBookingId(),
                updatedLesson.getLessonId(),
                updatedLesson.getWeekNumber(),
                formatDay(updatedLesson.getDay()),
                formatTimeSlot(updatedLesson.getTimeSlot()),
                updatedLesson.getExerciseType().getName()
        );
    }

    private void handleAttendLesson() {
        String bookingId = readRequiredText("Enter booking ID: ");
        Booking booking = system.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("That booking ID was not found.");
            return;
        }

        if (booking.getStatus() != BookingStatus.BOOKED && booking.getStatus() != BookingStatus.CHANGED) {
            System.out.println("Only booked or changed lessons can be marked as attended.");
            return;
        }

        String reviewText = readRequiredText("Enter review text: ");
        int rating = readRating();

        if (system.attendLesson(booking.getBookingId(), reviewText, rating)) {
            System.out.printf("Attendance recorded for booking %s.%n", booking.getBookingId());
        } else {
            System.out.println("Attendance could not be recorded.");
        }
    }

    private Member promptForMember() {
        String memberId = readRequiredText("Enter member ID: ");
        Member member = system.findMemberById(memberId);
        if (member == null) {
            System.out.println("That member ID was not found.");
        }
        return member;
    }

    private List<Lesson> promptForLessonSelectionList() {
        while (true) {
            System.out.println("View lessons by:");
            System.out.println("1. Day");
            System.out.println("2. Exercise");

            int selection = readBinarySelection("Choose an option: ");
            if (selection == 1) {
                return system.getTimetableByDay(promptForDay());
            }
            if (selection == 2) {
                return system.getTimetableByExercise(promptForExerciseType().getName());
            }
        }
    }

    private Day promptForDay() {
        while (true) {
            System.out.println("[1] Saturday  [2] Sunday");
            int selection = readBinarySelection("Choose a day: ");
            if (selection == 1) {
                return Day.SATURDAY;
            }
            if (selection == 2) {
                return Day.SUNDAY;
            }
        }
    }

    private ExerciseType promptForExerciseType() {
        while (true) {
            ExerciseType[] exerciseTypes = ExerciseType.values();
            for (int index = 0; index < exerciseTypes.length; index++) {
                System.out.printf("%d. %s%n", index + 1, exerciseTypes[index].getName());
            }

            int selection = readSelection("Choose an exercise: ");
            if (selection >= 1 && selection <= exerciseTypes.length) {
                return exerciseTypes[selection - 1];
            }
            System.out.println("Please choose one of the listed exercises.");
        }
    }

    private int readMonthSelection() {
        while (true) {
            System.out.println("[3] March  [4] April");
            int selection = readSelection("Choose a month: ");
            if (selection == 3 || selection == 4) {
                return selection;
            }
            System.out.println("Please choose 3 or 4.");
        }
    }

    private int readRating() {
        while (true) {
            int rating = readSelection("Enter rating (1-5): ");
            if (rating >= 1 && rating <= 5) {
                return rating;
            }
            System.out.println("Please enter a rating from 1 to 5.");
        }
    }

    private int readMenuSelection() {
        while (true) {
            int selection = readSelection("Choose an option: ");
            if (selection >= 0 && selection <= 5) {
                return selection;
            }
            System.out.println("Please choose one of the listed options.");
        }
    }

    private int readSelection(String prompt) {
        while (true) {
            String input = readRawLine(prompt);
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private int readBinarySelection(String prompt) {
        while (true) {
            int selection = readSelection(prompt);
            if (selection == 1 || selection == 2) {
                return selection;
            }
            System.out.println("Please choose either 1 or 2.");
        }
    }

    private String readRequiredText(String prompt) {
        while (true) {
            String input = readRawLine(prompt);
            if (!input.isBlank()) {
                return input.trim();
            }
            System.out.println("Input cannot be blank.");
        }
    }

    private String readRawLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private List<Booking> getMemberBookings(Member member) {
        return member.getBookings().stream()
                .sorted(Comparator.comparing(Booking::getBookingId))
                .toList();
    }

    private void printMembersTable() {
        System.out.printf("%-10s %-20s%n", "Member ID", "Name");
        for (Member member : system.getMembers()) {
            System.out.printf("%-10s %-20s%n", member.getMemberId(), member.getName());
        }
    }

    private void printLessonsTable(List<Lesson> lessons) {
        System.out.printf("%-10s %-6s %-10s %-10s %-12s %-11s%n",
                "Lesson ID", "Week", "Day", "Slot", "Exercise", "Spaces Left");
        for (Lesson lesson : lessons) {
            System.out.printf("%-10s %-6d %-10s %-10s %-12s %-11d%n",
                    lesson.getLessonId(),
                    lesson.getWeekNumber(),
                    formatDay(lesson.getDay()),
                    formatTimeSlot(lesson.getTimeSlot()),
                    lesson.getExerciseType().getName(),
                    lesson.getAvailableSpots());
        }
    }

    private void printBookingsTable(List<Booking> bookings) {
        System.out.printf("%-10s %-10s %-6s %-10s %-10s %-12s %-10s%n",
                "Booking ID", "Lesson ID", "Week", "Day", "Slot", "Exercise", "Status");
        for (Booking booking : bookings) {
            Lesson lesson = booking.getLesson();
            System.out.printf("%-10s %-10s %-6d %-10s %-10s %-12s %-10s%n",
                    booking.getBookingId(),
                    lesson.getLessonId(),
                    lesson.getWeekNumber(),
                    formatDay(lesson.getDay()),
                    formatTimeSlot(lesson.getTimeSlot()),
                    lesson.getExerciseType().getName(),
                    formatBookingStatus(booking.getStatus()));
        }
    }

    private String formatDay(Day day) {
        return titleCase(day.name());
    }

    private String formatTimeSlot(TimeSlot timeSlot) {
        return titleCase(timeSlot.name());
    }

    private String formatBookingStatus(BookingStatus status) {
        return titleCase(status.name());
    }

    private String titleCase(String value) {
        String normalized = value.toLowerCase(Locale.ROOT).replace('_', ' ');
        String[] parts = normalized.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
