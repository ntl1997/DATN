-- CƠ SỞ DỮ LIỆU HỢP NHẤT viettutor

-- Drop the database 'viettutor'
-- Connect to the 'master' database to run this snippet
--USE master
--GO
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
CREATE TABLE Roles
(
    RoleId BIGINT PRIMARY KEY IDENTITY,
    Role NVARCHAR(10),
);
GO

-- 2
-- USERS TABLE
CREATE TABLE Users
(
    UserId BIGINT PRIMARY KEY IDENTITY,
    FullName NVARCHAR(100),
    Email NVARCHAR(100) UNIQUE,
    Image NVARCHAR(MAX),
    PasswordHash NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE(),
    PhoneNumber NVARCHAR(20),
    Occupation NVARCHAR(100),
    Biography NVARCHAR(MAX),
    Status BIT DEFAULT 1,
);
GO

-- 3
-- ROLE - USER MAPPING
CREATE TABLE UserRoles
(
    RoleId BIGINT FOREIGN KEY REFERENCES Roles(RoleId),
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    PRIMARY KEY (RoleId, UserId)
);
GO


-- 4
-- COURSES TABLE (HỢP NHẤT TOÀN BỘ)
CREATE TABLE Courses
(
    CourseId BIGINT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(255),
    Overview NVARCHAR(MAX),
    Price DECIMAL(18,2),
    Discount DECIMAL(5,2),
    CourseImage NVARCHAR(255),
    Status NVARCHAR(20) CHECK (Status IN (N'publish', N'pending', N'draft', N'hidden')) DEFAULT N'draft',
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME DEFAULT GETDATE(),
    Note NVARCHAR(255),
    ApprovedBy BIGINT FOREIGN KEY REFERENCES Users(UserId),
    ApprovedAt DATETIME DEFAULT GETDATE(),
    -- ✅ Các cột bổ sung
    HasCertificate BIT DEFAULT 0,
    Language NVARCHAR(50),
    SkillLevel NVARCHAR(20),
    demoVideoUrl NVARCHAR(1000)
);
GO

-- 6
-- COURSE MODULES
CREATE TABLE CourseModules
(
    ModuleId BIGINT PRIMARY KEY IDENTITY,
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    ModuleTitle NVARCHAR(255),
    SortOrder INT,
);
GO

-- 7
-- LECTURES TABLE (HỢP NHẤT)
CREATE TABLE Lectures
(
    LectureId BIGINT PRIMARY KEY IDENTITY,
    ModuleId BIGINT FOREIGN KEY REFERENCES CourseModules(ModuleId),
    LectureTitle NVARCHAR(255),
    Content NVARCHAR(MAX),
    VideoUrl NVARCHAR(500),
    SortOrder INT,

    -- ✅ Cột bổ sung
    duration INT
    -- Đơn vị: phút
);
GO


-- 8
-- ENROLLMENTS
CREATE TABLE Enrollments
(
    EnrollmentId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    EnrolledAt DATETIME DEFAULT GETDATE(),
    EnrolledBy BIGINT FOREIGN KEY REFERENCES Users(UserId) DEFAULT NULL
);
GO

-- 10
-- WISHLIST
CREATE TABLE Wishlist
(
    WishlistId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    AddedAt DATETIME DEFAULT GETDATE()
);
GO

