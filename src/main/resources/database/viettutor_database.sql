-- CƠ SỞ DỮ LIỆU HỢP NHẤT viettutor

-- Drop the database 'viettutor'
-- Connect to the 'master' database to run this snippet
USE master
GO
-- Uncomment the ALTER DATABASE statement below to set the database to SINGLE_USER mode if the drop database command fails because the database is in use.
-- ALTER DATABASE viettutor SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
-- Drop the database if it exists
IF EXISTS (
    SELECT [name]
        FROM sys.databases
        WHERE [name] = N'viettutor'
)
DROP DATABASE viettutor
GO

-- Create a new database called 'viettutor'
-- Connect to the 'master' database to run this snippet
USE master
GO
-- Create the new database if it does not exist already
IF NOT EXISTS (
    SELECT [name]
        FROM sys.databases
        WHERE [name] = N'viettutor'
)
CREATE DATABASE viettutor
GO

USE viettutor
GO

-- 1
-- ROLES TABLE
CREATE TABLE Roles (
    RoleId BIGINT PRIMARY KEY IDENTITY,
    Role NVARCHAR(10),
);
GO

-- 2
-- USERS TABLE
CREATE TABLE Users (
    UserId BIGINT PRIMARY KEY IDENTITY,
    FullName NVARCHAR(100),
    Email NVARCHAR(100) UNIQUE,
    Image NVARCHAR(MAX),
    PasswordHash NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE(),
    PhoneNumber NVARCHAR(20),
    Occupation NVARCHAR(100),
    Biography NVARCHAR(MAX)
);
GO

-- 3
-- ROLE - USER MAPPING
CREATE TABLE UserRoles (
    RoleId BIGINT FOREIGN KEY REFERENCES Roles(RoleId),
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    PRIMARY KEY (RoleId, UserId)
);
GO


-- 4
-- COURSES TABLE (HỢP NHẤT TOÀN BỘ)
CREATE TABLE Courses (
    CourseId BIGINT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(255),
    Overview NVARCHAR(MAX),
    Price DECIMAL(18,2),
    Discount DECIMAL(5,2),
    CourseImage NVARCHAR(255),
    Status NVARCHAR(20) CHECK (Status IN (N'Pending', N'Approved', N'Rejected')),
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    
    -- ✅ Các cột bổ sung
    HasCertificate BIT DEFAULT 0,
    Language NVARCHAR(50),
    SkillLevel NVARCHAR(20),
    demoVideoUrl NVARCHAR(1000)
);
GO


-- -- 5
-- -- COURSE OBJECTIVES
-- CREATE TABLE CourseObjectives (
--     ObjectiveId BIGINT PRIMARY KEY IDENTITY,
--     CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
--     ObjectiveText NVARCHAR(500)
-- );
-- GO

-- 6
-- COURSE MODULES
CREATE TABLE CourseModules (
    ModuleId BIGINT PRIMARY KEY IDENTITY,
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    ModuleTitle NVARCHAR(255),
    SortOrder INT,
);
GO

-- 7
-- LECTURES TABLE (HỢP NHẤT)
CREATE TABLE Lectures (
    LectureId BIGINT PRIMARY KEY IDENTITY,
    ModuleId BIGINT FOREIGN KEY REFERENCES CourseModules(ModuleId),
    LectureTitle NVARCHAR(255),
    Content NVARCHAR(MAX),
    VideoUrl NVARCHAR(500),
    SortOrder INT,
    
    -- ✅ Cột bổ sung
    duration INT -- Đơn vị: phút
);
GO


-- 8
-- ENROLLMENTS
CREATE TABLE Enrollments (
    EnrollmentId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    EnrolledAt DATETIME DEFAULT GETDATE()
);
GO

-- -- 9
-- -- CART
-- CREATE TABLE Cart (
--     CartId BIGINT PRIMARY KEY IDENTITY,
--     UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
--     CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
--     AddedAt DATETIME DEFAULT GETDATE()
-- );
-- GO

-- 10
-- WISHLIST
CREATE TABLE Wishlist (
    WishlistId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    AddedAt DATETIME DEFAULT GETDATE()
);
GO

