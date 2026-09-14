package com.pi.backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name="application_documents")
public class ApplicationDocument {
    @EmbeddedId
   private ApplicationDocumentId documentId;
   private LocalDate attached_at;
}
