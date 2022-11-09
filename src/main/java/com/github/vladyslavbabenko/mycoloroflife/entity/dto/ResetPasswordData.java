package com.github.vladyslavbabenko.mycoloroflife.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordData {
    @NotEmpty(message = "{empty.token}")
    private String token;
    @Size(min = 5, message = "{user.password.length}")
    private String password;
    @Size(min = 5, message = "{user.password.length}")
    private String passwordConfirm;
}