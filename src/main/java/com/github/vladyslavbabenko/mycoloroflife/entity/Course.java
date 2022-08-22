package com.github.vladyslavbabenko.mycoloroflife.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Article entity.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "t_course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;
    @NotEmpty(message = "Назва не повинна бути порожньою")
    @Size(min = 1, max = 100, message = "Назва має бути від 1 до 100 символів")
    @Column(length = 100, nullable = false)
    private String courseTitle;
    @Column(nullable = false)
    @Max(value = 100, message = "Сторінка має бути від 1 до 100")
    @Min(value = 1, message = "Сторінка має бути від 1 до 100")
    @NotNull(message = "Вкажіть сторінку")
    private Integer page;
    @Size(max = 100, message = "Назва має бути до 100 символів")
    @Column(length = 100)
    private String videoTitle;
    private String videoLink;
    @Size(max = 65535, message = "Текст має бути до 65535 символів")
    private String text;
}