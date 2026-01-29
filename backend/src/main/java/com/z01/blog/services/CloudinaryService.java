package com.z01.blog.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
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

    // public String[] fetchPostMedia(long postId) {
    // var options = ObjectUtils.asMap(
    // "type", "upload",
    // "prefix", postId);
    // ApiResponse resp = cloudinary.api().resources(options);
    // System.out.println(resp.values().toString());
    // }
}
