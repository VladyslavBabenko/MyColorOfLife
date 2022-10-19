package com.github.vladyslavbabenko.mycoloroflife.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResetPasswordData {
    @NotEmpty(message = "Відсутній токен")
    private String token;
    @Size(min = 5, message = "Довжина пароля має бути від 5 до 30 символів")
    private String password;
    @Size(min = 5, message = "Довжина пароля має бути від 5 до 30 символів")
    private String passwordConfirm;
}