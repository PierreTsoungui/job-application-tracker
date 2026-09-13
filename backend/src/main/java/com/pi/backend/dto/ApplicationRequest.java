package com.pi.backend.dto;
import com.pi.backend.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationRequest {
    
    @NotBlank(message = "Company name is required")
    private String company;
    @NotBlank(message = "Position is required")
    private String position;
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    @NotNull(message = "Date applied is required")
    private LocalDate dateApplied;
    
}
