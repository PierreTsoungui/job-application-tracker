package com.pi.backend.Service;

import com.pi.backend.dto.ApplicationRequest;
import com.pi.backend.model.Application;
import com.pi.backend.model.ApplicationStatus;
import com.pi.backend.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    public  ApplicationRepository repository;

    public ApplicationService(ApplicationRepository repository) {
        this.repository = repository;
    }

    public Application createApplication(ApplicationRequest applicationRequest) {
          Application application = Application.builder()
                .company(applicationRequest.getCompany())
                .position(applicationRequest.getPosition())
                .status(applicationRequest.getStatus())
                .dateApplied(applicationRequest.getDateApplied())
                .build();
          return repository.save(application);
        
    }

    public void updateApplication(Long applicationId, ApplicationStatus status) {

        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);

        repository.save(application);
    }

    public void  deleteApplication(Long applicationId) {
        checkIfApplicationExists(applicationId);
        repository.deleteById(applicationId);


    }

    public  void  checkIfApplicationExists(Long applicationId) {
        if(!repository.existsById(applicationId)) {
            throw new RuntimeException("Application not found");

        }
    }
    
}

