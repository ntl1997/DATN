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
-- USERS TABLE
CREATE TABLE Users (
    UserId INT PRIMARY KEY IDENTITY,
    FullName NVARCHAR(100),
    Email NVARCHAR(100) UNIQUE,
    Image NVARCHAR(MAX),
    PasswordHash NVARCHAR(255),
    Role NVARCHAR(20) CHECK (Role IN (N'Guest', N'Student', N'Admin')),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 2
-- COURSES TABLE
CREATE TABLE Courses (
    CourseId INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(MAX),
    Curriculum NVARCHAR(MAX),
    AuthorName NVARCHAR(100),
    Price DECIMAL(18,2),
    Discount DECIMAL(5,2),
    CourseImage NVARCHAR(MAX),
    Status NVARCHAR(20) CHECK (Status IN (N'Pending', N'Approved', N'Rejected')),
    CreatedBy INT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 3
-- COURSE OBJECTIVES
CREATE TABLE CourseObjectives (
    ObjectiveId INT PRIMARY KEY IDENTITY,
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    ObjectiveText NVARCHAR(500)
);
GO

-- 4
-- COURSE MODULES
CREATE TABLE CourseModules (
    ModuleId INT PRIMARY KEY IDENTITY,
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    ModuleTitle NVARCHAR(255),
    SortOrder INT
);
GO

-- 5
-- LECTURES
CREATE TABLE Lectures (
    LectureId INT PRIMARY KEY IDENTITY,
    ModuleId INT FOREIGN KEY REFERENCES CourseModules(ModuleId),
    LectureTitle NVARCHAR(255),
    Content NVARCHAR(MAX),
    VideoUrl NVARCHAR(500),
    SortOrder INT
);
GO

-- 6
-- ENROLLMENTS
CREATE TABLE Enrollments (
    EnrollmentId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    EnrolledAt DATETIME DEFAULT GETDATE()
);
GO

-- 7
-- CART
CREATE TABLE Cart (
    CartId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    AddedAt DATETIME DEFAULT GETDATE()
);
GO

-- 8
-- WISHLIST
CREATE TABLE Wishlist (
    WishlistId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    AddedAt DATETIME DEFAULT GETDATE()
);
GO

-- 9
-- REVIEWS
CREATE TABLE Reviews (
    ReviewId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    ReviewedAt DATETIME DEFAULT GETDATE()
);
GO

-- 10
-- ORDERS
CREATE TABLE Orders (
    OrderId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    TotalAmount DECIMAL(18,2),
    CouponCode NVARCHAR(50),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 11
-- ORDER DETAILS
CREATE TABLE OrderDetails (
    OrderDetailId INT PRIMARY KEY IDENTITY,
    OrderId INT FOREIGN KEY REFERENCES Orders(OrderId),
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    Price DECIMAL(18,2)
);
GO

-- 12
-- COUPONS
CREATE TABLE Coupons (
    CouponCode NVARCHAR(50) PRIMARY KEY,
    DiscountPercent DECIMAL(5,2),
    CreatedBy INT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1
);
GO

-- 13
-- CERTIFICATES
CREATE TABLE Certificates (
    CertificateId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    IssuedAt DATETIME DEFAULT GETDATE()
);
GO

-- 14
-- CATEGORIES
CREATE TABLE Categories (
    CategoryId INT PRIMARY KEY IDENTITY,
    Name NVARCHAR(100),
    ParentId INT FOREIGN KEY REFERENCES Categories(CategoryId)
);
GO

-- 15
-- COURSE - CATEGORY MAPPING
CREATE TABLE CourseCategories (
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    CategoryId INT FOREIGN KEY REFERENCES Categories(CategoryId),
    PRIMARY KEY (CourseId, CategoryId)
);
GO

-- 16
-- BLOG POSTS
CREATE TABLE BlogPosts (
    PostId INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Content NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    CreatedBy INT FOREIGN KEY REFERENCES Users(UserId)
);
GO

-- 17
-- CONTACT INFO
CREATE TABLE ContactInfo (
    ContactId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    Message NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 18
-- COURSE MATERIALS
CREATE TABLE CourseMaterials (
    MaterialId INT PRIMARY KEY IDENTITY,
    CourseId INT FOREIGN KEY REFERENCES Courses(CourseId),
    FileName NVARCHAR(255),
    FileUrl NVARCHAR(500),
    FileType NVARCHAR(50),
    UploadedAt DATETIME DEFAULT GETDATE()
);
GO

-- 19
-- NOTIFICATIONS
CREATE TABLE Notifications (
    NotificationId INT PRIMARY KEY IDENTITY,
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    Title NVARCHAR(255),
    Message NVARCHAR(MAX),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO


-- Insert sample users
INSERT INTO Users (FullName, Email, Image, PasswordHash, Role)
VALUES
(N'Nguyễn Văn A', 'a@example.com', 'image1.png', 'hashed_pw_1', N'Student'),
(N'Trần Thị B', 'b@example.com', 'image2.png', 'hashed_pw_2', N'Student'),
(N'Admin', 'admin@example.com', 'admin_image.png', 'hashed_pw_admin', N'Admin');
GO

-- Insert sample courses
INSERT INTO Courses (Title, Description, Curriculum, AuthorName, Price, Discount, CourseImage, Status, CreatedBy)
VALUES
(N'Khóa học Lập trình Python', N'Học lập trình Python từ cơ bản đến nâng cao.', N'Nội dung chi tiết...', N'Nguyễn Văn A', 500000, 0, 'image.png', N'Approved', 1),
(N'Thiết kế Web cơ bản', N'Hướng dẫn thiết kế website cho người mới.', N'Nội dung chi tiết...', N'Trần Thị B', 400000, 10, 'image.png', N'Pending', 2);
GO

-- Insert course objectives
INSERT INTO CourseObjectives (CourseId, ObjectiveText)
VALUES
(1, N'Hiểu cú pháp cơ bản của Python'),
(1, N'Biết cách xử lý file và dữ liệu'),
(2, N'Thiết kế giao diện bằng HTML/CSS');
GO

-- Insert course modules
INSERT INTO CourseModules (CourseId, ModuleTitle, SortOrder)
VALUES
(1, N'Giới thiệu Python', 1),
(1, N'Cấu trúc điều kiện và vòng lặp', 2),
(2, N'Cơ bản HTML', 1);
GO

-- Insert lectures
INSERT INTO Lectures (ModuleId, LectureTitle, Content, VideoUrl, SortOrder)
VALUES
(1, N'Giới thiệu ngôn ngữ Python', N'Nội dung bài giảng 1', 'https://video.example.com/python1', 1),
(2, N'Câu lệnh if-else', N'Nội dung bài giảng 2', 'https://video.example.com/python2', 1),
(3, N'Thẻ HTML cơ bản', N'Nội dung bài giảng 3', 'https://video.example.com/html1', 1);
GO

-- Insert enrollments
INSERT INTO Enrollments (UserId, CourseId)
VALUES
(1, 1),
(2, 2);
GO

-- Insert carts
INSERT INTO Cart (UserId, CourseId)
VALUES
(1, 2);
GO

-- Insert wishlists
INSERT INTO Wishlist (UserId, CourseId)
VALUES
(2, 1);
GO

-- Insert reviews
INSERT INTO Reviews (UserId, CourseId, Rating, Comment)
VALUES
(1, 1, 5, N'Khóa học rất hay'),
(2, 2, 4, N'Nội dung rõ ràng');
GO

-- Insert coupons
INSERT INTO Coupons (CouponCode, DiscountPercent, CreatedBy)
VALUES
('NEWUSER', 15, 3);
GO

-- Insert orders
INSERT INTO Orders (UserId, TotalAmount, CouponCode)
VALUES
(1, 425000, 'NEWUSER');
GO

-- Insert order details
INSERT INTO OrderDetails (OrderId, CourseId, Price)
VALUES
(1, 2, 425000);
GO

-- Insert certificates
INSERT INTO Certificates (UserId, CourseId)
VALUES
(1, 1);
GO

-- Insert categories
INSERT INTO Categories (Name, ParentId)
VALUES
(N'IT - CNTT', null), (N'Lập trình', 1), (N'Thiết kế', null);
GO

-- Insert course-categories
INSERT INTO CourseCategories (CourseId, CategoryId)
VALUES
(1, 1),
(2, 2);
GO

-- Insert blog posts
INSERT INTO BlogPosts (Title, Content, CreatedBy)
VALUES
(N'Lợi ích của học online', N'Học online giúp tiết kiệm thời gian...', 3);
GO

-- Insert contact info
INSERT INTO ContactInfo (UserId, Message)
VALUES
(1, N'Tôi cần hỗ trợ về đơn hàng');
GO

-- Insert course materials
INSERT INTO CourseMaterials (CourseId, FileName, FileUrl, FileType)
VALUES
(1, 'slides_intro.pdf', 'https://files.example.com/slide1.pdf', 'pdf');
GO

-- Insert notifications
INSERT INTO Notifications (UserId, Title, Message)
VALUES
(1, N'Xác nhận đăng ký khóa học', N'Bạn đã đăng ký thành công khóa học Python.');
GO
