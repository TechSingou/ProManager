package ml.malikura.handler;

import ml.malikura.exception.ProjectServiceBusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProjectBusinessExceptionHandler {

    @ExceptionHandler(ProjectServiceBusinessException.class)
    public String handleProjectServiceBusinessException(ProjectServiceBusinessException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "commons/errorPage";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, Model model) {
        // Custom error message or response
        String errorMessage = "action impossible car cela entraine une violation d'intégrité entre les données";
        model.addAttribute("errorMessage", errorMessage);
        return "commons/errorPage";
    }

}
