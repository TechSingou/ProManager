package ml.malikura.service;

import ml.malikura.dto.EditTaskDTO;
import ml.malikura.dto.EvaluateTaskDTO;
import ml.malikura.dto.NewCommentDTO;
import ml.malikura.dto.NewTaskDTO;
import ml.malikura.entity.CommentEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.config.Task;

import java.util.List;

public interface TaskService {
    public TaskEntity saveNewTask(NewTaskDTO newTaskDTO);
    public TaskEntity getTask(Long taskId);
    public Page<TaskEntity> getTasksByProject(Long projectId, int page, int size);
    public void updateTask(EditTaskDTO editTaskDTO, Long taskId);
    public void onTaskSubmit(Long taskId);
    public ProjectEntity getAssociatedProject(Long projectId);
    public Page<TaskEntity> getAllByPage(String keyword, int page,int size);
    public Page<TaskEntity> getMyTasks(String email,int page,int size);
    public void evaluateTask(EvaluateTaskDTO evaluateTaskDTO, Long taskId);
    public void solveTask(Long taskId, String proposition);
    public void deleteTaskById(Long taskId);
    public void saveComment(Long taskId,String authorComment, String content);
    public boolean isExecutorOfTask(String email,Long taskId);
}
