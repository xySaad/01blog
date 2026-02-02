package com.z01.blog.api.v1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.z01.blog.annotation.Auth;
import com.z01.blog.model.Post.PostRepo;
import com.z01.blog.services.CloudinaryService;

@RestController
@RequestMapping("/api/v1/posts/{postId}/media")
public class MediaController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PostRepo postsRepo;

    @PostMapping
    public String uploadFile(
            @Auth.User long userId,
            @RequestBody byte[] file,
            @PathVariable long postId,
            @RequestHeader("X-File-Name") String fileName) {
        try {
            postsRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, false);

            String decodedName = java.net.URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            return cloudinaryService.userUpload(postId, decodedName, file);
        } catch (IOException e) {
            System.err.println("error upload file" + e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<String> getPostMedia(@Auth.User long userId, @PathVariable long postId) {
        postsRepo.findByIdAndDeletedFalse(postId).ensureAccess(userId, false);
        return cloudinaryService.fetchPostMedia(postId);
    }
}