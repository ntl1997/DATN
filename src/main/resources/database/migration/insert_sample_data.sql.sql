-- Roles
INSERT INTO Roles (Role) VALUES (N'ADMIN'), (N'TEACHER'), (N'STUDENT');

-- Users
INSERT INTO Users (FullName, Email, Image, PasswordHash, CreatedAt) VALUES
(N'Nguyễn Văn A', 'admin@example.com', 'default.jpg', 'hashed-password-1', GETDATE()),
(N'Trần Thị B', 'teacher@example.com', 'default.jpg', 'hashed-password-2', GETDATE()),
(N'Lê Văn C', 'student@example.com', 'default.jpg', 'hashed-password-3', GETDATE());

-- Gán quyền cho user
INSERT INTO UserRoles (UserId, RoleId)
SELECT U.UserId, R.RoleId FROM Users U, Roles R
WHERE (U.Email = 'admin@example.com' AND R.Role = N'ADMIN')
   OR (U.Email = 'teacher@example.com' AND R.Role = N'TEACHER')
   OR (U.Email = 'student@example.com' AND R.Role = N'STUDENT');

-- Categories
INSERT INTO Categories (Name, ParentId) VALUES
(N'Phát triển phần mềm', NULL),
(N'Kỹ năng mềm', NULL),
(N'Front-end', 1),
(N'Back-end', 1);

-- Courses
INSERT INTO Courses (Title, Description, Curriculum, AuthorName, Price, Discount, CourseImage, Status, CreatedBy, CreatedAt)
VALUES
(N'Lập trình Java Web', N'Khóa học lập trình Java với Spring Boot', N'20 giờ học với nhiều bài thực hành', N'Trần Thị B', 1200000, 10, 'java-course.jpg', N'Approved', 2, GETDATE()),
(N'Kỹ năng giao tiếp', N'Nâng cao kỹ năng giao tiếp trong công việc', N'10 bài học thực tiễn', N'Nguyễn Văn A', 500000, 0, 'communication.jpg', N'Approved', 1, GETDATE());

-- Gắn Course với Category
INSERT INTO CourseCategories (CourseId, CategoryId) VALUES
(1, 4), -- Java Web -> Back-end
(2, 2); -- Giao tiếp -> Kỹ năng mềm

-- Objectives
INSERT INTO CourseObjectives (CourseId, ObjectiveText) VALUES
(1, N'Biết cách tạo project Spring Boot cơ bản'),
(1, N'Biết sử dụng Thymeleaf để làm frontend'),
(1, N'Sử dụng Flyway để quản lý cơ sở dữ liệu');

-- Modules
INSERT INTO CourseModules (CourseId, ModuleTitle, SortOrder) VALUES
(1, N'Giới thiệu và chuẩn bị môi trường', 1),
(1, N'Cấu trúc project và Thymeleaf', 2);

-- Lectures
INSERT INTO Lectures (ModuleId, LectureTitle, Content, VideoUrl, SortOrder) VALUES
(1, N'Cài đặt JDK & IDE', N'Chi tiết hướng dẫn cài đặt', 'https://youtube.com/video1', 1),
(2, N'Tạo Controller và hiển thị giao diện', N'Kết nối giữa controller và view', 'https://youtube.com/video2', 1);

-- Enrollments
INSERT INTO Enrollments (UserId, CourseId, EnrolledAt) VALUES
(3, 1, GETDATE());

-- Wishlist
INSERT INTO Wishlist (UserId, CourseId, AddedAt) VALUES
(3, 2, GETDATE());

-- Cart
INSERT INTO Cart (UserId, CourseId, AddedAt) VALUES
(3, 2, GETDATE());

-- Reviews
INSERT INTO Reviews (UserId, CourseId, Rating, Comment, ReviewedAt) VALUES
(3, 1, 5, N'Khóa học rất bổ ích và dễ hiểu', GETDATE());

-- PaymentMethods
INSERT INTO PaymentMethods (PaymentMethod) VALUES
(N'Momo'), (N'VNPay'), (N'ZaloPay');

-- Orders
INSERT INTO Orders (UserId, PaymentMethodId, TotalAmount, CouponCode, CreatedAt) VALUES
(3, 1, 1080000, NULL, GETDATE()); -- 10% giảm

-- OrderDetails
INSERT INTO OrderDetails (OrderId, CourseId, Price) VALUES
(1, 1, 1080000);

-- Certificates
INSERT INTO Certificates (UserId, CourseId, IssuedAt) VALUES
(3, 1, GETDATE());

-- Coupons
INSERT INTO Coupons (CouponCode, DiscountPercent, CreatedBy, CreatedAt, IsActive) VALUES
('GIAM10', 10, 1, GETDATE(), 1),
('FREECOURSE', 100, 1, GETDATE(), 1);

-- BlogPosts
INSERT INTO BlogPosts (Title, Content, CreatedAt, CreatedBy) VALUES
(N'Học lập trình có khó không?', N'Học lập trình giống như học ngoại ngữ, cần kiên nhẫn và thực hành.', GETDATE(), 1);

-- ContactInfo
INSERT INTO ContactInfo (UserId, Message, CreatedAt) VALUES
(3, N'Em không đăng ký được khóa học', GETDATE());

-- Notifications
INSERT INTO Notifications (UserId, Title, Message, IsRead, CreatedAt) VALUES
(3, N'Chào mừng bạn đến với VietTutor', N'Chúng tôi rất vui khi bạn tham gia hệ thống', 0, GETDATE());

-- CourseMaterials
INSERT INTO CourseMaterials (CourseId, FileName, FileUrl, FileType, UploadedAt) VALUES
(1, N'Slide bài 1', 'https://example.com/slide1.pdf', 'PDF', GETDATE());
