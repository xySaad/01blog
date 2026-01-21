package com.z01.blog.api.v1;

import com.z01.blog.controller.Auth;
import com.z01.blog.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class User extends Auth {
    @Autowired
    private UserModel.repo userRepo;

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
}