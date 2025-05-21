package com.LeetCode.LeetCodeController.controller;

import com.LeetCode.LeetCodeController.model.LeetCodeRequest;
import com.LeetCode.LeetCodeController.service.LeetCodeRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leetcode")
public class LeetCodeController {

    @Autowired
    private LeetCodeRunnerService runnerService;

    @PostMapping("/run")
    public String runCode(@RequestBody LeetCodeRequest request) {
        return runnerService.runCode(request);
    }
}
