package org.kafnetty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "_user", uniqueConstraints = {
        @UniqueConstraint(name = "UK_user_email", columnNames = {"email"}),
        @UniqueConstraint(name = "UK_user_nick_name", columnNames = {"nick_name"})})
public class User implements UserDetails {
    @Id
    private UUID id;
    private String email;
    private String fullName;
    @Column(name = "nick_name")
    private String nickName;
    private String password;
    private boolean sent = false;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
