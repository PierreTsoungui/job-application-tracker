package com.pi.backend.repository;

import com.pi.backend.model.ApplicationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationDocumentRepository  extends JpaRepository<ApplicationDocument,Long> {
}
