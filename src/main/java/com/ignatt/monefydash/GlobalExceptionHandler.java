package com.ignatt.monefydash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        logger.warn("Страница не найдена: {}", ex.getRequestURL());
        model.addAttribute("status", 404);
        model.addAttribute("error", "Страница не найдена");
        model.addAttribute("message", "Запрошенная страница не существует.");
        return "error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Превышен максимальный размер файла: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage",
                "Файл слишком большой. Максимальный размер: 10 МБ.");
        return "redirect:/start";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Внутренняя ошибка сервера", ex);
        model.addAttribute("status", 500);
        model.addAttribute("error", "Внутренняя ошибка сервера");
        model.addAttribute("message", "Произошла непредвиденная ошибка. Попробуйте позже.");
        return "error";
    }
}