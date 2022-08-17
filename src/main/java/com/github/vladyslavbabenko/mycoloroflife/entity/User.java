package com.github.vladyslavbabenko.mycoloroflife.entity;

import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * User entity.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "t_user")
public class User implements UserDetails, OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;
    @Column(nullable = false)
    @NotEmpty(message = "Вкажіть ім'я")
    @Size(min = 2, max = 30, message = "Ім'я користувача має бути від 2 до 30 символів")
    private String username;
    @Column(nullable = false)
    @Email(message = "Пошта має бути валідною")
    @NotEmpty(message = "Вкажіть пошту")
    private String email;
    @Size(min = 5, message = "Довжина пароля має бути від 5 до 30 символів")
    private String password;
    @Transient
    private String passwordConfirm;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Transient
    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private Set<Article> articles;
    @Transient
    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private Set<Event> events;
    @Column(nullable = false)
    //Assume that the user will register via standard form by default
    private UserRegistrationType registrationType = UserRegistrationType.REGISTRATION_FORM;

    @Transient
    private Map<String, Object> attributes;

    public User(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String getName() {
        return username;
    }
}