package com.z01.blog.api.v1;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.Post.PostExtra;
import com.z01.blog.model.Post.PostRepo;
import com.z01.blog.model.RBAC.AccountRoleModel;
import com.z01.blog.model.RBAC.RoleModel;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserExtra;
import com.z01.blog.model.User.UserRepo;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private AccountRoleModel.repo accountRoleRepo;
    @Autowired
    private RoleModel.repo roleRepo;

    @DeleteMapping
    public void delete(@Auth.Account long accountId) {
        userRepo.deleteById(accountId);
    }

    @GetMapping("{userId}")
    public Optional<UserExtra> get(@PathVariable long userId) {
        return userRepo.findExtraById(userId);
    }

    @PostMapping
    public void saveOrUpdate(@Auth.Account long accountId, @RequestBody UserEntity data) {
        data.accountId = accountId;

        if (userRepo.existsByLogin(data.login)) {
            throw AppError.USERNAME_ALREADY_EXISTS.asException();
        }

        var defaultRole = roleRepo.findByName("default").get();

        if (!accountRoleRepo.existsById_AccountIdAndRole(accountId, defaultRole)) {
            var accRole = new AccountRoleModel(accountId, defaultRole);
            accountRoleRepo.save(accRole);
        }

        userRepo.save(data);
    }

    @GetMapping("{id}/posts")
    List<PostExtra> getUserPosts(@Auth.User long userId, @PathVariable long id) {
        if (id == userId)
            return postRepo.findAllByAccountAndDeletedFalse(userId);

        return postRepo.findAllByAccountAndDeletedFalseAndIsPublicTrue(userId);
    }

    @GetMapping("search/{query}")
    @RequiresPermission(scope = "v1:users:read", description = "view and search for users")
    public List<UserEntity> searchUsers(@PathVariable String query) {
        return userRepo.findTop20ByLoginStartingWithIgnoreCaseOrderByLogin(query);
    }
}