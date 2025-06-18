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
    Skill NVARCHAR(100),
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
-- COURSES TABLE
CREATE TABLE Courses (
    CourseId BIGINT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(MAX),
    Curriculum NVARCHAR(MAX),
    AuthorName NVARCHAR(100),
    Price DECIMAL(18,2),
    Discount DECIMAL(5,2),
    CourseImage NVARCHAR(MAX),
    Status NVARCHAR(20) CHECK (Status IN (N'Pending', N'Approved', N'Rejected')),
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE()
);
GO

-- 5
-- COURSE OBJECTIVES
CREATE TABLE CourseObjectives (
    ObjectiveId BIGINT PRIMARY KEY IDENTITY,
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    ObjectiveText NVARCHAR(500)
);
GO

-- 6
-- COURSE MODULES
CREATE TABLE CourseModules (
    ModuleId BIGINT PRIMARY KEY IDENTITY,
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    ModuleTitle NVARCHAR(255),
    SortOrder INT
);
GO

-- 7
-- LECTURES
CREATE TABLE Lectures (
    LectureId BIGINT PRIMARY KEY IDENTITY,
    ModuleId BIGINT FOREIGN KEY REFERENCES CourseModules(ModuleId),
    LectureTitle NVARCHAR(255),
    Content NVARCHAR(MAX),
    VideoUrl NVARCHAR(500),
    SortOrder INT
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

-- 9
-- CART
CREATE TABLE Cart (
    CartId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    AddedAt DATETIME DEFAULT GETDATE()
);
GO

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
    CreatedAt DATETIME DEFAULT GETDATE(),
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId)
);
GO

-- 20
-- CONTACT INFO
CREATE TABLE ContactInfo (
    ContactId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    Message NVARCHAR(MAX),
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

-- 1. Roles
INSERT INTO Roles (Role) VALUES 
(N'ADMIN'),
(N'INSTRUCTOR'),
(N'STUDENT');

-- 2. Users (mật khẩu mặc định: 123456)
INSERT INTO Users (FullName, Email, Image, PasswordHash)
VALUES 
(N'Admin User', N'admin@viettutor.com', null, N'$2a$12$V2XUN.FhiVRyIpev2m6.MOUdKlRveFMlC3C6kfYT/Y7ZrClNep55W'),
(N'John Instructor', N'john@viettutor.com', null, N'$2a$12$CvyLQybDyPrkgBjMwVjsj./KYP806nPneY1A7VU/PaRoSL0jkleZS'),
(N'Jane Student', N'jane@student.com', null, N'$2a$12$5KxQ27DY6NeQB0B115wa8eOXDzJmrejMdWFK6LkPsniklvy2JqTOy');

-- 3. UserRoles
INSERT INTO UserRoles (RoleId, UserId)
VALUES 
(1, 1), -- Admin
(2, 2), -- Instructor
(3, 3); -- Student

-- 4. Categories
INSERT INTO Categories (Name, ParentId)
VALUES 
(N'Programming', NULL),         -- ID 1
(N'Web Development', 1),        -- ID 2
(N'Data Science', 1);           -- ID 3

-- 5. Courses
INSERT INTO Courses (Title, Description, Curriculum, AuthorName, Price, Discount, CourseImage, Status, CreatedBy)
VALUES 
(N'Java for Beginners', N'Learn Java from scratch', N'Basics, OOP, Collections', N'John Instructor', 100.00, 20.00, N'/images/java-course.jpg', N'Approved', 2), -- ID 1
(N'Python Data Analysis', N'Use Python for data science', N'Pandas, NumPy, Matplotlib', N'John Instructor', 120.00, 15.00, N'/images/python-course.jpg', N'Approved', 2); -- ID 2

-- 6. CourseCategories
INSERT INTO CourseCategories (CourseId, CategoryId)
VALUES 
(1, 2), -- Java -> Web Dev
(2, 3); -- Python -> Data Science

-- 7. Enrollments
INSERT INTO Enrollments (UserId, CourseId)
VALUES 
(3, 1),
(3, 2);

-- 8. Cart
INSERT INTO Cart (UserId, CourseId)
VALUES 
(3, 2);

-- 9. Wishlist
INSERT INTO Wishlist (UserId, CourseId)
VALUES 
(3, 1);

-- 10. Reviews
INSERT INTO Reviews (UserId, CourseId, Rating, Comment)
VALUES 
(3, 1, 5, N'Excellent Java course!'),
(3, 2, 4, N'Great content, could use more exercises');

-- 11. PaymentMethods
INSERT INTO PaymentMethods (PaymentMethod)
VALUES 
(N'Credit Card'),
(N'PayPal'),
(N'Bank Transfer');

-- 12. Orders
INSERT INTO Orders (UserId, PaymentMethodId, TotalAmount, CouponCode)
VALUES 
(3, 1, 85.00, NULL); -- ID 1

-- 13. OrderDetails
INSERT INTO OrderDetails (OrderId, CourseId, Price)
VALUES 
(1, 1, 80.00),
(1, 2, 5.00);

-- 14. Coupons
INSERT INTO Coupons (CouponCode, DiscountPercent, CreatedBy)
VALUES 
(N'WELCOME10', 10.00, 1);

-- 15. Certificates
INSERT INTO Certificates (UserId, CourseId)
VALUES 
(3, 1);

-- 16. CourseObjectives
INSERT INTO CourseObjectives (CourseId, ObjectiveText)
VALUES 
(1, N'Understand basic Java syntax'),
(1, N'Build OOP Java applications');

-- 17. BlogPosts
INSERT INTO BlogPosts (Title, Content, CreatedBy)
VALUES 
(N'5 Tips to Learn Programming Faster', N'Practice, practice, practice...', 2);

-- 18. ContactInfo
INSERT INTO ContactInfo (UserId, Message)
VALUES 
(3, N'I need help accessing my course');

-- 19. Notifications
INSERT INTO Notifications (UserId, Title, Message)
VALUES 
(3, N'Enrollment Successful', N'You have successfully enrolled in Java for Beginners');
