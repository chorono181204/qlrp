# QLRP Domain Roadmap

## Bang va model da co

- `roles` -> `Role`
- `users` -> `User`
- `movies` -> `Movie`
- `genres` -> `Genre`
- `movie_genres` -> `MovieGenre`
- `cinemas` -> `Cinema`
- `rooms` -> `Room`
- `seat_types` -> `SeatType`
- `seats` -> `Seat`
- `showtimes` -> `Showtime`
- `bookings` -> `Booking`
- `booking_details` -> `BookingDetail`
- `payments` -> `Payment`

## Thu tu nen code tiep

1. `Auth`: dang ky, dang nhap, dang xuat
2. `Movie`: CRUD phim, the loai, gan the loai cho phim
3. `Cinema`: CRUD rap, phong, ghế
4. `Showtime`: tao lich chieu, kiem tra trung lich phong
5. `Booking`: chon phim, lich chieu, ghe, tao booking
6. `Payment`: xac nhan thanh toan va cap nhat trang thai don
7. `Report`: thong ke doanh thu, ve da ban, phim hot

## Luong mua ve de code

1. Tai menu khach hang, cho phep xem phim va lich chieu.
2. Chon `showtime`, load danh sach ghế cua `room`.
3. Tim ghế da dat bang `booking_details` theo `showtime_id`.
4. Chon ghế hop le, tinh tong tien theo `base_price * price_multiplier`.
5. Tao `booking`, sau do tao cac dong `booking_details`.
6. Khi thanh toan thanh cong, cap nhat `bookings.status = 'PAID'`.

## Rang buoc quan trong

- `users.username` la duy nhat.
- `rooms` khong trung ten trong cung mot rap.
- `seats` khong trung vi tri trong cung mot phong.
- `booking_details` co `UNIQUE (showtime_id, seat_id)` de chan ban trung ghế.
