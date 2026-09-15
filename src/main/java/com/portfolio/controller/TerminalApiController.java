package com.portfolio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/terminal")
public class TerminalApiController {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @PostMapping("/execute")
    public ResponseEntity<?> executeCommand(@RequestBody Map<String, String> request) {
        String command = request.getOrDefault("command", "").trim();
        List<String> output = new ArrayList<>();
        int exitCode = 0;

        if (command.isEmpty()) {
            output.add("[error] Empty command received.");
            return ResponseEntity.ok(Map.of("output", output, "exitCode", 1));
        }

        String lower = command.toLowerCase();
        String[] rawParts = command.split("\\s+");
        String[] parts = lower.split("\\s+");
        String base = parts[0];

        if (base.equals("help")) {
            output.add("╔══════════════════════════════════════════════════════════╗");
            output.add("║      ROHAN BISHT — SDE EXECUTION RUNTIME V2.0            ║");
            output.add("╚══════════════════════════════════════════════════════════╝");
            output.add("  help                     — Display available console commands");
            output.add("  whoami                   — Display Rohan Bisht's profile");
            output.add("  skills                   — Print categorized technical skills");
            output.add("  ls                       — List deployed portfolio projects");
            output.add("  run <project>            — Execute project simulation & test suite");
            output.add("  education                — View academic credentials");
            output.add("  certs                    — View certifications and awards");
            output.add("  contact                  — Print verified contact channels");
            output.add("  status                   — Display JVM, database & cloud health");
            output.add("  login <user> <password>  — Authenticate administrator session");
            output.add("  logout                   — Exit administrator session");
            output.add("  add project              — Open project creation dialog");
            output.add("  clear                    — Reset terminal console");
        } else if (base.equals("whoami")) {
            output.add("Developer  : Rohan Bisht");
            output.add("Role       : Java Backend Developer / SDE");
            output.add("Education  : B.Tech in CSE — Dr. A.P.J. Abdul Kalam Technical University (CGPA: 7.8)");
            output.add("Affiliation: GDG Technical Team Member, MGMCOET");
            output.add("Focus      : Spring Boot 3.x, Multithreading, Pessimistic Locking, PostgreSQL, Azure Cloud");
        } else if (base.equals("skills")) {
            output.add("[Languages]          : Java (Core Java, Java 17+), Python, SQL, C");
            output.add("[Core Java]          : OOP, Multithreading & Concurrency, Collections, JDBC, Design Patterns");
            output.add("[Frameworks & DBs]   : Spring Boot 3.x, Spring MVC, Spring Data JPA, Hibernate ORM, JUnit 5, PostgreSQL, MySQL");
            output.add("[Cloud & DevOps]     : Microsoft Azure (App Service, ACR, Blob), Docker, GitHub Actions CI/CD, Maven, Linux, Git");
            output.add("[Architecture & DSA] : Concurrency Control, Pessimistic Locking, RESTful API Design, Command Pattern");
        } else if (base.equals("ls")) {
            output.add("Verified production projects in runtime:");
            output.add("  [1] nexstore       — NexStore E-Commerce (Spring Boot 3.x, Hibernate, Azure, Docker)");
            output.add("  [2] atm-simulator  — Enterprise ATM Simulator (Java 17, PostgreSQL, JDBC, Command Pattern)");
            output.add("Run via: run <nexstore | atm-simulator>");
        } else if (base.equals("run")) {
            if (parts.length < 2) {
                output.add("[error] Missing project identifier. Usage: run <nexstore | atm-simulator>");
                exitCode = 1;
            } else {
                String target = parts[1];
                if (target.contains("nexstore")) {
                    output.add("[exec] Initializing NexStore E-Commerce Runtime Environment...");
                    output.add("[spring] Starting Spring Boot 3.2.3 application on port 8080 (Azure App Service)");
                    output.add("[database] Connecting to PostgreSQL instance with Hibernate 6.x dialect");
                    output.add("[concurrency] Initializing 20 concurrent threads to test inventory updates...");
                    output.add("[locking] SELECT * FROM inventory WHERE item_id = ? FOR UPDATE [Pessimistic Write Lock active]");
                    output.add("[result] 20 simultaneous checkout requests processed: 0 race conditions, 100% transactional integrity.");
                    output.add("[ci/cd] Automated GitHub Actions build verified: Docker container deployed to Azure Container Registry.");
                    output.add("[status] NexStore backend ONLINE: https://nexstore.azurewebsites.net");
                } else if (target.contains("atm")) {
                    output.add("[exec] Initializing Enterprise ATM Simulator (Java 17)...");
                    output.add("[pattern] Registering Command Pattern dispatch handlers with dynamic O(1) registry...");
                    output.add("[jdbc] Establishing PostgreSQL relational persistence layer...");
                    output.add("[security] Testing Custom Exception Propagation across transactional boundaries...");
                    output.add("[junit] Running automated JUnit 5 concurrent transaction test suites...");
                    output.add("[junit] 28/28 tests PASSED [100% code stability]");
                    output.add("[repo] GitHub: https://github.com/RohanBisht33/atm-simulator");
                } else {
                    output.add("[error] Unknown project '" + target + "'. Available: nexstore, atm-simulator");
                    exitCode = 1;
                }
            }
        } else if (base.equals("education")) {
            output.add("1. Dr. A.P.J. Abdul Kalam Technical University (2024 – 2028)");
            output.add("   Degree: B.Tech — Computer Science & Engineering | CGPA: 7.8");
            output.add("2. Modern School (2022 – 2024)");
            output.add("   Senior Secondary (Class XII)");
        } else if (base.equals("certs")) {
            output.add("🏆 Code Clash — Google Developer Group, MGMCOET (1st Place 2026, 3rd Place 2024)");
            output.add("📜 Project Development Using Java for Beginners — Udemy (Hemanth Kumar Gurrala, 45.5 hrs)");
            output.add("📜 Java for Beginners — Learn all the Basics of Java — Udemy (Yassin Marco, 5.5 hrs)");
        } else if (base.equals("contact")) {
            output.add("Name     : Rohan Bisht");
            output.add("Location : Ghaziabad, Uttar Pradesh, India");
            output.add("Phone    : +91-9354156168");
            output.add("Email    : bishtrohan33@gmail.com");
            output.add("LinkedIn : https://linkedin.com/in/rohan-bisht-0735771a1");
            output.add("GitHub   : https://github.com/RohanBisht33");
        } else if (base.equals("status")) {
            output.add("System Status        : ONLINE (Healthy)");
            output.add("JVM Environment      : Java 17 LTS (Spring Boot 3.2.3)");
            output.add("Database             : PostgreSQL relational mode active");
            output.add("WebSocket            : Active at /ws/terminal");
            output.add("Container Registry   : Azure Container Registry (ACR) connected");
        } else if (base.equals("login")) {
            if (rawParts.length < 3) {
                output.add("[auth] Usage: login <username> <password>");
                output.add("[auth] Example: login imrb rb@123");
                exitCode = 1;
            } else {
                String username = rawParts[1];
                String password = rawParts[2];
                if ("imrb".equalsIgnoreCase(username) && "rb@123".equals(password)) {
                    String token = "adm_" + UUID.randomUUID().toString().substring(0, 8);
                    output.add("[auth] ══════════════════════════════════════════════");
                    output.add("[auth] ACCESS GRANTED. Welcome Rohan Bisht (@sys.rb)");
                    output.add("[auth] Administrator session authenticated.");
                    output.add("[auth] 'Add Project' controls are now active across the site.");
                    output.add("[auth] ══════════════════════════════════════════════");
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("output", output);
                    resp.put("exitCode", 0);
                    resp.put("authenticated", true);
                    resp.put("token", token);
                    resp.put("username", "imrb");
                    return ResponseEntity.ok(resp);
                } else {
                    output.add("[auth] ACCESS DENIED: Invalid credentials.");
                    exitCode = 1;
                }
            }
        } else if (base.equals("logout")) {
            output.add("[auth] Administrator session terminated.");
            output.add("[auth] 'Add Project' controls locked and hidden.");
            Map<String, Object> resp = new HashMap<>();
            resp.put("output", output);
            resp.put("exitCode", 0);
            resp.put("loggedOut", true);
            return ResponseEntity.ok(resp);
        } else if (command.equalsIgnoreCase("add project") || command.equalsIgnoreCase("add-project")) {
            output.add("[action] Opening Add Project dialog...");
            Map<String, Object> resp = new HashMap<>();
            resp.put("output", output);
            resp.put("exitCode", 0);
            resp.put("openAddProjectModal", true);
            return ResponseEntity.ok(resp);
        } else {
            output.add("[error] Unknown command: " + base);
            output.add("Type 'help' to inspect available commands.");
            exitCode = 1;
        }

        return ResponseEntity.ok(Map.of("output", output, "exitCode", exitCode));
    }
}
