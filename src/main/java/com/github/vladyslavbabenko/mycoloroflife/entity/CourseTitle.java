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

    @NotEmpty(message = "{validation.title.not.empty}")
    @Size(min = 1, max = 100, message = "{validation.title.length}")
    @Column(length = 100, nullable = false)
    private String title;

    @NotEmpty(message = "{validation.description.not.empty}")
    @Size(min = 1, max = 200, message = "{validation.description.length}")
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