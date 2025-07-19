package com.poly.viettutor.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = (statusObj != null) ? Integer.parseInt(statusObj.toString()) : 500;
        // Object exceptionObj =
        // request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        // String reason;
        // try {
        // reason = HttpStatus.valueOf(statusCode).getReasonPhrase();
        // } catch (Exception e) {
        // reason = "Unknown Status";
        // }

        // // Ghi log ngắn gọn
        // if (exceptionObj instanceof Throwable ex) {
        // log.error("❌ [{}] Exception: {}", statusCode, ex.getMessage());
        // } else {
        // log.error("❌ [{} {}] No exception object captured", statusCode, reason);
        // }

        return switch (statusCode) {
            case 400 -> loadPage(model, "400 - Bad Request", "client/error/400");
            case 403 -> loadPage(model, "403 - Forbidden", "client/error/403");
            case 404 -> loadPage(model, "404 - Not Found", "client/error/404");
            default -> loadPage(model, "500 - Internal Server Error", "client/error/500");
        };
    }

    public String loadPage(Model model, String title, String viewPath) {
        model.addAttribute("title", title);
        model.addAttribute("content", viewPath);
        return "client/layout/index";
    }

}
