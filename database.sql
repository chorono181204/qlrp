CREATE DATABASE IF NOT EXISTS qlrp
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlrp;

CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    role_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    duration_minutes INT NOT NULL,
    director VARCHAR(100),
    actors TEXT,
    language VARCHAR(50),
    age_rating VARCHAR(20),
    release_date DATE,
    end_date DATE,
    description TEXT,
    poster_url VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'COMING_SOON',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS movie_genres (
    movie_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genres_movies FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_genres_genres FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cinemas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cinema_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    room_type VARCHAR(30) NOT NULL DEFAULT '2D',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_rooms_cinemas FOREIGN KEY (cinema_id) REFERENCES cinemas(id),
    CONSTRAINT uq_room_name_per_cinema UNIQUE (cinema_id, name)
);

CREATE TABLE IF NOT EXISTS seat_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    price_multiplier DECIMAL(5,2) NOT NULL DEFAULT 1.00
);

CREATE TABLE IF NOT EXISTS seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    seat_row CHAR(2) NOT NULL,
    seat_number INT NOT NULL,
    seat_type_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_seats_rooms FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_seats_seat_types FOREIGN KEY (seat_type_id) REFERENCES seat_types(id),
    CONSTRAINT uq_seat_per_room UNIQUE (room_id, seat_row, seat_number)
);

CREATE TABLE IF NOT EXISTS showtimes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    room_id INT NOT NULL,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT fk_showtimes_movies FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_showtimes_rooms FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    booking_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_bookings_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS booking_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    showtime_id INT NOT NULL,
    seat_id INT NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
    CONSTRAINT fk_booking_details_bookings FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_details_showtimes FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    CONSTRAINT fk_booking_details_seats FOREIGN KEY (seat_id) REFERENCES seats(id),
    CONSTRAINT uq_showtime_seat UNIQUE (showtime_id, seat_id)
);

CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    CONSTRAINT fk_payments_bookings FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

INSERT INTO roles (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name)
SELECT 'STAFF'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STAFF');

INSERT INTO roles (name)
SELECT 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CUSTOMER');

INSERT INTO seat_types (name, price_multiplier)
SELECT 'THUONG', 1.00
WHERE NOT EXISTS (SELECT 1 FROM seat_types WHERE name = 'THUONG');

INSERT INTO seat_types (name, price_multiplier)
SELECT 'VIP', 1.30
WHERE NOT EXISTS (SELECT 1 FROM seat_types WHERE name = 'VIP');

INSERT INTO seat_types (name, price_multiplier)
SELECT 'DOI', 1.80
WHERE NOT EXISTS (SELECT 1 FROM seat_types WHERE name = 'DOI');

INSERT INTO genres (name)
SELECT 'Hanh dong'
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Hanh dong');

INSERT INTO genres (name)
SELECT 'Tinh cam'
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Tinh cam');

INSERT INTO genres (name)
SELECT 'Kinh di'
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Kinh di');

INSERT INTO genres (name)
SELECT 'Hai'
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Hai');

INSERT INTO genres (name)
SELECT 'Hoat hinh'
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Hoat hinh');

INSERT INTO genres (name)
SELECT 'Vien tuong'
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Vien tuong');

INSERT INTO users (full_name, username, password, role_id)
SELECT 'Quan tri vien', 'admin', '123456', r.id
FROM roles r
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (full_name, username, password, role_id)
SELECT 'Nhan vien rap', 'staff', '123456', r.id
FROM roles r
WHERE r.name = 'STAFF'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff');

INSERT INTO users (full_name, username, password, phone, email, role_id)
SELECT 'Nguyen Van Nam', 'nam123', '123456', '0901000001', 'nam123@example.com', r.id
FROM roles r
WHERE r.name = 'CUSTOMER'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'nam123');

INSERT INTO users (full_name, username, password, phone, email, role_id)
SELECT 'Tran Thi Lan', 'lan456', '123456', '0901000002', 'lan456@example.com', r.id
FROM roles r
WHERE r.name = 'CUSTOMER'
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'lan456');

