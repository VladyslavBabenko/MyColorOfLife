package com.github.vladyslavbabenko.mycoloroflife.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "t_course_title")
public class CourseTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotEmpty(message = "Назва не повинна бути порожньою")
    @Size(min = 1, max = 100, message = "Назва має бути від 1 до 100 символів")
    @Column(length = 100, nullable = false)
    private String title;

    @NotEmpty(message = "Опис не повинна бути порожньою")
    @Size(min = 1, max = 200, message = "Опис має бути від 1 до 200 символів")
    @Column(length = 200, nullable = false)
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_title_id")
    @ToString.Exclude
    private Set<Course> courses;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_title_id")
    @ToString.Exclude
    private Set<ActivationCode> activationCodes;

}