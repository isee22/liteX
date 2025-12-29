package litex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import litex.Service;
import litejava.Context;
import litejava.Routes;
import litejava.UploadedFile;
import litejava.util.Maps;

@Tag(name = "上传", description = "文件上传接口")
public class UploadController {
    
    public Routes routes() {
        return new Routes(this)
            .post("/api/upload/image", this::uploadImage, "uploadImage")
            .post("/api/upload/video", this::uploadVideo, "uploadVideo")
            .end();
    }
    
    @Operation(summary = "上传图片", description = "上传图片文件，支持jpg/png/gif/webp，最大5MB")
    void uploadImage(Context ctx) {
        UploadedFile file = ctx.file("file");
        
        if (file == null) {
            ctx.fail("请选择文件");
            return;
        }
        
        try {
            String url = Service.upload.uploadImage(file);
            ctx.ok(Maps.of("url", url));
        } catch (IllegalArgumentException e) {
            ctx.fail(e.getMessage());
        } catch (Exception e) {
            ctx.fail("上传失败");
        }
    }
    
    @Operation(summary = "上传视频", description = "上传视频文件，支持mp4/webm/mov/avi，最大50MB")
    void uploadVideo(Context ctx) {
        UploadedFile file = ctx.file("file");
        
        if (file == null) {
            ctx.fail("请选择文件");
            return;
        }
        
        try {
            String url = Service.upload.uploadVideo(file);
            ctx.ok(Maps.of("url", url));
        } catch (IllegalArgumentException e) {
            ctx.fail(e.getMessage());
        } catch (Exception e) {
            ctx.fail("上传失败");
        }
    }
}
