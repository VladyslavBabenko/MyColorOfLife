package com.github.vladyslavbabenko.mycoloroflife.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Course entity.
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

    @ManyToOne
    @JoinColumn(name = "course_title_id", nullable = false)
    @NotNull(message = "{validation.title.not.empty}")
    private CourseTitle courseTitle;

    @Column(nullable = false)
    @Max(value = 100, message = "{validation.page.max}")
    @Min(value = 1, message = "{validation.page.max}")
    @NotNull(message = "{validation.page.not.empty}")
    private Integer page;

    @Size(max = 100, message = "{validation.title.length}")
    @Column(length = 100)
    private String videoTitle;

    private String videoLink;

    @Size(max = 65535, message = "{validation.text.length}")
    private String text;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    private Set<CourseProgress> courseProgresses;
}