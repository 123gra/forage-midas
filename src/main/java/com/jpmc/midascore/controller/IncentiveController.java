package com.jpmc.midascore.controller;


import com.jpmc.midascore.service.IncentiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incentives")
public class IncentiveController {

    @Autowired
    private IncentiveService incentiveService;

    @GetMapping("/{userId}/amount")
    public Double getAmount(@PathVariable Long userId) {
        return incentiveService.getAmount(userId);
    }
}