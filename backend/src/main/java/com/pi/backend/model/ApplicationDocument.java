package com.pi.backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(name="application_documents")
public class ApplicationDocument {
    @EmbeddedId
   private ApplicationDocumentId documentId;
}
