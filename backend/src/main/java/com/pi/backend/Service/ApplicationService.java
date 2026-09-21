package com.pi.backend.Service;

import com.pi.backend.dto.ApplicationRequest;
import com.pi.backend.dto.ApplicationResponse;
import com.pi.backend.model.*;

import com.pi.backend.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ApplicationService {

     private   final  ApplicationRepository repository;
     private final DocumentService documentService;
    public ApplicationService(ApplicationRepository repository,  DocumentService documentService) {
        this.repository = repository;
        this.documentService = documentService;
  }

    public ApplicationResponse  createApplication(ApplicationRequest applicationRequest, List<MultipartFile> files, List<DocumentType> documentTypeList){
        Application application = Application.builder()
                .company(applicationRequest.getCompany())
                .position(applicationRequest.getPosition())
                .status(applicationRequest.getStatus())
                .dateApplied(applicationRequest.getDateApplied())
                .build();
        Application newApplication = repository.save(application);
        if (documentTypeList != null && !documentTypeList.isEmpty()
                && files != null && !files.isEmpty()) {
            try {
                documentService.uploadDocument(
                        files,
                        documentTypeList,
                        newApplication.getId()
                );

            } catch (Exception e) {

                return new ApplicationResponse(
                        newApplication.getId(),
                        "Bewerbung wurde erstellt, aber beim Speichern der Dokumente ist ein Fehler aufgetreten."
                );
            }
        }

        return new ApplicationResponse(
                newApplication.getId(),
                "Bewerbung wurde erfolgreich erstellt."
        );
    }

    public void updateApplication(Long applicationId, ApplicationStatus status) {

        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Bewerbung wurde nicht gefunden"));

        application.setStatus(status);

        repository.save(application);
    }

    public void  deleteApplication(Long applicationId) {
        checkIfApplicationExists(applicationId);
        repository.deleteById(applicationId);


    }

    public  void  checkIfApplicationExists(Long applicationId) {
        if(!repository.existsById(applicationId)) {
            throw new RuntimeException("Bewerbung wurde nicht gefunden");

        }
    }


}

