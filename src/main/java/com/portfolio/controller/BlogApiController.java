package com.portfolio.controller;

import com.portfolio.model.Blog;
import com.portfolio.model.BlogComment;
import com.portfolio.repository.BlogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * REST API Controller for Blog CRUD and engagement operations.
 * Maps directly from the old Node.js /api/blogs routes.
 *
 * Endpoints:
 *   GET    /api/blogs              — List all blogs (with optional filters)
 *   GET    /api/blogs/{id}         — Get single blog (increments view count)
 *   POST   /api/blogs              — Create blog post (admin, multipart)
 *   PUT    /api/blogs/{id}/like    — Like a blog post
 *   POST   /api/blogs/{id}/comment — Add comment to blog
 */
@RestController
@RequestMapping("/api/blogs")
public class BlogApiController {

    private final BlogRepository blogRepository;
    private static final String UPLOAD_DIR = "uploads/blogs/";

    public BlogApiController(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs(
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Integer limit) {
        List<Blog> blogs;
        if (Boolean.TRUE.equals(featured)) {
            blogs = blogRepository.findByFeaturedTrue();
        } else if (limit != null && limit > 0) {
            blogs = blogRepository.findTop5ByOrderByDateDesc();
        } else {
            blogs = blogRepository.findAllByOrderByDateDesc();
        }
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlog(@PathVariable Long id) {
        return blogRepository.findById(id).map(blog -> {
            // Increment view count on read
            blog.setViews(blog.getViews() + 1);
            blogRepository.save(blog);
            return ResponseEntity.ok(blog);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "featured", defaultValue = "false") boolean featured,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
        }
        if (content == null || content.length() < 100) {
            return ResponseEntity.badRequest().body(Map.of("error", "Content must be at least 100 characters"));
        }

        Blog blog = new Blog();
        blog.setTitle(title.trim());
        blog.setContent(content.trim());
        blog.setFeatured(featured);

        // Parse comma-separated tags
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            blog.setTags(new ArrayList<>(tagList));
        }

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = saveImage(image, UPLOAD_DIR);
                blog.setImage(imagePath);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Image upload failed: " + e.getMessage()));
            }
        }

        Blog saved = blogRepository.save(blog);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<?> likeBlog(@PathVariable Long id) {
        return blogRepository.findById(id).map(blog -> {
            blog.setLikes(blog.getLikes() + 1);
            blogRepository.save(blog);
            return ResponseEntity.ok(Map.of("likes", blog.getLikes()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentOnBlog(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String commentContent = body.get("content");
        String author = body.getOrDefault("author", "Anonymous");

        if (commentContent == null || commentContent.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comment content required"));
        }

        return blogRepository.findById(id).map(blog -> {
            BlogComment comment = new BlogComment();
            comment.setAuthor(author.trim());
            comment.setContent(commentContent.trim());
            comment.setBlog(blog);
            blog.getComments().add(comment);
            Blog saved = blogRepository.save(blog);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Helpers ──────────────────────────────────────────────

    private String saveImage(MultipartFile file, String dir) throws IOException {
        Path uploadPath = Paths.get(dir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/" + dir + filename;
    }
}
