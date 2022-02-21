package com.kpi.tefresolver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MyController {
    @GetMapping("/python")
    public void python() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", resolvePythonScriptPath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        System.out.println(readProcessOutput(process.getInputStream()));
    }
    private String resolvePythonScriptPath() {
        File file = new File("src/main/java/com/kpi/tefresolver/hello.py");
        return file.getAbsolutePath();
    }
    private List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }
}