INSERT INTO cinemas (name, address, phone)
SELECT 'QLRP Nguyen Trai', '101 Nguyen Trai, Quan 1, TP.HCM', '02873001001'
WHERE NOT EXISTS (SELECT 1 FROM cinemas WHERE name = 'QLRP Nguyen Trai');

INSERT INTO cinemas (name, address, phone)
SELECT 'QLRP Phan Xich Long', '25 Phan Xich Long, Phu Nhuan, TP.HCM', '02873001002'
WHERE NOT EXISTS (SELECT 1 FROM cinemas WHERE name = 'QLRP Phan Xich Long');

INSERT INTO rooms (cinema_id, name, capacity, room_type, status)
SELECT c.id, 'Phong 1', 8, '2D', 'ACTIVE'
FROM cinemas c
WHERE c.name = 'QLRP Nguyen Trai'
  AND NOT EXISTS (
      SELECT 1 FROM rooms r
      WHERE r.cinema_id = c.id AND r.name = 'Phong 1'
  );

INSERT INTO rooms (cinema_id, name, capacity, room_type, status)
SELECT c.id, 'Phong 2', 8, '3D', 'ACTIVE'
FROM cinemas c
WHERE c.name = 'QLRP Nguyen Trai'
  AND NOT EXISTS (
      SELECT 1 FROM rooms r
      WHERE r.cinema_id = c.id AND r.name = 'Phong 2'
  );

INSERT INTO rooms (cinema_id, name, capacity, room_type, status)
SELECT c.id, 'Phong VIP', 8, 'IMAX', 'ACTIVE'
FROM cinemas c
WHERE c.name = 'QLRP Phan Xich Long'
  AND NOT EXISTS (
      SELECT 1 FROM rooms r
      WHERE r.cinema_id = c.id AND r.name = 'Phong VIP'
  );

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 1, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 1);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 2, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 2);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 3, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 3);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 4, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 4);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 1, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 1);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 2, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 2);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 3, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 3);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 4, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong 1'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 4);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 1, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 1);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 2, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 2);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 3, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 3);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 4, st.id, 'ACTIVE'
FROM rooms r
JOIN seat_types st ON st.name = 'THUONG'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 4);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 1, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 1);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 2, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 2);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 3, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 3);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 4, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong 2'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 4);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 1, st.id, 'VIP'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 1);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 2, st.id, 'VIP'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 2);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 3, st.id, 'VIP'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 3);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'A', 4, st.id, 'VIP'
FROM rooms r
JOIN seat_types st ON st.name = 'VIP'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'A' AND s.seat_number = 4);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 1, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 1);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 2, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 2);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 3, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 3);

INSERT INTO seats (room_id, seat_row, seat_number, seat_type_id, status)
SELECT r.id, 'B', 4, st.id, 'DOI'
FROM rooms r
JOIN seat_types st ON st.name = 'DOI'
WHERE r.name = 'Phong VIP'
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id AND s.seat_row = 'B' AND s.seat_number = 4);

INSERT INTO movies (title, duration_minutes, director, actors, language, age_rating, release_date, description, status)
SELECT 'Lat Mat 8', 128, 'Ly Hai', 'Ly Hai, Thu Trang', 'Tieng Viet', 'P13', '2026-04-01', 'Phim hanh dong gia dinh Viet Nam.', 'NOW_SHOWING'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Lat Mat 8');

INSERT INTO movies (title, duration_minutes, director, actors, language, age_rating, release_date, description, status)
SELECT 'Vu Tru Hoat Hinh', 102, 'Minh Nhat', 'Long tieng Viet', 'Tieng Viet', 'K', '2026-04-02', 'Phim hoat hinh phieu luu danh cho gia dinh.', 'NOW_SHOWING'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Vu Tru Hoat Hinh');

