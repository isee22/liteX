package litex.service;

import litejava.App;
import litejava.UploadedFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UploadService {
    
    private static final Set<String> IMAGE_TYPES = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp"
    ));
    
    private static final Set<String> VIDEO_TYPES = new HashSet<>(Arrays.asList(
        "mp4", "webm", "mov", "avi"
    ));
    
    private static final long DEFAULT_IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long DEFAULT_VIDEO_MAX_SIZE = 50 * 1024 * 1024; // 50MB
    
    /**
     * 上传图片并返回 URL
     */
    public String uploadImage(UploadedFile file) throws IOException {
        if (!validateImageType(file.name)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }
        if (!validateFileSize(file.size, getImageMaxSize())) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        return saveFile(file, "images");
    }
    
    /**
     * 上传视频并返回 URL
     */
    public String uploadVideo(UploadedFile file) throws IOException {
        if (!validateVideoType(file.name)) {
            throw new IllegalArgumentException("不支持的视频格式");
        }
        if (!validateFileSize(file.size, getVideoMaxSize())) {
            throw new IllegalArgumentException("视频大小超过限制");
        }
        return saveFile(file, "videos");
    }
    
    /**
     * 保存文件到指定子目录
     */
    private String saveFile(UploadedFile file, String subdir) throws IOException {
        String uploadPath = getUploadPath();
        String ext = getExtension(file.name);
        String filename = UUID.randomUUID().toString() + "." + ext;
        
        Path targetPath = Paths.get(uploadPath, subdir, filename);
        Files.createDirectories(targetPath.getParent());
        file.saveTo(targetPath);
        
        return "/uploads/" + subdir + "/" + filename;
    }
    
    public boolean validateImageType(String filename) {
        if (filename == null || filename.isEmpty()) return false;
        return IMAGE_TYPES.contains(getExtension(filename).toLowerCase());
    }
    
    public boolean validateVideoType(String filename) {
        if (filename == null || filename.isEmpty()) return false;
        return VIDEO_TYPES.contains(getExtension(filename).toLowerCase());
    }
    
    /**
     * 验证文件类型（兼容旧方法）
     */
    public boolean validateFileType(String filename) {
        return validateImageType(filename);
    }
    
    /**
     * 验证文件大小（兼容旧方法）
     */
    public boolean validateFileSize(long size) {
        return validateFileSize(size, getImageMaxSize());
    }
    
    private boolean validateFileSize(long size, long maxSize) {
        return size > 0 && size <= maxSize;
    }
    
    private String getUploadPath() {
        if (App.instance != null && App.instance.conf != null) {
            return App.instance.conf.getString("upload", "path", "uploads");
        }
        return "uploads";
    }
    
    private long getImageMaxSize() {
        if (App.instance != null && App.instance.conf != null) {
            return App.instance.conf.getInt("upload", "maxSize", (int) DEFAULT_IMAGE_MAX_SIZE);
        }
        return DEFAULT_IMAGE_MAX_SIZE;
    }
    
    private long getVideoMaxSize() {
        if (App.instance != null && App.instance.conf != null) {
            return App.instance.conf.getInt("upload", "videoMaxSize", (int) DEFAULT_VIDEO_MAX_SIZE);
        }
        return DEFAULT_VIDEO_MAX_SIZE;
    }
    
    private long getMaxSize() {
        return getImageMaxSize();
    }
    
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
