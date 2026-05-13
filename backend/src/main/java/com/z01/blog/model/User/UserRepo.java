package com.z01.blog.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findById(long userId);

    @Query("select u from UserExtra u where u.accountId = :id and deleted = false")
    Optional<UserExtra> findExtraById(long id);

    Optional<UserEntity> findByAccountIdAndDeletedFalse(long accountId);

    Optional<UserEntity> findByLoginAndDeletedFalse(String login);

    boolean existsByLogin(String login);

    List<UserEntity> findTop20ByLoginStartingWithIgnoreCaseAndDeletedFalseOrderByLogin(String login);

}
