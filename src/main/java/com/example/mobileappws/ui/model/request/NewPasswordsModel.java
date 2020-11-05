package com.example.mobileappws.ui.model.request;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordsModel {
    // password`s fields must be identical
    // Bean Validation (not less 4 symbols)
    @Size(min = 2)
    String newPassword;
    @Size(min = 2)
    String verifyPassword;

    public boolean checkEqualityOfFields() {

        return this.newPassword.equals(this.verifyPassword);
    }
}
