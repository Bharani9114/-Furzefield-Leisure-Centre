package flc.report;

import flc.model.ExerciseType;
import flc.model.Lesson;
import flc.system.FLCSystem;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ReportGenerator {
    private final FLCSystem system;

    public ReportGenerator(FLCSystem system) {
        this.system = Objects.requireNonNull(system, "system must not be null");
    }

    public void printMonthlyLessonReport(int month) {
        List<Lesson> lessons = system.getTimetable().getLessonsByMonth(month);

        System.out.println("Monthly Lesson Report (Month " + month + ")");
        System.out.printf("%-4s %-9s %-10s %-12s %-8s %-10s%n",
                "Week", "Day", "Slot", "Exercise", "Attended", "Avg Rating");

        for (Lesson lesson : lessons) {
            System.out.printf("%-4d %-9s %-10s %-12s %-8d %-10.2f%n",
                    lesson.getWeekNumber(),
                    lesson.getDay(),
                    lesson.getTimeSlot(),
                    lesson.getExerciseType().getName(),
                    lesson.getAttendedCount(),
                    lesson.getAverageRating());
        }
    }

    public void printChampionReport(int month) {
        List<Lesson> lessons = system.getTimetable().getLessonsByMonth(month);
        Map<ExerciseType, Double> incomeByType = new EnumMap<>(ExerciseType.class);

        for (Lesson lesson : lessons) {
            incomeByType.merge(lesson.getExerciseType(), lesson.getIncome(), Double::sum);
        }

        ExerciseType champion = incomeByType.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        System.out.println("Monthly Champion Report (Month " + month + ")");
        System.out.printf("%-12s %-10s%n", "Exercise", "Income");

        for (ExerciseType type : ExerciseType.values()) {
            double income = incomeByType.getOrDefault(type, 0.0);
            String label = type.getName();
            if (type == champion && income > 0.0) {
                label = label + " *";
            }
            System.out.printf("%-12s %-10.2f%n", label, income);
        }
    }
}