-- 11
-- REVIEWS
CREATE TABLE Reviews
(
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
CREATE TABLE PaymentMethods
(
    PaymentMethodId BIGINT PRIMARY KEY IDENTITY,
    PaymentMethod NVARCHAR(20),
);
GO

-- 13
-- ORDERS
CREATE TABLE Orders
(
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
CREATE TABLE OrderDetails
(
    OrderDetailId BIGINT PRIMARY KEY IDENTITY,
    OrderId BIGINT FOREIGN KEY REFERENCES Orders(OrderId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    Price DECIMAL(18,2)
);
GO

-- 15
-- COUPONS
CREATE TABLE Coupons
(
    CouponCode NVARCHAR(50) PRIMARY KEY,
    DiscountPercent DECIMAL(5,2),
    CreatedBy BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1
);
GO

-- 16
-- CERTIFICATES
CREATE TABLE Certificates
(
    CertificateId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    IssuedAt DATETIME DEFAULT GETDATE()
);
GO

-- 17
-- CATEGORIES
CREATE TABLE Categories
(
    CategoryId BIGINT PRIMARY KEY IDENTITY,
    Name NVARCHAR(100),
    ImageUrl NVARCHAR(255),
    Level INT,
    ParentId BIGINT FOREIGN KEY REFERENCES Categories(CategoryId)
);
GO

-- 18
-- COURSE - CATEGORY MAPPING
CREATE TABLE CourseCategories
(
    CourseId BIGINT FOREIGN KEY REFERENCES Courses(CourseId),
    CategoryId BIGINT FOREIGN KEY REFERENCES Categories(CategoryId),
    PRIMARY KEY (CourseId, CategoryId)
);
GO

-- 19
-- BLOG POSTS
CREATE TABLE BlogPosts
(
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
CREATE TABLE ContactInfo
(
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
CREATE TABLE CourseMaterials
(
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
CREATE TABLE Notifications
(
    NotificationId BIGINT PRIMARY KEY IDENTITY,
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    Title NVARCHAR(255),
    Message NVARCHAR(MAX),
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);
GO
-- 23. Quizzes
CREATE TABLE Quizzes
(
    QuizId BIGINT PRIMARY KEY IDENTITY,
    -- Mã định danh tự tăng cho mỗi bài quiz
    ModuleId BIGINT FOREIGN KEY REFERENCES CourseModules(ModuleId),
    -- Gắn quiz với một module cụ thể
    Title NVARCHAR(255),
    -- Tiêu đề bài quiz (VD: Quiz bài 1)
    TotalScore INT,
    -- Tổng điểm tối đa đạt được
    TimeLimit INT,
    -- Giới hạn thời gian làm bài (phút)
    QuizType NVARCHAR(20) DEFAULT 'regular',
    -- Kiểu Quiz (thông thường, assignment)
    CreatedAt DATETIME DEFAULT GETDATE()
    -- Ngày tạo quiz
);
-- 24. Questions
CREATE TABLE Questions
(
    QuestionId BIGINT PRIMARY KEY IDENTITY,
    -- Mã định danh câu hỏi
    QuizId BIGINT FOREIGN KEY REFERENCES Quizzes(QuizId),
    -- Gắn câu hỏi với một quiz
    QuestionText NVARCHAR(MAX),
    -- Nội dung câu hỏi
    Score INT DEFAULT 1
    -- Điểm cho câu hỏi này (mặc định 1 điểm)
);

-- 25. Options
CREATE TABLE Options
(
    OptionId BIGINT PRIMARY KEY IDENTITY,
    -- Mã định danh đáp án
    QuestionId BIGINT FOREIGN KEY REFERENCES Questions(QuestionId),-- Gắn đáp án với câu hỏi
    OptionText NVARCHAR(MAX),
    -- Nội dung đáp án
    IsCorrect BIT
    -- Đáp án này có đúng không (1 = đúng, 0 = sai)
);

-- 26. QuizSubmissions
CREATE TABLE QuizSubmissions
(
    SubmissionId BIGINT PRIMARY KEY IDENTITY,
    -- Mã định danh lần nộp quiz
    QuizId BIGINT FOREIGN KEY REFERENCES Quizzes(QuizId),
    -- Gắn lần nộp với quiz
    UserId BIGINT FOREIGN KEY REFERENCES Users(UserId),
    -- Người làm bài
    SubmittedAt DATETIME DEFAULT GETDATE(),
    -- Thời điểm nộp
    Score INT
    -- Tổng điểm đạt được
);

-- 27. QuizAnswers (tùy chọn)
CREATE TABLE QuizAnswers
(
    AnswerId BIGINT PRIMARY KEY IDENTITY,
    -- Mã định danh câu trả lời
    SubmissionId BIGINT FOREIGN KEY REFERENCES QuizSubmissions(SubmissionId),
    -- Gắn với lần nộp
    QuestionId BIGINT,
    -- ID của câu hỏi
    SelectedOptionId BIGINT,
    -- Đáp án học sinh chọn
    IsCorrect BIT
    -- Đáp án đó có đúng không (1 = đúng)
);

-- DỮ LIỆU MẪU CHO viettutor
-- 1. Roles (độc lập)
INSERT INTO Roles
    (Role)
VALUES
    (N'ADMIN'),
    (N'INSTRUCTOR'),
    (N'STUDENT');

-- 2. Users (phụ thuộc Roles)
-- (mật khẩu mặc định: 123456)
INSERT INTO Users
    (FullName, Email, PasswordHash, CreatedAt, Image, Biography, Occupation, PhoneNumber)
VALUES
    (N'Admin User', N'admin@viettutor.com', N'$2a$12$V2XUN.FhiVRyIpev2m6.MOUdKlRveFMlC3C6kfYT/Y7ZrClNep55W', GETDATE(), N'https://short.com.vn/6tMs', N'An experienced software engineer passionate about building scalable applications.', N'Software Engineer', N'0901234567'),
    (N'John Instructor', N'john@viettutor.com', N'$2a$12$CvyLQybDyPrkgBjMwVjsj./KYP806nPneY1A7VU/PaRoSL0jkleZS', GETDATE(), N'https://short.com.vn/U9Ow', N'Marketing specialist with a focus on digital campaigns and brand growth.', N'Digital Marketer', N'0902345678'),
    (N'Jane Student', N'jane@student.com', N'$2a$12$5KxQ27DY6NeQB0B115wa8eOXDzJmrejMdWFK6LkPsniklvy2JqTOy', GETDATE(), N'https://short.com.vn/nKzp', N'A dedicated teacher who loves helping students achieve their goals.', N'High School Teacher', N'0903456789');

-- 3. UserRoles (phụ thuộc Roles + Users)
INSERT INTO UserRoles
    (RoleId, UserId)
VALUES
    (1, 1),
    -- Admin
    (2, 2),
    -- Instructor
    (3, 3);
-- Student

-- 4. Categories (độc lập)
INSERT INTO Categories
    (Name, ParentId, ImageUrl, Level)
VALUES
    (N'Programming', NULL, N'https://bitly.li/vYIB', 1),
    (N'Web Development', 1, N'https://bitly.li/AwvR', 2),
    (N'Data Science', 1, N'https://bitly.li/vYIB', 2);

-- 5. PaymentMethods (độc lập)
INSERT INTO PaymentMethods
    (PaymentMethod)
VALUES
    (N'Credit Card'),
    (N'PayPal'),
    (N'Bank Transfer');

-- 6. Courses (phụ thuộc Users)
INSERT INTO Courses
    (
    Title, Description, Overview, Price, Discount, CourseImage, Status, CreatedBy, CreatedAt,
    UpdatedAt, demoVideoUrl, HasCertificate, Language, SkillLevel
    )
VALUES
    (N'Khóa học Lập trình Python', N'Học lập trình Python từ cơ bản đến nâng cao.', N'Đây là nội dung chi tiết', 0, 0, N'image.png', N'publish', 1, GETDATE(), GETDATE(), N'https://youtu.be/kISRDWXC6-A?si=2JVJqTg6029m3J-P', 1, N'Tiếng Việt', N'Cơ bản'),
    (N'Thiết kế Web cơ bản', N'Hướng dẫn thiết kế website cho người mới.', N'Đây là nội dung chi tiết', 0, 0, N'image.png', N'publish', 2, GETDATE(), GETDATE(), N'https://youtu.be/TvUNY2VfyX8?si=Pvm8n3LvYVYLhOzJ', 1, N'Tiếng Anh', N'Trung cấp'),
    (N'Khóa học Lập trình Robotics', N'Học lập trình Spike từ cơ bản đến nâng cao.', N'Đây là nội dung chi tiết', 0, 0, N'https://short.com.vn/08Wa', N'publish', 1, GETDATE(), GETDATE(), NULL, 0, N'Tiếng Việt', N'Phổ thông'),
    (N'Khóa học Lập trình Python Cơ Bản 2', N'Học lập trình Python từ cơ bản đến nâng cao.', N'Đây là nội dung chi tiết', 0, 0, N'https://s.pro.vn/epcy', N'publish', 1, GETDATE(), GETDATE(), N'https://youtu.be/NZj6LI5a9vc?si=0JOLcPjuaSgmNrJb', 1, N'English', N'Nâng cao'),
    (
        N'Phân Tích Dữ Liệu Cho Người Mới Bắt Đầu', N'Học cách xử lý và phân tích dữ liệu với Python và Excel.', N'Khóa học này giúp bạn hiểu các khái niệm cơ bản về phân tích dữ liệu, thực hành với các công cụ như Pandas và biểu đồ trực quan.', 0, 0, N'https://img-cdn.com/data-analysis.jpg', N'publish', 2, GETDATE(), GETDATE(),
        N'https://www.youtube.com/watch?v=iMbCKOQnLMg',
        1,
        N'Tiếng Việt',
        N'Cơ bản'
)
-- 7. CourseCategories (phụ thuộc Courses + Categories)
INSERT INTO CourseCategories
    (CourseId, CategoryId)
VALUES
    (1, 2),
    (2, 3);

-- 8. CourseModules (phụ thuộc Courses)
INSERT INTO CourseModules
    (CourseId, ModuleTitle, SortOrder)
VALUES
    (1, N'Giới thiệu Python', 1),
    (1, N'Cấu trúc điều kiện và vòng lặp', 2),
    (2, N'Cơ bản HTML', 1),
    (5, N'Giới thiệu về Phân tích Dữ liệu', 1),
    (1, N'Hàm và Thư viện trong Python', 3),
    (2, N'CSS cơ bản', 2),
    (5, N'Công cụ trực quan hóa dữ liệu', 2);

-- 9. Lectures (phụ thuộc CourseModules)
INSERT INTO Lectures
    (
    ModuleId, LectureTitle, Content, VideoUrl, SortOrder, duration
    )
VALUES
    (1, N'Giới thiệu ngôn ngữ Python', N'Nội dung bài giảng 1', N'https://www.youtube.com/embed/K7ZKTjmZeWw', 1, 30),
    (1, N'Biến và Kiểu dữ liệu', N'Nội dung bài giảng về biến và kiểu dữ liệu', N'https://www.youtube.com/embed/rfscVS0vtbw', 2, 28),
    (1, N'Hello World và print()', N'Cách in ra màn hình dòng chữ đầu tiên', N'https://www.youtube.com/embed/hxGX2m2xw0A', 3, 15),
    (1, N'Tổng quan kiểu dữ liệu nâng cao', N'List, Tuple, Dictionary, Set', N'https://www.youtube.com/embed/R-HLU9Fl5ug', 4, 25),
    (2, N'Vòng lặp for trong Python', N'Nội dung về vòng lặp for', N'https://www.youtube.com/embed/6iF8Xb7Z3wQ', 2, 26),
    (2, N'Câu lệnh if-else', N'Nội dung bài giảng 2', N'https://www.youtube.com/embed/W0kMn7dYNGo', 1, 18),
    (2, N'Vòng lặp while', N'Sử dụng vòng lặp while hiệu quả', N'https://www.youtube.com/embed/6iF8Xb7Z3wQ', 3, 22),
    (2, N'break và continue', N'Dừng hoặc bỏ qua lặp', N'https://www.youtube.com/embed/1XQg6WxaIyQ', 4, 20),
    (3, N'Thẻ HTML cơ bản', N'Nội dung bài giảng 3', N'https://www.youtube.com/embed/PN9EUufNkWA', 1, 14),
    (3, N'Thực hành HTML: danh sách', N'Danh sách có thứ tự và không thứ tự trong HTML', N'https://www.youtube.com/embed/kUMe1FH4CHE', 2, 21),
    (3, N'Thẻ a và img trong HTML', N'Tạo liên kết và chèn ảnh', N'https://www.youtube.com/embed/n4R2E7O-Ngo', 3, 18),
    (3, N'Thẻ table trong HTML', N'Tạo bảng với HTML', N'https://www.youtube.com/embed/9uOETcuFjbE', 4, 22),
    (4, N'Công cụ phân tích dữ liệu', N'Giới thiệu pandas và matplotlib', N'https://www.youtube.com/embed/1xtrIEwY_zY', 2, 35),
    (4, N'Biểu đồ trong phân tích dữ liệu', N'Cách vẽ biểu đồ với matplotlib', N'https://www.youtube.com/embed/GW0rj4sNH2w', 3, 30),
    (4, N'Đọc file CSV với Pandas', N'Thực hành đọc dữ liệu', N'https://www.youtube.com/embed/zmdjNSmRXF4', 4, 24),
    (4, N'Làm sạch dữ liệu với Pandas', N'Handling missing values', N'https://www.youtube.com/embed/0gRc-d3k_8Y', 5, 26),
    (5, N'Hàm trong Python', N'Học cách định nghĩa và sử dụng hàm', N'https://www.youtube.com/embed/9Os0o3wzS_I', 1, 24),
    (5, N'Hàm có tham số và giá trị trả về', N'Sử dụng tham số mặc định và return', N'https://www.youtube.com/embed/YB2v4jjl2j8', 3, 22),
    (5, N'Thư viện ngoài: requests, numpy', N'Cài và sử dụng thư viện', N'https://www.youtube.com/embed/gbnDnV1qdK0', 4, 27),
    (5, N'Thư viện chuẩn Python', N'Giới thiệu các thư viện như math, datetime, random', N'https://www.youtube.com/embed/tVZc2E9s7QY', 2, 28),
    (6, N'CSS cơ bản', N'Cách viết và liên kết CSS với HTML', N'https://www.youtube.com/embed/1PnVor36_40', 1, 20),
    (6, N'Selector và thuộc tính cơ bản', N'Sử dụng selector id, class', N'https://www.youtube.com/embed/yfoY53QXEnI', 2, 20),
    (6, N'Màu sắc và font chữ trong CSS', N'Cách tùy chỉnh giao diện trang web', N'https://www.youtube.com/embed/1Rs2ND1ryYc', 3, 19),
    (6, N'Margin, Padding, Border', N'Tùy chỉnh layout cơ bản', N'https://www.youtube.com/embed/1KkA9bJj-rM', 4, 21),
    (7, N'Giới thiệu Matplotlib', N'Vẽ biểu đồ đường, cột với matplotlib', N'https://www.youtube.com/embed/a9UrKTVEeZA', 1, 25),
    (7, N'Biểu đồ nâng cao với Seaborn', N'Direct plot và heatmap', N'https://www.youtube.com/embed/5cLmzM-lENg', 2, 26),
    (7, N'Biểu đồ tròn và biểu đồ phân tán', N'Phân tích dữ liệu nâng cao với biểu đồ', N'https://www.youtube.com/embed/ZjX2ZAdb0Rw', 3, 23),
    (7, N'Tùy chỉnh biểu đồ: màu, nhãn, tiêu đề', N'Làm đẹp biểu đồ với matplotlib', N'https://www.youtube.com/embed/3Xc3CA655Y4', 4, 25);

-- 11. CourseMaterials (phụ thuộc Courses)
INSERT INTO CourseMaterials
    (
    CourseId, FileName, FileUrl, FileType, UploadedAt
    )
VALUES
    (1, N'slides_intro.pdf', N'https://files.example.com/slide1.pdf', N'pdf', GETDATE());

-- 12. Coupons (phụ thuộc Users)
INSERT INTO Coupons
    (CouponCode, DiscountPercent, CreatedBy)
VALUES
    (N'WELCOME10', 10.00, 1);

-- 13. Orders (phụ thuộc Users + PaymentMethods)
INSERT INTO Orders
    (UserId, PaymentMethodId, TotalAmount, CouponCode, Status)
VALUES
    (3, 1, 85.00, NULL, 'paid');
-- ID 1

-- 14. OrderDetails (phụ thuộc Orders + Courses)
INSERT INTO OrderDetails
    (OrderId, CourseId, Price)
VALUES
    (1, 1, 80.00),
    (1, 2, 5.00);

-- 15. Enrollments (phụ thuộc Users + Courses)
INSERT INTO Enrollments
    (UserId, CourseId)
VALUES
    (3, 1),
    (3, 2);

-- 16. Wishlist (phụ thuộc Users + Courses)
INSERT INTO Wishlist
    (UserId, CourseId)
VALUES
    (3, 1);

-- -- 17. Cart (phụ thuộc Users + Courses)
-- INSERT INTO Cart (UserId, CourseId) VALUES (3, 2);

-- 18. Reviews (phụ thuộc Users + Courses)
INSERT INTO Reviews
    (UserId, CourseId, Rating, Comment)
VALUES
    (2, 1, 5, N'Excellent Java course!'),
    (3, 2, 4, N'Great content, could use more exercises');

-- 19. Certificates (phụ thuộc Users + Courses)
INSERT INTO Certificates
    (UserId, CourseId)
VALUES
    (3, 1);

-- 20. BlogPosts (phụ thuộc Users)
INSERT INTO BlogPosts
    (Title, Content, imageBlog, CreatedBy)
VALUES
    (N'5 Tips to Learn Programming Faster', N'Practice, practice, practice...', N'/assets/images/blog/blog-card-01.jpg', 1),
    (N'Trở Thành Lập Trình Viên Giỏi Trong 6 Tháng', N'Hãy bắt đầu với nền tảng vững chắc và dự án thực tế.', N'/assets/images/blog/blog-card-02.jpg', 2),
    (N'Những Lỗi Thường Gặp Khi Học Lập Trình', N'Tìm hiểu và tránh các lỗi phổ biến giúp bạn tiến bộ nhanh hơn.', N'/assets/images/blog/blog-card-03.jpg', 1),
    (N'Học Java Có Khó Không? Hướng Dẫn Cho Người Mới Bắt Đầu', N'Java là ngôn ngữ mạnh mẽ nhưng không hề khó nếu bạn học đúng cách.', N'/assets/images/blog/blog-card-04.jpg', 2),
    (N'Frontend vs Backend: Nên Học Gì Trước?', N'Bài viết giúp bạn phân biệt rõ giữa frontend và backend, cũng như lộ trình học phù hợp.', N'/assets/images/blog/blog-card-05.jpg', 2),
    (N'5 Kênh YouTube Học Lập Trình Chất Lượng Miễn Phí', N'Cùng khám phá những kênh YouTube giúp bạn tự học lập trình hiệu quả.', N'/assets/images/blog/blog-card-06.jpg', 1);

-- 21. ContactInfo (phụ thuộc Users)
INSERT INTO ContactInfo
    (Name, Email, PhoneNumber, Message)
VALUES
    (N'Trần Thị B', N'tranthiB@gmail.com', N'0987654321', N'Tôi muốn được tư vấn về khóa học Lập trình Python.');

-- 22. Notifications (phụ thuộc Users)
INSERT INTO Notifications
    (UserId, Title, Message)
VALUES
    (3, N'Enrollment Successful', N'You have successfully enrolled in Java for Beginners');
GO

-- 23. Thêm 6 quiz: mỗi module có 2 quiz
INSERT INTO Quizzes
    (ModuleId, Title, TotalScore, TimeLimit)
VALUES
    (1, N'Quiz Giới thiệu Python', 10, 15),
    (1, N'Quiz Biến và Kiểu dữ liệu', 10, 15),
    (3, N'Quiz HTML cơ bản', 10, 15),
    (3, N'Quiz Thẻ HTML nâng cao', 10, 15),
    (4, N'Quiz Giới thiệu Phân tích Dữ liệu', 10, 15),
    (4, N'Quiz Công cụ Phân tích Dữ liệu', 10, 15);


-- 24. Thêm câu hỏi cho mỗi quiz
INSERT INTO Questions
    (QuizId, QuestionText, Score)
VALUES
    -- Quiz 1: Giới thiệu Python
    (1, N'Python là ngôn ngữ thông dịch?', 5),
    (1, N'Kiểu dữ liệu nào không có trong Python?', 5),

    -- Quiz 2: Biến và Kiểu dữ liệu
    (2, N'Biến trong Python có thể đổi kiểu dữ liệu sau khi gán?', 5),
    (2, N'Kiểu dữ liệu nào trong Python biểu diễn số thực?', 5),

    -- Quiz 3: HTML cơ bản
    (3, N'Thẻ HTML nào dùng để tạo tiêu đề?', 5),
    (3, N'Thẻ nào dùng để tạo đường liên kết trong HTML?', 5),

    -- Quiz 4: Thẻ HTML nâng cao
    (4, N'Thẻ HTML nào dùng để chèn ảnh?', 5),
    (4, N'Thẻ nào dùng để tạo danh sách có thứ tự?', 5),

    -- Quiz 5: Giới thiệu Phân tích Dữ liệu
    (5, N'Dữ liệu là gì trong phân tích dữ liệu?', 5),
    (5, N'Thư viện nào phổ biến trong Python để phân tích dữ liệu?', 5),

    -- Quiz 6: Công cụ Phân tích Dữ liệu
    (6, N'Công cụ nào dùng để vẽ biểu đồ trong phân tích dữ liệu?', 5),
    (6, N'Jupyter Notebook thường dùng cho mục đích nào?', 5);


-- 25. Đáp án cho từng câu hỏi
-- Câu 1
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (1, N'Đúng', 1),
    (1, N'Sai', 0);

-- Câu 2
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (2, N'List', 0),
    (2, N'Tuple', 0),
    (2, N'Class', 0),
    (2, N'Pointer', 1);

-- Câu 3
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (3, N'Có', 1),
    (3, N'Không', 0);

-- Câu 4
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (4, N'float', 1),
    (4, N'int', 0),
    (4, N'str', 0),
    (4, N'bool', 0);

-- Câu 5
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (5, N'<h1>', 1),
    (5, N'<div>', 0),
    (5, N'<title>', 0),
    (5, N'<p>', 0);

-- Câu 6
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (6, N'<a>', 1),
    (6, N'<link>', 0),
    (6, N'<href>', 0),
    (6, N'<img>', 0);

-- Câu 7
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (7, N'<img>', 1),
    (7, N'<src>', 0),
    (7, N'<picture>', 0),
    (7, N'<media>', 0);

-- Câu 8
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (8, N'<ol>', 1),
    (8, N'<ul>', 0),
    (8, N'<li>', 0),
    (8, N'<list>', 0);

-- Câu 9
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (9, N'Tập hợp các thông tin có thể xử lý', 1),
    (9, N'Một dạng ngôn ngữ lập trình', 0),
    (9, N'Công cụ phân tích dữ liệu', 0),
    (9, N'Một phần mềm thống kê', 0);

-- Câu 10
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (10, N'Pandas', 1),
    (10, N'Django', 0),
    (10, N'NumPy', 0),
    (10, N'Flask', 0);

-- Câu 11
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (11, N'Matplotlib', 1),
    (11, N'Pandas', 0),
    (11, N'NumPy', 0),
    (11, N'Scipy', 0);

-- Câu 12
INSERT INTO Options
    (QuestionId, OptionText, IsCorrect)
VALUES
    (12, N'Viết mã và phân tích dữ liệu tương tác', 1),
    (12, N'Thiết kế giao diện đồ họa', 0),
    (12, N'Xây dựng hệ quản trị cơ sở dữ liệu', 0),
    (12, N'Tạo game trong Python', 0);

-- DỮ LIỆU MẪU LÀM BÀI QUIZZ
-- INSERT INTO QuizSubmissions
--     (QuizId, UserId, Score, SubmittedAt)
--  VALUES
--     (2, 3, 10, GETDATE()),
--     (3, 3, 5, GETDATE()),
--     (5, 3, 8, GETDATE());

-- INSERT INTO QuizAnswers
--     (SubmissionId, QuestionId, SelectedOptionId, IsCorrect)
-- VALUES
--     (1, 3, 9, 1),
--     (1, 4, 16, 0),
--     (2, 5, 17, 1),
--     (2, 6, 24, 0);
