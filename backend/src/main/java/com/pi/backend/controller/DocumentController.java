package com.pi.backend.controller;

import com.pi.backend.Service.DocumentService;
import com.pi.backend.model.DocumentType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller("api/documents")
public class DocumentController {

    private  final DocumentService documentService;
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<Void> createDocument(@RequestPart( value="documents",required = false) List<MultipartFile> files, @RequestPart( value="documentTypes",required = false) List<DocumentType>documentTypeList, @RequestParam("applicationId") long applicationId) throws NoSuchAlgorithmException, IOException {
         documentService.uploadDocument(files, documentTypeList,applicationId);
        return  ResponseEntity.ok().build();

    }
}
