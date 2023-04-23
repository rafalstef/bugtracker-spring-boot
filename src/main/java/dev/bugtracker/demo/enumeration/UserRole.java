package dev.bugtracker.demo.enumeration;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;

public enum UserRole {
    DEVELOPER,
    TESTER,
    MANAGER,
    ADMIN;

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.name()));
    }
}
