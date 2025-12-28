package litex.controller;

import litex.Service;
import litejava.Context;
import litejava.Routes;
import litejava.UploadedFile;

import java.util.Map;

public class UploadController {
    
    public Routes routes() {
        return new Routes()
            .post("/api/upload/image", this::uploadImage)
            .post("/api/upload/video", this::uploadVideo)
            .end();
    }
    
    void uploadImage(Context ctx) {
        UploadedFile file = ctx.file("file");
        
        if (file == null) {
            ctx.status(400).json(Map.of("error", "请选择文件"));
            return;
        }
        
        try {
            String url = Service.upload.uploadImage(file);
            ctx.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "上传失败"));
        }
    }
    
    void uploadVideo(Context ctx) {
        UploadedFile file = ctx.file("file");
        
        if (file == null) {
            ctx.status(400).json(Map.of("error", "请选择文件"));
            return;
        }
        
        try {
            String url = Service.upload.uploadVideo(file);
            ctx.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "上传失败"));
        }
    }
}
