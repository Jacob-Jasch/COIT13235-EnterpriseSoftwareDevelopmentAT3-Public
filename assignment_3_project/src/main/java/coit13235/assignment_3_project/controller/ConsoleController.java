package coit13235.assignment_3_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import coit13235.assignment_3_project.console.ConsoleInterface;

/**
 * Web controller for launching the console interface easily.
 */
@Controller
@RequestMapping("/console")
public class ConsoleController {
    
    @Autowired
    private ConsoleInterface consoleInterface;
    
    @GetMapping
    public String consolePage(Model model) {
        model.addAttribute("pageTitle", "Console Interface Launcher");
        return "console-launcher";
    }
    
    @PostMapping("/start")
    @ResponseBody
    public String startConsole() {
        try {
            // Start the console interface in a separate thread
            Thread consoleThread = new Thread(() -> {
                try {
                    consoleInterface.startConsoleInterface();
                } catch (Exception e) {
                    System.err.println("Console interface error: " + e.getMessage());
                }
            });
            consoleThread.setDaemon(true);
            consoleThread.start();
            
            return "{ \"status\": \"success\", \"message\": \"Console interface started! Check your application logs or terminal for the console interface.\" }";
        } catch (Exception e) {
            return "{ \"status\": \"error\", \"message\": \"Failed to start console interface: " + e.getMessage() + "\" }";
        }
    }
    
    @GetMapping("/status")
    @ResponseBody
    public String getConsoleStatus() {
        return "{ \"status\": \"ready\", \"message\": \"Console interface is available to start\" }";
    }
}