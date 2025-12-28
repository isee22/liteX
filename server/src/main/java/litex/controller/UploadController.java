package litex.controller;

import litex.Service;
import litejava.Context;
import litejava.Routes;
import litejava.UploadedFile;
import litejava.util.Maps;

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
