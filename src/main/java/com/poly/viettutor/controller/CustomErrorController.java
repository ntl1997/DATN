package com.poly.viettutor.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusObj != null) {
            int statusCode = Integer.parseInt(statusObj.toString());
            return switch (statusCode) {
                case 400 -> loadPage(model, "400 - Bad Request", "client/error/400");
                case 403 -> loadPage(model, "403 - Forbidden", "client/error/403");
                case 404 -> loadPage(model, "404 - Not Found", "client/error/404");
                default -> loadPage(model, "500 - Internal Server Error", "client/error/500");
            };
        }
        return loadPage(model, "500 - Internal Server Error", "client/error/500");
    }

    public String loadPage(Model model, String title, String viewPath) {
        model.addAttribute("title", title);
        model.addAttribute("content", viewPath);
        return "client/layout/index";
    }

}
