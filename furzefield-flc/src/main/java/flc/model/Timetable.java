package flc.model;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class Timetable {
    private final List<Lesson> lessons;

    public Timetable(List<Lesson> lessons) {
        this.lessons = List.copyOf(Objects.requireNonNull(lessons, "lessons must not be null"));
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<Lesson> getLessonsByDay(Day day) {
        Objects.requireNonNull(day, "day must not be null");
        return lessons.stream()
                .filter(lesson -> lesson.getDay() == day)
                .toList();
    }

    public List<Lesson> getLessonsByExercise(String exerciseName) {
        Objects.requireNonNull(exerciseName, "exerciseName must not be null");
        String normalizedInput = normalizeExerciseName(exerciseName);
        return lessons.stream()
                .filter(lesson -> normalizeExerciseName(lesson.getExerciseType().getName()).equals(normalizedInput)
                        || normalizeExerciseName(lesson.getExerciseType().name()).equals(normalizedInput))
                .toList();
    }

    public List<Lesson> getLessonsByMonth(int month) {
        return lessons.stream()
                .filter(lesson -> lesson.getMonth() == month)
                .toList();
    }

    private static String normalizeExerciseName(String exerciseName) {
        return exerciseName.trim()
                .replace('_', ' ')
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
