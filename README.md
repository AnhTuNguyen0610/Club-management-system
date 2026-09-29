# Hệ thống quản lý Câu lạc bộ

Bài tập lớn môn Lập trình hướng đối tượng (OOP) — Java thuần + Swing (giao diện) +
SQLite (lưu trữ), không dùng framework nặng (không Spring, không ORM).

## 1. Tổng quan

Ứng dụng quản lý một Câu lạc bộ (CLB): quản lý thành viên và quản lý các sự kiện do
CLB tổ chức (workshop, cuộc thi, giao lưu). Dữ liệu được lưu thật vào file SQLite —
tắt/mở lại chương trình không mất dữ liệu.

**Trạng thái hiện tại:** hạ tầng (database + khung giao diện) do Tech Lead dựng xong
và đã chạy được (trang Tổng quan hoạt động đầy đủ). Hai màn hình Thành viên và Sự
kiện đang được Biên và Nhật Minh hoàn thiện theo `docs/task-breakdown.md`. Bản
console cũ (`--console`) vẫn giữ lại, chạy đầy đủ chức năng, dùng để đối chiếu khi
cần.

## 2. Chức năng chính

- **Quản lý thành viên:** thêm, xoá, tìm kiếm (theo ID / theo tên), cập nhật email-SĐT, liệt kê, sắp xếp theo tên.
- **Quản lý sự kiện:** tạo sự kiện, đăng ký thành viên tham gia, huỷ đăng ký, liệt kê/lọc theo trạng thái, cập nhật trạng thái.
- Mỗi loại sự kiện (Workshop, Competition, SocialEvent) tự tính phí tham gia theo cách riêng (polymorphism).
- Trang **Tổng quan**: số liệu nhanh (số thành viên, số sự kiện, số lượt đăng ký, doanh thu ước tính) + danh sách sự kiện sắp diễn ra + trạng thái kết nối database.
- Dữ liệu lưu trong **SQLite** (file `data/club.db`, tự tạo khi chạy lần đầu).

Toàn bộ chức năng trên đã được cài đặt đầy đủ và nối vào menu `ConsoleUI` — đây là bản
demo hoàn chỉnh đầu tiên (v1) của dự án.

## 3. Kiến trúc

Kiến trúc 5 tầng (thêm tầng `repository` so với bản đầu, đúng mẫu "có database"):

```text
model/        - các lớp dữ liệu (Person, Member, Event, ...)
repository/   - truy cập database bằng JDBC (MemberRepository, EventRepository, DatabaseConnection)
service/      - xử lý nghiệp vụ (MemberService, EventService, ClubService)
ui/           - giao diện Swing (MainFrame, DashboardPanel, MemberPanel, EventPanel...)
               + ConsoleUI (bản console cũ, giữ lại qua cờ --console)
exception/    - các exception tự định nghĩa
Main.java     - điểm khởi chạy, nối các tầng lại với nhau
```

**Luồng phụ thuộc** (tầng trên gọi tầng dưới, không có chiều ngược lại):
`ui` → `service` → `repository` → `DatabaseConnection` (SQLite).
`service` không biết SQL; `ui` không biết Repository — đổi cách lưu trữ (ví dụ sau
này đổi sang MySQL) chỉ cần sửa tầng `repository`, các tầng còn lại giữ nguyên.

## 4. Cấu trúc thư mục

```text
Club_management_system/
├── lib/                          # Thư viện ngoài (không phải code tự viết)
│   ├── sqlite-jdbc-3.46.1.0.jar
│   ├── slf4j-api-1.7.32.jar      # sqlite-jdbc cần để chạy
│   ├── slf4j-nop-1.7.32.jar      # (tắt log, không bắt buộc nhưng nên có)
│   └── flatlaf-3.7.2.jar         # giao diện Swing hiện đại
├── src/
│   └── clubmanagement/
│       ├── Main.java
│       ├── model/
│       │   ├── Person.java, Member.java, MembershipType.java
│       │   ├── Event.java, Workshop.java, Competition.java, SocialEvent.java
│       │   ├── EventStatus.java, Payable.java, Club.java
│       ├── repository/
│       │   ├── DatabaseConnection.java
│       │   ├── MemberRepository.java
│       │   └── EventRepository.java
│       ├── service/
│       │   ├── MemberService.java, EventService.java, ClubService.java
│       ├── ui/
│       │   ├── MainFrame.java, DashboardPanel.java
│       │   ├── MemberPanel.java, EventPanel.java   (+ các Dialog sẽ thêm)
│       │   ├── UiUtils.java, InputValidator.java, Refreshable.java
│       │   └── ConsoleUI.java                       (bản console cũ)
│       └── exception/
│           ├── DuplicateMemberException.java, MemberNotFoundException.java
│           ├── EventFullException.java, InvalidInputException.java
│           ├── EventNotFoundException.java, DuplicateEventException.java
│           └── DatabaseException.java
├── docs/
│   ├── class-diagram.md
│   ├── database.md               # sơ đồ bảng + quy tắc viết Repository
│   └── task-breakdown.md
├── data/                          # Tự tạo khi chạy, chứa file club.db (không commit)
├── run.sh                         # Script chạy nhanh (Linux/macOS)
├── run.bat                        # Script chạy nhanh (Windows)
└── README.md
```

