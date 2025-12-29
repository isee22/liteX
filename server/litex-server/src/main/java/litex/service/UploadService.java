package litex.service;

import litejava.UploadedFile;
import litejava.plugin.ConfPlugin;
import litejava.util.Files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UploadService {
    
    /** 默认图片类型白名单 */
    public static final String DEFAULT_IMAGE_TYPES = "jpg,jpeg,png,gif,webp";
    
    /** 默认视频类型白名单 */
    public static final String DEFAULT_VIDEO_TYPES = "mp4,webm,mov,avi";
    
    /** 默认危险文件类型黑名单 */
    public static final String DEFAULT_BLOCKED_TYPES = 
        "exe,bat,cmd,sh,ps1,vbs,js,jar,msi,dll,scr,com,pif,application,gadget,hta,cpl,msc,inf,reg,php,asp,aspx,jsp";
    
    public ConfPlugin conf;
    
    // 配置字段
    public String location = "./uploads";
    public long maxFileSize = 5 * 1024 * 1024;      // 5MB
    public long maxRequestSize = 50 * 1024 * 1024;  // 50MB
    public Set<String> imageTypes = new HashSet<>();
    public Set<String> videoTypes = new HashSet<>();
    public Set<String> blockedTypes = new HashSet<>();
    
    public void init(ConfPlugin conf) {
        this.conf = conf;
        // 加载配置
        location = conf.getString("upload", "location", location);
        maxFileSize = parseSize(conf.getString("upload", "max-file-size", "5MB"));
        maxRequestSize = parseSize(conf.getString("upload", "max-request-size", "50MB"));
        
        // 加载类型配置
        imageTypes = parseTypes(conf.getString("upload", "image-types", DEFAULT_IMAGE_TYPES));
        videoTypes = parseTypes(conf.getString("upload", "video-types", DEFAULT_VIDEO_TYPES));
        blockedTypes = parseTypes(conf.getString("upload", "blocked-types", DEFAULT_BLOCKED_TYPES));
        
        // 确保目录存在
        Files.ensureDir(location);
    }
    
    /**
     * 解析大小字符串（如 5MB, 1GB）
     */
    public long parseSize(String size) {
        if (size == null || size.isEmpty()) return 0;
        size = size.toUpperCase().trim();
        long multiplier = 1;
        if (size.endsWith("KB")) {
            multiplier = 1024;
            size = size.substring(0, size.length() - 2);
        } else if (size.endsWith("MB")) {
            multiplier = 1024 * 1024;
            size = size.substring(0, size.length() - 2);
        } else if (size.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
            size = size.substring(0, size.length() - 2);
        }
        try {
            return Long.parseLong(size.trim()) * multiplier;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 解析逗号分隔的类型列表
     */
    public Set<String> parseTypes(String types) {
        Set<String> result = new HashSet<>();
        if (types != null && !types.isEmpty()) {
            for (String type : types.split(",")) {
                String t = type.trim().toLowerCase();
                if (!t.isEmpty()) {
                    result.add(t);
                }
            }
        }
        return result;
    }
    
    /**
     * 上传图片并返回 URL（白名单限制）
     */
    public String uploadImage(UploadedFile file) throws IOException {
        String ext = Files.getExtension(file.name);
        if (!imageTypes.contains(ext)) {
            throw new IllegalArgumentException("不支持的图片格式，仅支持: " + imageTypes);
        }
        if (file.size > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        return saveFile(file, "images");
    }
    
    /**
     * 上传视频并返回 URL（白名单限制）
     */
    public String uploadVideo(UploadedFile file) throws IOException {
        String ext = Files.getExtension(file.name);
        if (!videoTypes.contains(ext)) {
            throw new IllegalArgumentException("不支持的视频格式，仅支持: " + videoTypes);
        }
        if (file.size > maxRequestSize) {
            throw new IllegalArgumentException("视频大小超过限制");
        }
        return saveFile(file, "videos");
    }
    
    /**
     * 上传通用文件（黑名单限制）
     */
    public String uploadFile(UploadedFile file) throws IOException {
        String ext = Files.getExtension(file.name);
        if (blockedTypes.contains(ext)) {
            throw new IllegalArgumentException("不允许上传此类型文件: " + ext);
        }
        if (file.size > maxRequestSize) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        return saveFile(file, "files");
    }
    
    /**
     * 保存文件到指定子目录
     */
    public String saveFile(UploadedFile file, String subdir) throws IOException {
        String ext = Files.getExtension(file.name);
        String filename = ext.isEmpty() 
            ? UUID.randomUUID().toString() 
            : UUID.randomUUID().toString() + "." + ext;
        
        String dir = location + "/" + subdir;
        Files.ensureDir(dir);
        
        Path targetPath = Paths.get(dir, filename);
        file.saveTo(targetPath);
        
        return "/uploads/" + subdir + "/" + filename;
    }
    
    public boolean isImageType(String filename) {
        return imageTypes.contains(Files.getExtension(filename));
    }
    
    public boolean isVideoType(String filename) {
        return videoTypes.contains(Files.getExtension(filename));
    }
    
    public boolean isBlocked(String filename) {
        return blockedTypes.contains(Files.getExtension(filename));
    }
}
