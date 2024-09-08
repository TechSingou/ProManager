package ml.malikura.util;

import ml.malikura.entity.TaskEntity;

import java.util.List;

public class MyUtils {
    public static double getMoyenne(List<TaskEntity> tasks){
        double sum = 0;
        double moyenne = 0;
        if (!tasks.isEmpty()) {
            for (TaskEntity task : tasks) {
                sum += task.getTaskEvaluation();
            }
            moyenne = sum / tasks.size();
        }
        return moyenne;
    }
}