INSERT INTO movies (title, duration_minutes, director, actors, language, age_rating, release_date, description, status)
SELECT 'Dem O Nha Hoang', 115, 'Tran Bao', 'Quoc Huy, Ngoc Lan', 'Tieng Viet', 'T18', '2026-04-05', 'Phim kinh di bi an trong biet thu co.', 'COMING_SOON'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Dem O Nha Hoang');

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id
FROM movies m
JOIN genres g ON g.name = 'Hanh dong'
WHERE m.title = 'Lat Mat 8'
  AND NOT EXISTS (
      SELECT 1 FROM movie_genres mg
      WHERE mg.movie_id = m.id AND mg.genre_id = g.id
  );

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id
FROM movies m
JOIN genres g ON g.name = 'Tinh cam'
WHERE m.title = 'Lat Mat 8'
  AND NOT EXISTS (
      SELECT 1 FROM movie_genres mg
      WHERE mg.movie_id = m.id AND mg.genre_id = g.id
  );

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id
FROM movies m
JOIN genres g ON g.name = 'Hoat hinh'
WHERE m.title = 'Vu Tru Hoat Hinh'
  AND NOT EXISTS (
      SELECT 1 FROM movie_genres mg
      WHERE mg.movie_id = m.id AND mg.genre_id = g.id
  );

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id
FROM movies m
JOIN genres g ON g.name = 'Vien tuong'
WHERE m.title = 'Vu Tru Hoat Hinh'
  AND NOT EXISTS (
      SELECT 1 FROM movie_genres mg
      WHERE mg.movie_id = m.id AND mg.genre_id = g.id
  );

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id
FROM movies m
JOIN genres g ON g.name = 'Kinh di'
WHERE m.title = 'Dem O Nha Hoang'
  AND NOT EXISTS (
      SELECT 1 FROM movie_genres mg
      WHERE mg.movie_id = m.id AND mg.genre_id = g.id
  );

INSERT INTO showtimes (movie_id, room_id, show_date, start_time, end_time, base_price, status)
SELECT m.id, r.id, '2026-04-05', '09:00:00', '11:10:00', 90000, 'OPEN'
FROM movies m
JOIN rooms r ON r.name = 'Phong 1'
WHERE m.title = 'Lat Mat 8'
  AND NOT EXISTS (
      SELECT 1 FROM showtimes s
      WHERE s.movie_id = m.id AND s.room_id = r.id AND s.show_date = '2026-04-05' AND s.start_time = '09:00:00'
  );

INSERT INTO showtimes (movie_id, room_id, show_date, start_time, end_time, base_price, status)
SELECT m.id, r.id, '2026-04-05', '13:30:00', '15:20:00', 85000, 'OPEN'
FROM movies m
JOIN rooms r ON r.name = 'Phong 2'
WHERE m.title = 'Vu Tru Hoat Hinh'
  AND NOT EXISTS (
      SELECT 1 FROM showtimes s
      WHERE s.movie_id = m.id AND s.room_id = r.id AND s.show_date = '2026-04-05' AND s.start_time = '13:30:00'
  );

INSERT INTO showtimes (movie_id, room_id, show_date, start_time, end_time, base_price, status)
SELECT m.id, r.id, '2026-04-06', '19:00:00', '20:55:00', 120000, 'OPEN'
FROM movies m
JOIN rooms r ON r.name = 'Phong VIP'
WHERE m.title = 'Dem O Nha Hoang'
  AND NOT EXISTS (
      SELECT 1 FROM showtimes s
      WHERE s.movie_id = m.id AND s.room_id = r.id AND s.show_date = '2026-04-06' AND s.start_time = '19:00:00'
  );

INSERT INTO bookings (user_id, booking_code, booking_time, total_amount, status)
SELECT u.id, 'BKDEMO001', '2026-04-03 10:00:00', 207000, 'PAID'
FROM users u
WHERE u.username = 'nam123'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BKDEMO001');

