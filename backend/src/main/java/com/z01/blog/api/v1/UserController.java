package com.z01.blog.api.v1;

import com.z01.blog.annotation.Auth;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserExtra;
import com.z01.blog.model.User.UserRepo;
import com.z01.blog.model.Post.PostExtra;
import com.z01.blog.model.Post.PostRepo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PostRepo postRepo;

    @DeleteMapping
    public void delete(@Auth.Account long accountId) {
        userRepo.deleteById(accountId);
    }

    @GetMapping("{userId}")
    public Optional<UserExtra> get(@PathVariable long userId) {
        return userRepo.findExtraById(userId);
    }

    @PostMapping
    public ResponseEntity<?> saveOrUpdate(@Auth.Account long accountId, @RequestBody UserEntity data) {
        // TODO: verify name length
        data.accountId = accountId;

        try {
            userRepo.save(data);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("{id}/posts")
    List<PostExtra> getUserPosts(@Auth.User long userId, @PathVariable long id) {
        if (id == userId)
            return postRepo.findAllByAccountAndDeletedFalse(userId);

        return postRepo.findAllByAccountAndDeletedFalseAndIsPublicTrue(userId);
    }
}