package dev.sivalabs.musings.config;

import dev.sivalabs.musings.shared.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ModelAndView handle(ResourceNotFoundException e) {
        return handleInternal(e, "error/404", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ModelAndView handle(AccessDeniedException e) {
        return handleInternal(e, "error/403", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Exception e) {
        return handleInternal(e, "error/500", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView handleInternal(Exception e, String viewName, HttpStatus httpStatus) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("exception", e);
        modelAndView.setViewName(viewName);
        modelAndView.setStatus(httpStatus);
        return modelAndView;
    }
}
