package com.pi.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="documents")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String  fileName;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String filePath;
    private  String fileHash;


}
