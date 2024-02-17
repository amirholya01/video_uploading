package com.amir.video_uploading.services;

import com.amir.video_uploading.entities.Video;
import com.amir.video_uploading.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;
    private FileValidationService fileValidationService;

    @Value(value = "${file.path}")
    private String uploadPath;

    public Video uploadVideo(String title, String description, MultipartFile file) throws IOException {

        // Check if the upload directory exists, if not create it
        Path uploadDirectory = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.exists(uploadDirectory)){
            Files.createDirectories(uploadDirectory);
        }

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = fileValidationService.getFileExtension(originalFilename);
        if (!fileValidationService.isValidVideoFormat(fileExtension)){
            throw new IllegalArgumentException("Invalid video format " + fileExtension);
        }

        String filename = System.currentTimeMillis() + "-" + originalFilename;

        // Copy the file to the upload directory
        Path targetLocation = uploadDirectory.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Create Video entity
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setSize(file.getSize());
        video.setContentType(file.getContentType());
        video.setVideoUrl(filename);
        video.setCreated(new Date());
        video.setModified(new Date());

        return videoRepository.save(video);
    }
    public Resource loadVideoAsResource(String filename) throws MalformedURLException {
        Path filePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if(resource.exists()){
            return resource;
        }else {
            throw new RuntimeException("video not found " + filename);
        }
    }
    public List<Video> getAllVideos(){
        return videoRepository.findAll();
    }

    public List<Video> search(String keyword){
        return videoRepository.findFirstByTitleContainsOrDescriptionContains(keyword);
    }
    public Video getVideoById(Long id){
        Optional<Video> video = videoRepository.findById(id);
        return video.isPresent() ? video.get() : null;
    }
    public void deleteAllVideos(){
        FileSystemUtils.deleteRecursively(Paths.get(uploadPath).toFile());
    }

    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException exception) {
            throw new RuntimeException("Could not initialize upload folder: " + exception.getMessage());
        }
    }
}
