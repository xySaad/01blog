package com.z01.blog.api.v1;

import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.Post;
import com.z01.blog.model.UserModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class User extends AuthGuard {
    @Autowired
    private UserModel.repo userRepo;
    @Autowired
    private Post.repo postRepo;

    @GetMapping
    public ResponseEntity<UserModel> get(@CookieValue("jwt") String jwt) {
        System.out.println(getAuthId(jwt));
        return userRepo.findById(getAuthId(jwt))
                .map((u) -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public void delete(@CookieValue("jwt") String jwt) {
        userRepo.deleteById(getAuthId(jwt));
    }

    @PostMapping
    public ResponseEntity<?> saveOrUpdate(@CookieValue("jwt") String jwt, @RequestBody UserModel data) {
        // TODO: verify name length
        data.accountId = getAuthId(jwt);

        try {
            userRepo.save(data);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("{id}/posts")
    List<Post> getUserPosts(@CookieValue("jwt") String jwt, @PathVariable long id) {
        long accountId = this.getUserId(jwt);

        if (id == accountId)
            return postRepo.findAllByAccountAndDeletedFalse(accountId);

        return postRepo.findAllByAccountAndDeletedFalseAndIsPublicTrue(accountId);
    }
}