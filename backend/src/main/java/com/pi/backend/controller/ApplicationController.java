
package com.pi.backend.controller;

import com.pi.backend.Service.ApplicationService;
import com.pi.backend.dto.ApplicationRequest;
import com.pi.backend.model.Application;
import com.pi.backend.model.ApplicationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/create")
    public Application createApplication(@RequestBody ApplicationRequest applicationRequest) {

       return  applicationService.createApplication(applicationRequest);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Void>  updateApplication(@PathVariable Long id, @RequestBody ApplicationStatus status) {
         applicationService.updateApplication(id, status);
        return ResponseEntity.ok().build();

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable   Long id) {
         applicationService.deleteApplication(id);
       return ResponseEntity.ok().build();
    }
    
}
