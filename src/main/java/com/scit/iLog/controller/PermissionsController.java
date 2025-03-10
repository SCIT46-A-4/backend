package com.scit.iLog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/permissions")
public class PermissionsController {

    @GetMapping({"", "/"})
    public String handleGetPermissionsRequestView(
    		) {
        return "/children/permissions/childPermissionRequestView";
    }


}