INSERT INTO booking_details (booking_id, showtime_id, seat_id, ticket_price, status)
SELECT b.id, s.id, st.id, 117000, 'BOOKED'
FROM bookings b
JOIN showtimes s ON s.show_date = '2026-04-05' AND s.start_time = '09:00:00'
JOIN seats st ON st.seat_row = 'B' AND st.seat_number = 1
JOIN rooms r ON r.id = st.room_id AND r.id = s.room_id AND r.name = 'Phong 1'
WHERE b.booking_code = 'BKDEMO001'
  AND NOT EXISTS (
      SELECT 1 FROM booking_details bd
      WHERE bd.booking_id = b.id AND bd.showtime_id = s.id AND bd.seat_id = st.id
  );

INSERT INTO booking_details (booking_id, showtime_id, seat_id, ticket_price, status)
SELECT b.id, s.id, st.id, 90000, 'BOOKED'
FROM bookings b
JOIN showtimes s ON s.show_date = '2026-04-05' AND s.start_time = '09:00:00'
JOIN seats st ON st.seat_row = 'A' AND st.seat_number = 2
JOIN rooms r ON r.id = st.room_id AND r.id = s.room_id AND r.name = 'Phong 1'
WHERE b.booking_code = 'BKDEMO001'
  AND NOT EXISTS (
      SELECT 1 FROM booking_details bd
      WHERE bd.booking_id = b.id AND bd.showtime_id = s.id AND bd.seat_id = st.id
  );

INSERT INTO payments (booking_id, payment_method, amount, payment_time, status)
SELECT b.id, 'MOMO', 207000, '2026-04-03 10:01:00', 'SUCCESS'
FROM bookings b
WHERE b.booking_code = 'BKDEMO001'
  AND NOT EXISTS (
      SELECT 1 FROM payments p WHERE p.booking_id = b.id
  );

INSERT INTO bookings (user_id, booking_code, booking_time, total_amount, status)
SELECT u.id, 'BKDEMO002', '2026-04-03 11:30:00', 153000, 'PAID'
FROM users u
WHERE u.username = 'lan456'
  AND NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code = 'BKDEMO002');

INSERT INTO booking_details (booking_id, showtime_id, seat_id, ticket_price, status)
SELECT b.id, s.id, st.id, 76500, 'BOOKED'
FROM bookings b
JOIN showtimes s ON s.show_date = '2026-04-05' AND s.start_time = '13:30:00'
JOIN seats st ON st.seat_row = 'B' AND st.seat_number = 1
JOIN rooms r ON r.id = st.room_id AND r.id = s.room_id AND r.name = 'Phong 2'
WHERE b.booking_code = 'BKDEMO002'
  AND NOT EXISTS (
      SELECT 1 FROM booking_details bd
      WHERE bd.booking_id = b.id AND bd.showtime_id = s.id AND bd.seat_id = st.id
  );

INSERT INTO booking_details (booking_id, showtime_id, seat_id, ticket_price, status)
SELECT b.id, s.id, st.id, 76500, 'BOOKED'
FROM bookings b
JOIN showtimes s ON s.show_date = '2026-04-05' AND s.start_time = '13:30:00'
JOIN seats st ON st.seat_row = 'B' AND st.seat_number = 2
JOIN rooms r ON r.id = st.room_id AND r.id = s.room_id AND r.name = 'Phong 2'
WHERE b.booking_code = 'BKDEMO002'
  AND NOT EXISTS (
      SELECT 1 FROM booking_details bd
      WHERE bd.booking_id = b.id AND bd.showtime_id = s.id AND bd.seat_id = st.id
  );

INSERT INTO payments (booking_id, payment_method, amount, payment_time, status)
SELECT b.id, 'CARD', 153000, '2026-04-03 11:32:00', 'SUCCESS'
FROM bookings b
WHERE b.booking_code = 'BKDEMO002'
  AND NOT EXISTS (
      SELECT 1 FROM payments p WHERE p.booking_id = b.id
  );
