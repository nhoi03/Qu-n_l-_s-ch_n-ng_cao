-- Categories (6 thể loại)
INSERT INTO categories (id, name, description, active) VALUES
(1, 'Công nghệ thông tin', 'Sách về lập trình, kiến trúc phần mềm, AI, cơ sở dữ liệu và mạng máy tính', TRUE),
(2, 'Văn học - Nghệ thuật', 'Tiểu thuyết, truyện ngắn, thơ ca và tác phẩm nghệ thuật kinh điển', TRUE),
(3, 'Kinh tế - Quản trị', 'Tài chính, khởi nghiệp, marketing, quản trị kinh doanh và đầu tư', TRUE),
(4, 'Khoa học - Tự nhiên', 'Vật lý, thiên văn học, sinh học, hóa học và khoa học ứng dụng', TRUE),
(5, 'Tâm lý - Kỹ năng sống', 'Phát triển bản thân, kỹ năng giao tiếp, tâm lý học hành vi và tư duy', TRUE),
(6, 'Lịch sử - Triết học', 'Lịch sử thế giới, lịch sử Việt Nam, tư tưởng và triết học cổ kim', TRUE);

-- Books (25 cuốn sách thuộc các thể loại)
INSERT INTO books (id, isbn, title, author, category_id, total_quantity, available_quantity, status, created_at) VALUES
(1, '978-0132350884', 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 1, 10, 8, 'ACTIVE', CURRENT_TIMESTAMP),
(2, '978-0134685991', 'Effective Java (3rd Edition)', 'Joshua Bloch', 1, 8, 7, 'ACTIVE', CURRENT_TIMESTAMP),
(3, '978-0321125217', 'Domain-Driven Design: Tackling Complexity in the Heart of Software', 'Eric Evans', 1, 5, 5, 'ACTIVE', CURRENT_TIMESTAMP),
(4, '978-1491950357', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 1, 6, 4, 'ACTIVE', CURRENT_TIMESTAMP),
(5, '978-1617294945', 'Spring in Action (6th Edition)', 'Craig Walls', 1, 7, 6, 'ACTIVE', CURRENT_TIMESTAMP),
(6, '978-0141439518', 'Pride and Prejudice', 'Jane Austen', 2, 12, 12, 'ACTIVE', CURRENT_TIMESTAMP),
(7, '978-0451524935', '1984', 'George Orwell', 2, 15, 13, 'ACTIVE', CURRENT_TIMESTAMP),
(8, '978-0743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 2, 10, 10, 'ACTIVE', CURRENT_TIMESTAMP),
(9, '978-6047715435', 'Số Đỏ', 'Vũ Trọng Phụng', 2, 8, 8, 'ACTIVE', CURRENT_TIMESTAMP),
(10, '978-6045889923', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 2, 10, 10, 'ACTIVE', CURRENT_TIMESTAMP),
(11, '978-0060555665', 'The Intelligent Investor', 'Benjamin Graham', 3, 10, 10, 'ACTIVE', CURRENT_TIMESTAMP),
(12, '978-1591846444', 'Zero to One: Notes on Startups', 'Peter Thiel', 3, 7, 7, 'ACTIVE', CURRENT_TIMESTAMP),
(13, '978-0062457714', 'Good to Great', 'Jim Collins', 3, 6, 6, 'ACTIVE', CURRENT_TIMESTAMP),
(14, '978-1118503256', 'Principles: Life and Work', 'Ray Dalio', 3, 8, 8, 'ACTIVE', CURRENT_TIMESTAMP),
(15, '978-0553380163', 'A Brief History of Time', 'Stephen Hawking', 4, 9, 9, 'ACTIVE', CURRENT_TIMESTAMP),
(16, '978-0393609394', 'Astrophysics for People in a Hurry', 'Neil deGrasse Tyson', 4, 7, 7, 'ACTIVE', CURRENT_TIMESTAMP),
(17, '978-0385537377', 'The Gene: An Intimate History', 'Siddhartha Mukherjee', 4, 5, 5, 'ACTIVE', CURRENT_TIMESTAMP),
(18, '978-0671027032', 'Đắc Nhân Tâm (How to Win Friends and Influence People)', 'Dale Carnegie', 5, 20, 20, 'ACTIVE', CURRENT_TIMESTAMP),
(19, '978-1501124020', 'Atomic Habits', 'James Clear', 5, 15, 13, 'ACTIVE', CURRENT_TIMESTAMP),
(20, '978-0062316097', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 6, 12, 11, 'ACTIVE', CURRENT_TIMESTAMP),
(21, '978-0062464316', 'Homo Deus: Lược Sử Tương Lai', 'Yuval Noah Harari', 6, 8, 8, 'ACTIVE', CURRENT_TIMESTAMP),
(22, '978-0199298402', 'Lịch Sử Triết Học Tây Phương', 'Bertrand Russell', 6, 5, 5, 'ACTIVE', CURRENT_TIMESTAMP),
(23, '978-0307887894', 'The Lean Startup', 'Eric Ries', 3, 10, 10, 'ACTIVE', CURRENT_TIMESTAMP),
(24, '978-0596007126', 'Head First Design Patterns', 'Eric Freeman', 1, 8, 8, 'ACTIVE', CURRENT_TIMESTAMP),
(25, '978-0262033848', 'Introduction to Algorithms (CLRS)', 'Thomas H. Cormen', 1, 4, 4, 'INACTIVE', CURRENT_TIMESTAMP);

-- Members (12 thành viên với các trạng thái ACTIVE, BLOCKED)
INSERT INTO members (id, full_name, email, phone, status, created_at) VALUES
(1, 'Nguyễn Văn An', 'nguyenvanan@gmail.com', '0901234567', 'ACTIVE', CURRENT_TIMESTAMP),
(2, 'Trần Thị Bình', 'tranbinh@gmail.com', '0912345678', 'ACTIVE', CURRENT_TIMESTAMP),
(3, 'Lê Hoàng Cường', 'lecuong@gmail.com', '0923456789', 'ACTIVE', CURRENT_TIMESTAMP),
(4, 'Phạm Minh Đức', 'phamduc@gmail.com', '0934567890', 'BLOCKED', CURRENT_TIMESTAMP),
(5, 'Hoàng Thu Giang', 'hoanggiang@gmail.com', '0945678901', 'ACTIVE', CURRENT_TIMESTAMP),
(6, 'Vũ Hải Nam', 'vunam@gmail.com', '0956789012', 'ACTIVE', CURRENT_TIMESTAMP),
(7, 'Đỗ Bích Ngọc', 'dongoc@gmail.com', '0967890123', 'ACTIVE', CURRENT_TIMESTAMP),
(8, 'Bùi Quốc Phong', 'buiphong@gmail.com', '0978901234', 'BLOCKED', CURRENT_TIMESTAMP),
(9, 'Ngô Thanh Sơn', 'ngoson@gmail.com', '0989012345', 'ACTIVE', CURRENT_TIMESTAMP),
(10, 'Đinh Quỳnh Trang', 'dinhtrang@gmail.com', '0990123456', 'ACTIVE', CURRENT_TIMESTAMP),
(11, 'Mai Văn Vũ', 'maivu@gmail.com', '0909876543', 'ACTIVE', CURRENT_TIMESTAMP),
(12, 'Lý Thiên Hương', 'lyhuong@gmail.com', '0918765432', 'ACTIVE', CURRENT_TIMESTAMP);

-- Sample Borrowings (6 phiếu mượn mẫu gồm đã trả, đang mượn, quá hạn, phạt tiền)
-- Phiếu 1: Đã trả đúng hạn (Hoàn tất)
INSERT INTO borrowings (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(1, 1, '2026-08-01', '2026-08-15', '2026-08-12', 'RETURNED', 0.00);

INSERT INTO borrowing_details (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(1, 1, 1, 1, 1, 0.00),
(2, 1, 2, 1, 1, 0.00);

-- Phiếu 2: Đang mượn trong hạn
INSERT INTO borrowings (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(2, 2, '2026-09-20', '2026-10-04', NULL, 'BORROWED', 0.00);

INSERT INTO borrowing_details (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(3, 2, 1, 1, 0, 0.00),
(4, 2, 5, 1, 0, 0.00);

-- Phiếu 3: Đang mượn và QUÁ HẠN (Hạn trả 2026-09-10, hôm nay là 2026-09-30 -> Quá hạn 20 ngày)
INSERT INTO borrowings (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(3, 3, '2026-08-27', '2026-09-10', NULL, 'BORROWED', 0.00);

INSERT INTO borrowing_details (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(5, 3, 4, 2, 0, 0.00),
(6, 3, 7, 2, 0, 0.00);

-- Phiếu 4: Đã trả muộn và ĐÃ TÍNH PHẠT (Quá hạn 3 ngày * 2 cuốn * 5.000 = 30.000 VND)
INSERT INTO borrowings (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(4, 5, '2026-08-10', '2026-08-24', '2026-08-27', 'RETURNED', 30000.00);

INSERT INTO borrowing_details (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(7, 4, 19, 2, 2, 30000.00);

-- Phiếu 5: Đang mượn, đã trả 1 phần (Mượn 2 cuốn Clean Code, đã trả 1, còn 1)
INSERT INTO borrowings (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(5, 6, '2026-09-18', '2026-10-02', NULL, 'BORROWED', 0.00);

INSERT INTO borrowing_details (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(8, 5, 1, 2, 1, 0.00),
(9, 5, 2, 1, 0, 0.00);

-- Phiếu 6: Đang mượn và QUÁ HẠN (Hạn trả 2026-09-15)
INSERT INTO borrowings (id, member_id, borrow_date, due_date, returned_date, status, total_fine) VALUES
(6, 7, '2026-09-01', '2026-09-15', NULL, 'BORROWED', 0.00);

INSERT INTO borrowing_details (id, borrowing_id, book_id, quantity, returned_quantity, fine_amount) VALUES
(10, 6, 20, 1, 0, 0.00);
