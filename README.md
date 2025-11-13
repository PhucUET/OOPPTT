# Arkanoid Game - Object-Oriented Programming Project

## Author
Group 3 - OOPPTT - Class INT2204 4
1. Đinh Gia Phúc - 24020274
2. Thìn Thị Thúy - 24020319
3. Nguyễn Thành Trung - 24020337

**Instructor**: Kiều Văn Tuyên - Vũ Đức Hiếu
**Semester**: HK1 - 2025

---

## Description
Đây là một game Arkanoid cổ điển được phát triển bằng Java như một dự án cuối kỳ cho khóa học Lập trình Hướng đối tượng. Dự án thể hiện việc triển khai các nguyên tắc Lập trình Hướng đối tượng (OOP) và các mẫu thiết kế (design patterns)

**Key features:**
1. Trò chơi được phát triển bằng Java 17+ với JavaFX/Swing cho giao diện người dùng đồ họa (GUI).
2. Triển khai các nguyên tắc cốt lõi của Lập trình Hướng đối tượng (OOP): Đóng gói, Kế thừa, Đa hình và Trừu tượng.
3. Áp dụng nhiều mẫu thiết kế (design patterns): Singleton, Factory Method, Strategy, Observer và State.
4. Tích hợp đa luồng (multithreading) để mang lại trải nghiệm chơi mượt mà và giao diện người dùng (UI) phản hồi nhanh.
5. Bao gồm hiệu ứng âm thanh, hoạt ảnh (animations) và hệ thống tăng sức mạnh (power-up).
6. Hỗ trợ chức năng lưu trò chơi và hệ thống bảng xếp hạng.

**Game mechanics:**
- Điều khiển một thanh paddle (vợt) để nảy bóng và phá hủy các viên gạch.
- Thu thập các vật phẩm tăng sức mạnh (power-ups) để có được khả năng đặc biệt.
- Vượt qua nhiều cấp độ với độ khó tăng dần
- Ghi điểm và cạnh tranh trên bảng xếp hạng.

---

## UML Diagram

### Class Diagram
![Class Diagram](docs/uml/class-diagram.png)

_Có thể sử dụng IntelliJ để generate ra Class Diagrams: https://www.youtube.com/watch?v=yCkTqNxZkbY_

*Complete UML diagrams are available in the `docs/uml/` folder*

---

## Design Patterns Implementation

_Có dùng hay không và dùng ở đâu_

### 1. Singleton Pattern
**Used in:** `GameManager`, `AudioManager`, `ResourceLoader`

**Purpose:** Ensure only one instance exists throughout the application.

---

## Multithreading Implementation
_Có dùng hay không và dùng như thế nào_

The game uses multiple threads to ensure smooth performance:

1. **Game Loop Thread**: Updates game logic at 60 FPS
2. **Rendering Thread**: Handles graphics rendering (EDT for JavaFX Application Thread)
3. **Audio Thread Pool**: Plays sound effects asynchronously
4. **I/O Thread**: Handles save/load operations without blocking UI

---

## Installation

1. Clone dự án repository.
2. Mở dự án trên IDE.
3. Chạy dự án.

## Usage

### Controls
| Key | Action |
|-----|--------|
| `←` or `A` | Di chuyển paddle sang trái |
| `→` or `D` | Di chuyển paddle sang phải |
| `SPACE` | Bắt đầu di chuyển bóng |
| `P` or `ESC` | Tạm dừng trò chơi |
| `R` | Bắt đầu lại trò chơi |
| `Q` | Thoát ra menu |

