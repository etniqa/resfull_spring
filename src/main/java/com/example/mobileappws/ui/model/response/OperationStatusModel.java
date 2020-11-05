package com.example.mobileappws.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationStatusModel {
    private String operationStatus;
    private String operationName;
}
