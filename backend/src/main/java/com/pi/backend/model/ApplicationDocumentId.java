package com.pi.backend.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ApplicationDocumentId implements Serializable {
    private Long documentId;
    private Long applicationId;
}
