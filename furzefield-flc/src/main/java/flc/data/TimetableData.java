package flc.data;

import flc.model.Day;
import flc.model.ExerciseType;
import flc.model.Lesson;
import flc.model.TimeSlot;
import flc.model.Timetable;

import java.util.ArrayList;
import java.util.List;

public final class TimetableData {
    private static final Day[] WEEKEND_DAYS = {
            Day.SATURDAY,
            Day.SATURDAY,
            Day.SATURDAY,
            Day.SUNDAY,
            Day.SUNDAY,
            Day.SUNDAY
    };

    private static final TimeSlot[] WEEKEND_SLOTS = {
            TimeSlot.MORNING,
            TimeSlot.AFTERNOON,
            TimeSlot.EVENING,
            TimeSlot.MORNING,
            TimeSlot.AFTERNOON,
            TimeSlot.EVENING
    };

    private static final ExerciseType[][] WEEKEND_EXERCISES = {
            {ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.BOX_FIT, ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ, ExerciseType.YOGA},
            {ExerciseType.ZUMBA, ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ, ExerciseType.YOGA, ExerciseType.BOX_FIT, ExerciseType.ZUMBA},
            {ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.YOGA, ExerciseType.BODY_BLITZ, ExerciseType.ZUMBA, ExerciseType.AQUACISE},
            {ExerciseType.BODY_BLITZ, ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.BOX_FIT, ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ},
            {ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.AQUACISE, ExerciseType.ZUMBA, ExerciseType.YOGA, ExerciseType.BOX_FIT},
            {ExerciseType.YOGA, ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ, ExerciseType.BOX_FIT, ExerciseType.ZUMBA, ExerciseType.YOGA},
            {ExerciseType.ZUMBA, ExerciseType.BOX_FIT, ExerciseType.YOGA, ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ, ExerciseType.ZUMBA},
            {ExerciseType.AQUACISE, ExerciseType.BODY_BLITZ, ExerciseType.BOX_FIT, ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.AQUACISE}
    };

    private TimetableData() {
    }

    public static Timetable buildTimetable() {
        List<Lesson> lessons = new ArrayList<>();
        int lessonNumber = 1;

        for (int week = 1; week <= WEEKEND_EXERCISES.length; week++) {
            int month = week <= 4 ? 3 : 4;
            ExerciseType[] exerciseTypes = WEEKEND_EXERCISES[week - 1];

            for (int session = 0; session < exerciseTypes.length; session++) {
                lessons.add(new Lesson(
                        String.format("L%03d", lessonNumber++),
                        exerciseTypes[session],
                        WEEKEND_DAYS[session],
                        WEEKEND_SLOTS[session],
                        week,
                        month
                ));
            }
        }

        return new Timetable(lessons);
    }
}
