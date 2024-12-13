package com.ues.adminportal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ues.adminportal.entity.Site;
import com.ues.adminportal.entity.Status;
import com.ues.adminportal.entity.User;
import com.ues.adminportal.repository.SiteRepository;
import com.ues.adminportal.repository.StatusRepository;
import com.ues.adminportal.repository.UserRepository;
import com.ues.adminportal.service.SiteService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

     private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @GetMapping("/test")
    public String testEndpoint() {
        return "Controller is working!";
    }
    

    @GetMapping("/server-status")
    public ResponseEntity<List<Site>> getServerStatus() {
        logger.error("API HITTTTT::::::");
        List<Site> sites = siteRepository.findAll();
        logger.info("sites list:: "+sites.toString());
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/check-domain")
    public ResponseEntity<Map<String, Boolean>> checkDomain(@RequestParam String domain) {
        boolean exists = siteService.isDomainExists(domain);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/add-server")
    public ResponseEntity<Map<String, String>> addServer(
            @RequestParam("domain") String domain,
            @RequestParam("ipAddress") String ipAddress,
            @RequestParam("userId") int userId,
            @RequestParam("port") int port,
            @RequestParam("zipFile") MultipartFile zipFile) {

        //String baseDir = System.getProperty("user.home") + "/app/WEB_ROOT/";
        String baseDir = "/app/WEB_ROOT/";
        String uploadDir = baseDir;
        String originalFilename = zipFile.getOriginalFilename();
        String uploadDestination = uploadDir + "/" + originalFilename;

        
         Path path = Paths.get(originalFilename);
         String filenameWithoutExtension = path.getFileName().toString().replaceFirst("[.][^.]+$", "");
         String rootDestination = uploadDir + filenameWithoutExtension;

        try {
            // Ensure the WEB_ROOT directory exists
            File webRootDir = new File(baseDir);
            if (!webRootDir.exists()) {
                boolean webRootCreated = webRootDir.mkdirs();
                if (!webRootCreated) {
                    throw new IOException("Failed to create base directory: " + baseDir);
                }
            }

            // Ensure the domain-specific directory exists
            File domainDir = new File(uploadDir);
            if (!domainDir.exists()) {
                boolean domainDirCreated = domainDir.mkdirs();
                if (!domainDirCreated) {
                    throw new IOException("Failed to create domain directory: " + uploadDir);
                }
            }

            // Save the uploaded ZIP file
            File zipFileOnDisk = new File(uploadDestination);
            try (FileOutputStream fos = new FileOutputStream(zipFileOnDisk)) {
                fos.write(zipFile.getBytes());
            }

            
            unzipFile(uploadDestination, uploadDir);

            Files.delete(Paths.get(uploadDestination));

            
            Status runningStatus = statusRepository.findById(12100)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
            User user = userRepository.findById((long) userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

            
            Site site = new Site();
            site.setDomain(domain);
            site.setRoot(rootDestination);
            site.setIpAddress(ipAddress);
            site.setUser(user);
            site.setPort(port);
            site.setStatus(runningStatus);
            site.setCreatedAt(LocalDateTime.now());
            site.setTimestamp(LocalDateTime.now());
            siteRepository.save(site);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Website added successfully");
            response.put("domain", domain);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "An error occurred: " + e.getMessage()));
        }
    }

    private void unzipFile(String zipFilePath, String destDir) throws IOException {
        byte[] buffer = new byte[1024];
        Path destDirPath = Paths.get(destDir);
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newFile = destDirPath.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newFile);
                } else {
                    Files.createDirectories(newFile.getParent());
                    try (FileOutputStream fos = new FileOutputStream(newFile.toFile())) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }


    @PostMapping("/remove-server")
    public ResponseEntity<Map<String, String>> removeServer(@RequestBody Map<String, Long> request) {
        Long siteId = request.get("siteId");
        Map<String, String> response = new HashMap<>();
        if (siteId != null && siteRepository.existsById(siteId)) {
            siteRepository.deleteById(siteId);
            response.put("message", "Website removed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Website not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/stop-server")
    public ResponseEntity<Map<String, String>> stopServer(@RequestBody Map<String, Long> request) {
        Long siteId = request.get("siteId");
        Map<String, String> response = new HashMap<>();

        if (siteId != null && siteRepository.existsById(siteId)) {
            
            Optional<Site> optionalSite = siteRepository.findById(siteId);
            if (optionalSite.isPresent()) {
                Site site = optionalSite.get();

                
                Optional<Status> optionalStatus = statusRepository.findById(12101);
                if (optionalStatus.isPresent()) {
                    site.setStatus(optionalStatus.get());
                    siteRepository.save(site);
                    response.put("message", "Website stopped successfully");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Invalid status ID");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                response.put("message", "Website not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("message", "Website not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/start-server")
    public ResponseEntity<Map<String, String>> startServer(@RequestBody Map<String, Long> request) {
        Long siteId = request.get("siteId");
        Map<String, String> response = new HashMap<>();

        if (siteId != null && siteRepository.existsById(siteId)) {
            
            Optional<Site> optionalSite = siteRepository.findById(siteId);
            if (optionalSite.isPresent()) {
                Site site = optionalSite.get();

                
                Optional<Status> optionalStatus = statusRepository.findById(12100);
                if (optionalStatus.isPresent()) {
                    site.setStatus(optionalStatus.get());
                    siteRepository.save(site);
                    response.put("message", "Website started successfully");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Invalid status ID");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                response.put("message", "Website not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("message", "Website not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
}



}