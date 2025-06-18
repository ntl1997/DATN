package com.poly.viettutor.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public final class FileUtils {

    public static String saveImage(MultipartFile file, String folderPath) throws IOException {
        String uploadDir = new File(folderPath).getAbsolutePath() + File.separator;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // nếu thư mục không tồn tại thì tạo thư mục
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(uploadDir + fileName));
        return fileName;
    }

    public static void deleteImageIfExists(String fileName, String folderPath) {
        if (fileName == null || fileName.equals("user-icon.png")) {
            return; // Kiểm tra nếu ảnh là null hoặc là ảnh mặc định thì không xóa
        }
        String fullPath = new File(folderPath).getAbsolutePath() + File.separator + fileName;
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

}
