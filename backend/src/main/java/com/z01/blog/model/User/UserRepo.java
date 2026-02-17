package com.z01.blog.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findById(long userId);

    @Query("select u from UserExtra u where u.accountId = :id")
    Optional<UserExtra> findExtraById(long id);

    Optional<UserEntity> findByAccountId(long accountId);

    Optional<UserEntity> findByLogin(String login);

    List<UserEntity> findTop20ByLoginStartingWithIgnoreCaseOrderByLogin(String login);

}
