package com.pi.backend.Service;

import com.pi.backend.model.ApplicationDocument;
import com.pi.backend.model.ApplicationDocumentId;
import com.pi.backend.model.Document;
import com.pi.backend.model.DocumentType;
import com.pi.backend.repository.ApplicationDocumentRepository;
import com.pi.backend.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
@Service
public class DocumentService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_TOTAL_SIZE = 10 * 1024 * 1024;

    private static final String MIME_PDF = "application/pdf";

    private static final String MIME_DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private static final Set<String> ALLOWED_TYPES =
            Set.of(MIME_PDF, MIME_DOCX);

    private final DocumentRepository documentRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            ApplicationDocumentRepository applicationDocumentRepository) {

        this.documentRepository = documentRepository;
        this.applicationDocumentRepository = applicationDocumentRepository;
    }
    @Transactional
    public void uploadDocument(
            List<MultipartFile> files,
            List<DocumentType> documentTypes,
            long applicationId)
            throws NoSuchAlgorithmException, IOException {
         boolean isCreated=false;

         List<Path> createdFiles=new ArrayList<>();
        validateUpload(files, documentTypes);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Tika tika = new Tika();

        Path storageDirectory = Paths.get("uploads/applications");
        Files.createDirectories(storageDirectory);

        for (int i = 0; i < files.size(); i++) {

            MultipartFile file = files.get(i);
            DocumentType documentType = documentTypes.get(i);

            String mimeType;

            try (InputStream inputStream = file.getInputStream()) {
                mimeType = tika.detect(inputStream);
            }

            validateMimeType(mimeType, documentType);

            String fileHash = calculateHash(file, digest);

            Optional<Document> existingDocument =
                    documentRepository.findByFileHash(fileHash);

            Document document;
           try {
               if (existingDocument.isPresent()) {

                   document = existingDocument.get();

                   if (document.getDocumentType() != documentType) {
                       throw new IllegalArgumentException(
                               "The document already exists with a different document type."
                       );
                   }

               } else {

                   String extension = getExtension(mimeType);
                   String storedFileName = fileHash + extension;

                  Path path = storageDirectory.resolve(storedFileName);
                   createdFiles.add(path);

                   Files.copy(file.getInputStream(), path);

                   document = Document.builder()
                           .documentType(documentType)
                           .fileName(file.getOriginalFilename())
                           .fileHash(fileHash)
                           .filePath(path.toString())
                           .build();

                   document = documentRepository.save(document);
                   isCreated=true;
               }

               ApplicationDocumentId applicationDocumentId =
                       ApplicationDocumentId.builder()
                               .applicationId(applicationId)
                               .documentId(document.getId())
                               .build();

               applicationDocumentRepository.save(
                       new ApplicationDocument(applicationDocumentId)
               );
           }catch (Exception e) {
               if(isCreated) {
                   createdFiles.forEach(p-> {
                       try {
                           Files.deleteIfExists(p);
                       } catch (IOException ex) {
                           e.addSuppressed(ex);
                       }
                   });

               }
               throw e;
           }
        }
    }
    private void validateUpload(
            List<MultipartFile> files,
            List<DocumentType> documentTypes) {

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one file is required."
            );
        }

        if (files.size() > 2) {
            throw new IllegalArgumentException(
                    "A maximum of two documents can be uploaded."
            );
        }

        if (documentTypes == null || files.size() != documentTypes.size()) {
            throw new IllegalArgumentException(
                    "A document type is required for every file."
            );
        }

        if (documentTypes.stream().distinct().count() != documentTypes.size()) {
            throw new IllegalArgumentException(
                    "Only one document of each type is allowed."
            );
        }

        if (files.stream().anyMatch(MultipartFile::isEmpty)) {
            throw new IllegalArgumentException(
                    "Empty files are not allowed."
            );
        }

        if (files.stream().anyMatch(
                file -> file.getSize() > MAX_FILE_SIZE)) {

            throw new IllegalArgumentException(
                    "Each file must not exceed 5 MiB."
            );
        }

        long totalSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (totalSize > MAX_TOTAL_SIZE) {
            throw new IllegalArgumentException(
                    "The total size of all files must not exceed 10 MiB."
            );
        }
    }
    private void validateMimeType(
            String mimeType,
            DocumentType documentType) {

        if (!ALLOWED_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(
                    "Only PDF and DOCX files are allowed."
            );
        }

        if (documentType == DocumentType.CV
                && !MIME_PDF.equals(mimeType)) {

            throw new IllegalArgumentException(
                    "A CV must be a PDF."
            );
        }

        if (documentType == DocumentType.COVER_LETTER
                && !MIME_PDF.equals(mimeType)
                && !MIME_DOCX.equals(mimeType)) {

            throw new IllegalArgumentException(
                    "A cover letter must be a PDF or DOCX."
            );
        }
    }
    private String calculateHash(
            MultipartFile file,
            MessageDigest digest)
            throws IOException {

        try (InputStream inputStream = file.getInputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }
    private String getExtension(String mimeType) {

        return switch (mimeType) {
            case MIME_PDF -> ".pdf";
            case MIME_DOCX -> ".docx";
            default -> throw new IllegalArgumentException(
                    "Unsupported file type."
            );
        };
    }
}
