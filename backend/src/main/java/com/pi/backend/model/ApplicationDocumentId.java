package com.pi.backend.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ApplicationDocumentId implements Serializable {
    private Long documentId;
    private Long applicationId;
}