### How to Play
1. **Bắt đầu trò chơi**: Chọn chế độ chơi từ menu: battle hoặc adventure.
2. **Điều khiển thanh trượt**: Sử dụng các phím mũi tên hoặc A/D để di chuyển sang trái và phải.
3. **Phóng bóng**: Nhấn phím SPACE để phóng bóng từ thanh paddle.
4. **Phá hủy những viên gạch (tàu)**: Nảy bóng để đánh và phá hủy các viên gạch (tàu).
5. **Thu thập vật phẩm tăng sức mạnh**: Bắt các vật phẩm tăng sức mạnh (power-ups) rơi xuống để có được khả năng đặc biệt.
6. **Tránh mất bóng**: Giữ bóng không rơi xuống dưới thanh paddle.
7. **Hoàn thành cấp độ**: Phá hủy tất cả các viên gạch có thể phá hủy để tiến lên cấp độ tiếp theo.
8. **Giành chiến thắng cuộc đấu**: Phá hủy tất cả các viên gạch có thể phá hủy, giành điểm cao hơn để chiến thắng cuộc đấu

### Một số vật phẩm tăng sức mạnh
<img width="960" height="540" alt="arkanoid" src="https://github.com/user-attachments/assets/03f5ddf6-f5d3-41fc-9b3f-fd584e7cce52" />


### Cơ chế điểm số
- Ship 1: 100 điểm
- Ship 2: 300 điểm
- Ship 3: 500 điểm
- Nhặt được vật phẩm tăng sức mạnh: 50 điểm

---

## Demo

### Screenshots

**Main Menu**  
![Main Menu](docs/screenshots/menu.png)

**Gameplay**  
![Gameplay](docs/screenshots/gameplay.png)

**Power-ups in Action**  
![Power-ups](docs/screenshots/powerups.png)

**Leaderboard**  
![Leaderboard](docs/screenshots/leaderboard.png)

### Video Demo
[![Video Demo](docs/screenshots/video-thumbnail.png)](docs/demo/gameplay.mp4)

*Full gameplay video is available in `docs/demo/gameplay.mp4`*

---

## Future Improvements

### Planned Features
1. **Bổ sung các cấp độ chơi**
   - Thêm nhiều cấp độ chơi, độ khó tăng dần
   - Thêm chế độ chơi nhiều người hơn (hiện tại có chế độ 1 người chơi và 2 người chơi (battle))
   - Thêm chế độ chơi đồng đội
   - Cải tiến chế độ chơi paddle (thêm powerup riêng cho chế độ này,...)

2. **Nâng cao trải nghiệm chơi game**
   - Thêm những map đấu boss, thêm nhiều loại boss
   - Thêm nhiều vật phẩm tăng sức mạnh (Brick (ship) không thể bị phá vỡ, bóng di xuyên brick (ship), powerup làm nổ một vùng brick (ship),...)
   - Hệ thống nhiệm vụ, kiếm tiền để nâng cấp, mở khóa powerup
   - Thêm cốt truyện thú vị

3. **Cải tiến kỹ thuật**
   - Cải tiến, thêm nhiều hiệu ứng animation mượt mà hơn
   - Triển khai chế độ đối thủ AI

---

## Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Core language |
| JavaFX | 19.0.2 | GUI framework |
| Maven | 3.9+ | Build tool |
| Jackson | 2.15.0 | JSON processing |

---

## License

Dự án này được phát triển chỉ với mục đích học tập.

**Academic Integrity:** Mã này được cung cấp dưới dạng tài liệu tham khảo. Vui lòng tuân thủ các chính sách về tính chính trực trong học thuật của tổ chức bạn.

---

## Notes

- Trò chơi được phát triển như một phần của chương trình giảng dạy khóa học Lập trình Hướng đối tượng với Java.
- Tất cả mã được viết bởi các thành viên trong nhóm dưới sự hướng dẫn của giảng viên.
- Một số tài nguyên (hình ảnh, âm thanh) có thể được sử dụng cho mục đích giáo dục theo nguyên tắc sử dụng hợp lý (fair use).
- Dự án này minh họa ứng dụng thực tế các khái niệm OOP và các mẫu thiết kế (design patterns).
---

*Last updated: 13/11/2025*
