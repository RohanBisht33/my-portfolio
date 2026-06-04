package com.portfolio.controller;

import com.portfolio.model.Project;
import com.portfolio.model.ProjectComment;
import com.portfolio.repository.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * REST API Controller for Project CRUD and engagement operations.
 * Maps directly from the old Node.js /api/projects routes.
 *
 * Endpoints:
 *   GET    /api/projects              — List all projects
 *   GET    /api/projects/{id}         — Get single project
 *   POST   /api/projects              — Create project (admin, multipart)
 *   POST   /api/projects/{id}/like    — Like a project
 *   POST   /api/projects/{id}/comment — Add comment to project
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {

    private final ProjectRepository projectRepository;

    // Directory for uploaded images (relative to working dir)
    private static final String UPLOAD_DIR = "uploads/projects/";

    public ProjectApiController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured) {
        List<Project> projects;
        if (Boolean.TRUE.equals(featured)) {
            projects = projectRepository.findByFeaturedTrue();
        } else if (category != null && !category.isBlank()) {
            projects = projectRepository.findByCategoryIgnoreCase(category);
        } else {
            projects = projectRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "status", defaultValue = "completed") String status,
            @RequestParam(value = "featured", defaultValue = "false") boolean featured,
            @RequestParam(value = "technologies", required = false) String technologies,
            @RequestParam(value = "githubUrl", required = false) String githubUrl,
            @RequestParam(value = "liveUrl", required = false) String liveUrl,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
        }

        Project project = new Project();
        project.setTitle(title.trim());
        project.setDescription(description != null ? description.trim() : "");
        project.setStatus(status);
        project.setFeatured(featured);
        project.setGithubUrl(githubUrl);
        project.setLiveUrl(liveUrl);

        // Parse comma-separated technologies
        if (technologies != null && !technologies.isBlank()) {
            List<String> techList = Arrays.stream(technologies.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            project.setTechStack(new ArrayList<>(techList));

            // Auto-detect category from tech stack
            String cat = detectCategory(techList);
            project.setCategory(cat);
        }

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = saveImage(image, UPLOAD_DIR);
                project.setImage(imagePath);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Image upload failed: " + e.getMessage()));
            }
        }

        Project saved = projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeProject(@PathVariable Long id) {
        return projectRepository.findById(id).map(project -> {
            project.setLikeCount(project.getLikeCount() + 1);
            projectRepository.save(project);
            return ResponseEntity.ok(Map.of("likeCount", project.getLikeCount()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentOnProject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String commentBody = body.get("body");
        if (commentBody == null || commentBody.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comment body required"));
        }

        return projectRepository.findById(id).map(project -> {
            ProjectComment comment = new ProjectComment();
            comment.setAuthor(body.getOrDefault("author", "Anonymous"));
            comment.setBody(commentBody.trim());
            comment.setProject(project);
            project.getComments().add(comment);
            projectRepository.save(project);
            return ResponseEntity.ok(Map.of(
                    "author", comment.getAuthor(),
                    "body", comment.getBody()
            ));
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

    private String detectCategory(List<String> techStack) {
        String joined = String.join(" ", techStack).toLowerCase();
        if (joined.contains("spring") || joined.contains("java") || joined.contains("api") || joined.contains("hibernate")) return "BACKEND";
        if (joined.contains("react") || joined.contains("vue") || joined.contains("angular") || joined.contains("html")) return "FRONTEND";
        if (joined.contains("python") || joined.contains("flask") || joined.contains("django")) return "PYTHON";
        if (joined.contains("c++") || joined.contains("opengl") || joined.contains("engine")) return "SYSTEMS";
        if (joined.contains("docker") || joined.contains("k8s") || joined.contains("aws") || joined.contains("ci/cd")) return "DEVOPS";
        if (joined.contains("c ") || joined.contains("socket") || joined.contains("embedded")) return "LOW-LEVEL";
        return "GENERAL";
    }
}
