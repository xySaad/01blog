package com.z01.blog.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    static class Provider {
        @Value("${CLOUDINARY_URL}")
        String CloudinaryUrl;

        @Bean
        Cloudinary Cloudinary() {
            return new Cloudinary(CloudinaryUrl);
        }
    }

    @Autowired
    Cloudinary cloudinary;

    public String userUpload(long postId, String fileName, byte[] file) throws IOException {
        var options = ObjectUtils.asMap(
                "overwrite", false,
                "public_id_prefix", String.valueOf(postId),
                "public_id", fileName);

        return cloudinary.uploader().upload(file, options).get("url").toString();
    }

    public List<String> fetchPostMedia(long postId) {
        var options = ObjectUtils.asMap(
                "type", "upload",
                "prefix", String.valueOf(postId));

        try {
            ApiResponse resp = cloudinary.api().resources(options);
            @SuppressWarnings("unchecked")
            var resources = (List<Map<String, Object>>) resp.get("resources");

            return resources.stream().map(resource -> (String) resource.get("url")).toList();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