## 5. Các khái niệm OOP thể hiện trong dự án

| Khái niệm | Nơi thể hiện |
|---|---|
| Encapsulation | Field trong `Person`, `Member`, `Event`... đều `private`/`protected` kèm getter/setter |
| Inheritance | `Member extends Person`; `Workshop/Competition/SocialEvent extends Event` |
| Polymorphism | `calculateFee()` khác nhau mỗi loại sự kiện; `EventRepository` đọc 1 dòng DB rồi tạo **đúng lớp con** tuỳ `event_type` |
| Abstraction | `Person` và `Event` là abstract class, chỉ định nghĩa khung chung |
| Interface | `Payable` (mọi Event tự tính phí), `Refreshable` (mọi trang Swing tự làm mới dữ liệu — `MainFrame` gọi qua interface, không cần biết bên trong là màn hình nào) |
| Composition/Association | `ClubService` giữ `Club`/`MemberService`/`EventService`; `Event` chứa `List<Member>` participants; `Service` giữ `Repository` |
| Exception handling | 7 exception tự định nghĩa, trong đó `DatabaseException` (unchecked) tách riêng lỗi hạ tầng khỏi lỗi nghiệp vụ |
| Collection Framework | `List<Member>`, `List<Event>` xuyên suốt tầng service và repository |

## 6. Cài đặt & chạy chương trình

**Yêu cầu:** JDK 17 trở lên (`java -version` để kiểm tra). Các thư viện cần thiết
(SQLite JDBC, FlatLaf...) đã có sẵn trong thư mục `lib/`, không cần tải thêm.

### Cách nhanh nhất — dùng script

```bash
# Linux / macOS
./run.sh              # giao diện đồ hoạ (Swing)
./run.sh --console     # bản console cũ

# Windows
run.bat
run.bat --console
```

### Chạy thủ công (nếu cần hiểu rõ từng bước)

```bash
cd Club_management_system
javac -encoding UTF-8 -cp "lib/*" -d out $(find src -name "*.java")

# Giao diện đồ hoạ
java -cp "out:lib/*" clubmanagement.Main       # Windows dùng dấu ; thay :

# Bản console
java -cp "out:lib/*" clubmanagement.Main --console
```

### Mở trong VS Code

Đã có sẵn `.vscode/settings.json` khai báo `lib/*.jar` là thư viện tham chiếu — mở
thư mục dự án bằng VS Code (cài "Extension Pack for Java"), bấm ▶ Run ở
`src/clubmanagement/Main.java` là chạy được ngay, không cần cấu hình gì thêm.

### Xem dữ liệu trong database

Dùng [DB Browser for SQLite](https://sqlitebrowser.org/) (miễn phí) mở file
`data/club.db` để xem/sửa dữ liệu trực tiếp — rất hữu ích lúc debug.

## 7. Thành viên nhóm

| Vai trò | Phụ trách |
|---|---|
| Anh Tú | Kiến trúc, hạ tầng database (`repository/DatabaseConnection`), khung giao diện (`MainFrame`, `DashboardPanel`, `UiUtils`, `InputValidator`), `Main.java`, `exception`, integration, code review |
| Biên | Module Thành viên — `MemberRepository.java`, `MemberService.java`, `MemberPanel.java`, `MemberFormDialog.java` |
| Nhật Minh | Module Sự kiện — `EventRepository.java`, `EventService.java`, `EventPanel.java`, `EventFormDialog.java`, `RegisterMemberDialog.java` |

## 8. Git workflow

```text
main
  |
  +-- feature/bien        (Biên - module thành viên)
  |
  +-- feature/nhat-minh   (Nhật Minh - module sự kiện)
```

- Không push trực tiếp vào `main`.
- Commit convention: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`.
- Không commit file `data/*.db` (đã có trong `.gitignore`) — mỗi máy tự sinh dữ liệu riêng.
- Chi tiết task xem tại `docs/task-breakdown.md`, sơ đồ database xem tại `docs/database.md`.
