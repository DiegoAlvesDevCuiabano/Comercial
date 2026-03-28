package com.controle_comercial.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Entidade não encontrada: {} (ID: {})", ex.getEntityName(), ex.getEntityId());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/home";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Erro de validação: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/home";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Erro não tratado: ", ex);
        model.addAttribute("error", "Ocorreu um erro inesperado. Por favor, tente novamente.");
        return "error";
    }
}
