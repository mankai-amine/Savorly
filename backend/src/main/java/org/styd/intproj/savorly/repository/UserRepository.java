package org.styd.intproj.savorly.repository;

import org.styd.intproj.savorly.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
