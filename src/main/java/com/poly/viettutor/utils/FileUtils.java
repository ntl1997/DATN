package com.poly.viettutor.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

public final class FileUtils {

    public static String saveImage(MultipartFile file, String folderPath) throws IOException {
        // Đọc ảnh từ input stream (jpg, jpeg, png, gif)
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) { // Định dạng không hợp lệ trả về null
            throw new IllegalArgumentException("File không phải là ảnh hợp lệ");
        }

        Path uploadPath = Paths.get(folderPath); // Chỉ định đường dẫn thư mục lưu ảnh
        Files.createDirectories(uploadPath); // Kiểm tra và tạo thư mục nếu không tồn tại
        String fileName = UUID.randomUUID().toString() + ".jpg"; // Đặt tên mới và đuôi JPG
        Path filePath = uploadPath.resolve(fileName); // Tạo đường dẫn đầy đủ cho file
        ImageIO.write(originalImage, "jpg", filePath.toFile()); // Lưu ảnh dưới dạng JPG
        return fileName;
    }

    public static void deleteImageIfExists(String fileName, String folderPath) {
        if (fileName == null || fileName.equals("user-icon.png")) {
            return; // Không xóa ảnh mặc định hoặc null
        }
        Path filePath = Paths.get(folderPath).resolve(fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
