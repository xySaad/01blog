package com.z01.blog.api.v1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.model.Post.PostModel;
import com.z01.blog.services.CloudinaryService;

@RestController
@RequestMapping("/api/v1/posts/{postId}/media")
public class MediaController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping
    public String uploadFile(
            @EntityAccess(mode = Mode.Write) PostModel post,
            @RequestBody byte[] file,
            @RequestHeader("X-File-Name") String fileName) {
        try {
            String decodedName = java.net.URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            return cloudinaryService.userUpload(post.id, decodedName, file);
        } catch (IOException e) {
            System.err.println("error upload file" + e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<String> getPostMedia(@EntityAccess(mode = Mode.Write) PostModel post) {
        return cloudinaryService.fetchPostMedia(post.id);
    }
}