-- 11
-- REVIEWS
CREATE TABLE Reviews (
    ReviewId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    ReviewedAt DATETIME DEFAULT GETDATE()
);
GO

-- 12
-- PAYMENTMETHODS TABLE
CREATE TABLE PaymentMethods (
    PaymentMethodId BIGINT PRIMARY KEY IDENTITY,
    PaymentMethod NVARCHAR(20),
);
GO

-- 13
-- ORDERS
CREATE TABLE Orders (
    OrderId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    PaymentMethodId BIGINT FOREIGN KEY REFERENCES PaymentMethods(PaymentMethodId),
    TotalAmount DECIMAL(18,2),
    CouponCode NVARCHAR(50),
    Status NVARCHAR(10),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 14
-- ORDER DETAILS
CREATE TABLE OrderDetails (
    OrderDetailId BIGINT PRIMARY KEY IDENTITY,
    OrderId BIGINT FOREIGN KEY REFERENCES Orders(OrderId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    Price DECIMAL(18,2)
);
GO

-- 15
-- COUPONS
CREATE TABLE Coupons (
    CouponCode NVARCHAR(50) PRIMARY KEY,
    DiscountPercent DECIMAL(5,2),
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1
);
GO

-- 16
-- CERTIFICATES
CREATE TABLE Certificates (
    CertificateId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    IssuedAt DATETIME DEFAULT GETDATE()
);
GO

-- 17
-- CATEGORIES
CREATE TABLE Categories (
    CategoryId BIGINT PRIMARY KEY IDENTITY,
    Name NVARCHAR(100),
    ImageUrl NVARCHAR(255),
    Level INT,
    ParentId BIGINT FOREIGN KEY REFERENCES Categories(CategoryId)
);
GO

-- 18
-- COURSE - CATEGORY MAPPING
CREATE TABLE CourseCategories (
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    CategoryId BIGINT FOREIGN KEY REFERENCES Categories(CategoryId),
    PRIMARY KEY (CourseId, CategoryId)
);
GO

-- 19
-- BLOG POSTS
CREATE TABLE BlogPosts (
    PostId BIGINT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Content NVARCHAR(MAX),
    imageBlog NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId)
);
GO

-- 20
-- CONTACT INFO
CREATE TABLE ContactInfo (
    ContactId BIGINT PRIMARY KEY IDENTITY,
    Name NVARCHAR(50),
    Email NVARCHAR(100),
    PhoneNumber NVARCHAR(15),
    Message NVARCHAR(MAX),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 21
-- COURSE MATERIALS
CREATE TABLE CourseMaterials (
    MaterialId BIGINT PRIMARY KEY IDENTITY,
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    FileName NVARCHAR(255),
    FileUrl NVARCHAR(500),
    FileType NVARCHAR(50),
    UploadedAt DATETIME DEFAULT GETDATE()
);
GO

-- 22
-- NOTIFICATIONS
CREATE TABLE Notifications (
    NotificationId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    Title NVARCHAR(255),
    Message NVARCHAR(MAX),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- DỮ LIỆU MẪU CHO viettutor
-- 1. Roles (độc lập)
INSERT INTO Roles (Role) VALUES 
(N'ADMIN'), (N'INSTRUCTOR'), (N'STUDENT');

-- 2. Users (phụ thuộc Roles)
-- (mật khẩu mặc định: 123456)
INSERT INTO Users (FullName, Email, PasswordHash, CreatedAt, Image, Biography, Occupation, PhoneNumber) VALUES 
(N'Admin User', N'admin@viettutor.com', N'$2a$12$V2XUN.FhiVRyIpev2m6.MOUdKlRveFMlC3C6kfYT/Y7ZrClNep55W', GETDATE(), N'https://short.com.vn/6tMs', N'An experienced software engineer passionate about building scalable applications.', N'Software Engineer', N'0901234567'),
(N'John Instructor', N'john@viettutor.com', N'$2a$12$CvyLQybDyPrkgBjMwVjsj./KYP806nPneY1A7VU/PaRoSL0jkleZS', GETDATE(), N'https://short.com.vn/U9Ow', N'Marketing specialist with a focus on digital campaigns and brand growth.', N'Digital Marketer', N'0902345678'),
(N'Jane Student', N'jane@student.com', N'$2a$12$5KxQ27DY6NeQB0B115wa8eOXDzJmrejMdWFK6LkPsniklvy2JqTOy', GETDATE(), N'https://short.com.vn/nKzp', N'A dedicated teacher who loves helping students achieve their goals.', N'High School Teacher', N'0903456789');


-- 3. UserRoles (phụ thuộc Roles + Users)
INSERT INTO UserRoles (RoleId, UserId) VALUES 
(1, 1), -- Admin
(2, 2), -- Instructor
(3, 3); -- Student


-- 4. Categories (độc lập)
INSERT INTO Categories (Name, ParentId, ImageUrl, Level) VALUES 
(N'Programming', NULL, N'https://bitly.li/vYIB', 1),
(N'Web Development', 1, N'https://bitly.li/AwvR', 2),
(N'Data Science', 1, N'https://bitly.li/vYIB', 2);

-- 5. PaymentMethods (độc lập)
INSERT INTO PaymentMethods (PaymentMethod) VALUES 
(N'Credit Card'), (N'PayPal'), (N'Bank Transfer');

-- 6. Courses (phụ thuộc Users)
INSERT INTO Courses (
    Title, Description, Overview, Price, Discount, CourseImage, Status, CreatedBy, CreatedAt, 
    UpdatedAt, demoVideoUrl, HasCertificate, Language, SkillLevel
) VALUES 
(N'Khóa học Lập trình Python', N'Học lập trình Python từ cơ bản đến nâng cao.', N'Đây là nội dung chi tiết', 500000, 0, N'image.png', N'Approved', 1, GETDATE(), GETDATE(), N'https://youtu.be/kISRDWXC6-A?si=2JVJqTg6029m3J-P', 1, N'Tiếng Việt', N'Cơ bản'),
(N'Thiết kế Web cơ bản', N'Hướng dẫn thiết kế website cho người mới.', N'Đây là nội dung chi tiết', 400000, 10, N'image.png', N'Approved', 2, GETDATE(), GETDATE(), N'https://youtu.be/TvUNY2VfyX8?si=Pvm8n3LvYVYLhOzJ', 1, N'Tiếng Anh', N'Trung cấp'),
(N'Khóa học Lập trình Robotics', N'Học lập trình Spike từ cơ bản đến nâng cao.', N'Đây là nội dung chi tiết', 500000, 0, N'https://short.com.vn/08Wa', N'Approved', 1, GETDATE(), GETDATE(), NULL, 0, N'Tiếng Việt', N'Phổ thông'),
(N'Khóa học Lập trình Python Cơ Bản 2', N'Học lập trình Python từ cơ bản đến nâng cao.', N'Đây là nội dung chi tiết', 500000, 0, N'https://s.pro.vn/epcy', N'Approved', 1, GETDATE(), GETDATE(), N'https://youtu.be/NZj6LI5a9vc?si=0JOLcPjuaSgmNrJb', 1, N'English', N'Nâng cao');

-- 7. CourseCategories (phụ thuộc Courses + Categories)
INSERT INTO CourseCategories (CourseId, CategoryId) VALUES 
(1, 2), (2, 3);

-- 8. CourseModules (phụ thuộc Courses)
INSERT INTO CourseModules (CourseId, ModuleTitle, SortOrder) VALUES 
(1, N'Giới thiệu Python', 1),
(1, N'Cấu trúc điều kiện và vòng lặp', 2),
(2, N'Cơ bản HTML', 1);

-- 9. Lectures (phụ thuộc CourseModules)
INSERT INTO Lectures (
    ModuleId, LectureTitle, Content, VideoUrl, SortOrder, duration
) VALUES 
(1, N'Giới thiệu ngôn ngữ Python', N'Nội dung bài giảng 1', N'https://www.youtube.com/embed/K7ZKTjmZeWw', 1, 30),
(2, N'Câu lệnh if-else', N'Nội dung bài giảng 2', N'https://www.youtube.com/embed/W0kMn7dYNGo', 1, 18),
(3, N'Thẻ HTML cơ bản', N'Nội dung bài giảng 3', N'https://www.youtube.com/embed/PN9EUufNkWA', 1, 14);

-- -- 10. CourseObjectives (phụ thuộc Courses)
-- INSERT INTO CourseObjectives (CourseId, ObjectiveText) VALUES 
-- (1, N'Understand basic Java syntax'),
-- (1, N'Build OOP Java applications');


-- 11. CourseMaterials (phụ thuộc Courses)
INSERT INTO CourseMaterials (
    CourseId, FileName, FileUrl, FileType, UploadedAt
) VALUES 
(1, N'slides_intro.pdf', N'https://files.example.com/slide1.pdf', N'pdf', GETDATE());

-- 12. Coupons (phụ thuộc Users)
INSERT INTO Coupons (CouponCode, DiscountPercent, CreatedBy) VALUES 
(N'WELCOME10', 10.00, 1);

-- 13. Orders (phụ thuộc Users + PaymentMethods)
INSERT INTO Orders (UserId, PaymentMethodId, TotalAmount, CouponCode, Status)
VALUES (3, 1, 85.00, NULL, 'paid'); -- ID 1

-- 14. OrderDetails (phụ thuộc Orders + Courses)
INSERT INTO OrderDetails (OrderId, CourseId, Price)
VALUES (1, 1, 80.00), (1, 2, 5.00);

-- 15. Enrollments (phụ thuộc Users + Courses)
INSERT INTO Enrollments (UserId, CourseId) VALUES 
(3, 1), (3, 2);

-- 16. Wishlist (phụ thuộc Users + Courses)
INSERT INTO Wishlist (UserId, CourseId) VALUES (3, 1);

-- -- 17. Cart (phụ thuộc Users + Courses)
-- INSERT INTO Cart (UserId, CourseId) VALUES (3, 2);

-- 18. Reviews (phụ thuộc Users + Courses)
INSERT INTO Reviews (UserId, CourseId, Rating, Comment) VALUES 
(2, 1, 5, N'Excellent Java course!'),
(3, 2, 4, N'Great content, could use more exercises');

-- 19. Certificates (phụ thuộc Users + Courses)
INSERT INTO Certificates (UserId, CourseId) VALUES (3, 1);

-- 20. BlogPosts (phụ thuộc Users)
INSERT INTO BlogPosts (Title, Content, imageBlog, CreatedBy) VALUES 
(N'5 Tips to Learn Programming Faster', N'Practice, practice, practice...', N'/assets/images/blog/blog-card-01.jpg', 1),
(N'Trở Thành Lập Trình Viên Giỏi Trong 6 Tháng', N'Hãy bắt đầu với nền tảng vững chắc và dự án thực tế.', N'/assets/images/blog/blog-card-02.jpg', 2),
(N'Những Lỗi Thường Gặp Khi Học Lập Trình', N'Tìm hiểu và tránh các lỗi phổ biến giúp bạn tiến bộ nhanh hơn.', N'/assets/images/blog/blog-card-03.jpg', 1),
(N'Học Java Có Khó Không? Hướng Dẫn Cho Người Mới Bắt Đầu', N'Java là ngôn ngữ mạnh mẽ nhưng không hề khó nếu bạn học đúng cách.', N'/assets/images/blog/blog-card-04.jpg', 2),
(N'Frontend vs Backend: Nên Học Gì Trước?', N'Bài viết giúp bạn phân biệt rõ giữa frontend và backend, cũng như lộ trình học phù hợp.', N'/assets/images/blog/blog-card-05.jpg', 2),
(N'5 Kênh YouTube Học Lập Trình Chất Lượng Miễn Phí', N'Cùng khám phá những kênh YouTube giúp bạn tự học lập trình hiệu quả.', N'/assets/images/blog/blog-card-06.jpg', 1);

-- 21. ContactInfo (phụ thuộc Users)
INSERT INTO ContactInfo (Name, Email, PhoneNumber, Message) VALUES 
(N'Trần Thị B', N'tranthiB@gmail.com', N'0987654321', N'Tôi muốn được tư vấn về khóa học Lập trình Python.');

-- 22. Notifications (phụ thuộc Users)
INSERT INTO Notifications (UserId, Title, Message) VALUES 
(3, N'Enrollment Successful', N'You have successfully enrolled in Java for Beginners');
GO

