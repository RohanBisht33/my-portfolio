package com.portfolio.config;

import com.portfolio.model.Project;
import com.portfolio.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes database with Rohan Bisht's resume projects if the project repository is empty.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initProjects(ProjectRepository projectRepository) {
        return args -> {
            boolean addedNew = false;
            if (projectRepository.findAll().stream().noneMatch(p -> p.getTitle().contains("NexStore"))) {
                // Project 1: NexStore — E-Commerce
                Project nexstore = new Project();
                nexstore.setTitle("NexStore - E-Commerce");
                nexstore.setCategory("E-COMMERCE & CLOUD");
                nexstore.setDescription(
                        "Engineered a containerized Java e-commerce backend using Spring Boot 3.x and Hibernate ORM, " +
                        "and neutralized multi-threaded race conditions via database-level Pessimistic Write Locking, " +
                        "guaranteeing strict transactional integrity across simultaneous inventory updates. " +
                        "Automated a full CI/CD pipeline using GitHub Actions and Maven to deploy immutable Docker images " +
                        "to Azure Container Registry, enabling zero-downtime hot restarts on Azure App Service."
                );
                nexstore.setTechStack(List.of(
                        "Java", "Spring Boot 3.x", "Spring Data JPA", "Hibernate ORM",
                        "PostgreSQL", "Microsoft Azure", "Docker", "GitHub Actions", "Maven"
                ));
                nexstore.setStatus("completed");
                nexstore.setFeatured(true);
                nexstore.setLiveUrl("https://nexstore.azurewebsites.net");
                nexstore.setGithubUrl("https://github.com/RohanBisht33");
                nexstore.setLikeCount(0);
                nexstore.setImage("/images/project-default.png");
                projectRepository.save(nexstore);
                addedNew = true;
            }

            if (projectRepository.findAll().stream().noneMatch(p -> p.getTitle().contains("ATM Simulator"))) {
                // Project 2: Enterprise ATM Simulator
                Project atmSimulator = new Project();
                atmSimulator.setTitle("Enterprise ATM Simulator");
                atmSimulator.setCategory("BANKING & SYSTEMS");
                atmSimulator.setDescription(
                        "Architected a highly decoupled, Object-Oriented banking state simulator with PostgreSQL relational " +
                        "persistence via JDBC, using a Command Pattern dispatch architecture backed by an O(1) dynamic " +
                        "registry for open-closed extensibility. Implemented explicit Custom Exception Propagation across " +
                        "all transactional boundaries and enforced code stability through automated JUnit 5 test suites " +
                        "targeting edge cases in concurrent transaction flows."
                );
                atmSimulator.setTechStack(List.of(
                        "Java 17", "PostgreSQL", "JDBC", "Maven", "JUnit 5", "OOP", "Command Pattern", "Design Patterns"
                ));
                atmSimulator.setStatus("completed");
                atmSimulator.setFeatured(true);
                atmSimulator.setGithubUrl("https://github.com/RohanBisht33/atm-simulator");
                atmSimulator.setLikeCount(0);
                atmSimulator.setImage("/images/project-default.png");
                projectRepository.save(atmSimulator);
                addedNew = true;
            }
        };
    }
}
