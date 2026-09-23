# Hệ thống quản lý Câu lạc bộ

Bài tập lớn môn Lập trình hướng đối tượng (OOP) — Java thuần, không dùng framework.

## 1. Tổng quan

Ứng dụng console quản lý một Câu lạc bộ (CLB): quản lý thành viên và quản lý các
sự kiện do CLB tổ chức (workshop, cuộc thi, giao lưu).

## 2. Chức năng chính

- Quản lý thành viên: thêm, xoá, tìm kiếm (theo ID / theo tên), cập nhật, liệt kê, sắp xếp.
- Quản lý sự kiện: tạo sự kiện, đăng ký thành viên tham gia, huỷ đăng ký, liệt kê sự kiện theo trạng thái.
- Mỗi loại sự kiện (Workshop, Competition, SocialEvent) có cách tính phí tham gia khác nhau.
- Xem thông tin tổng quan của CLB.

## 3. Kiến trúc

Kiến trúc đơn giản 4 tầng, không dùng database (dữ liệu lưu trong bộ nhớ bằng
Collection Framework của Java):

```text
model/       - các lớp dữ liệu (Person, Member, Event, ...)
service/     - xử lý nghiệp vụ (MemberService, EventService, ClubService)
ui/          - giao diện console (ConsoleUI)
exception/   - các exception tự định nghĩa
Main.java    - điểm khởi chạy chương trình
```

## 4. Cấu trúc thư mục

```text
ClubManagement/
├── src/
│   └── clubmanagement/
│       ├── Main.java
│       ├── model/
│       │   ├── Person.java
│       │   ├── Member.java
│       │   ├── MembershipType.java
│       │   ├── Payable.java
│       │   ├── Event.java
│       │   ├── Workshop.java
│       │   ├── Competition.java
│       │   ├── SocialEvent.java
│       │   ├── EventStatus.java
│       │   └── Club.java
│       ├── service/
│       │   ├── MemberService.java
│       │   ├── EventService.java
│       │   └── ClubService.java
│       ├── ui/
│       │   └── ConsoleUI.java
│       └── exception/
│           ├── DuplicateMemberException.java
│           ├── MemberNotFoundException.java
│           ├── EventFullException.java
│           └── InvalidInputException.java
├── docs/
│   ├── class-diagram.md
│   └── task-breakdown.md
└── README.md
```

## 5. Các khái niệm OOP thể hiện trong dự án

| Khái niệm | Nơi thể hiện |
|---|---|
| Encapsulation | Tất cả field trong `Person`, `Member`, `Event`... đều `private`/`protected` kèm getter/setter |
| Inheritance | `Member extends Person`; `Workshop/Competition/SocialEvent extends Event` |
| Polymorphism | `calculateFee()` và `getEventTypeDescription()` được override khác nhau ở từng loại sự kiện |
| Abstraction | `Person` và `Event` là abstract class, chỉ định nghĩa khung chung |
| Interface | `Payable` — mọi `Event` phải tự tính được phí tham gia |
| Composition/Association | `ClubService` giữ tham chiếu đến `Club`, `MemberService`, `EventService`; `Event` chứa danh sách `Member` (participants) |
| Exception handling | 4 exception tự định nghĩa (`DuplicateMemberException`, `MemberNotFoundException`, `EventFullException`, `InvalidInputException`), được throw trong service và catch trong `ConsoleUI` |
| Collection Framework | `ArrayList<Member>`, `List<Event>` dùng để lưu trữ dữ liệu trong bộ nhớ |

## 6. Cài đặt & chạy chương trình

Yêu cầu: JDK 17 trở lên.

```bash
# Biên dịch
cd ClubManagement
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# Chạy chương trình
cd out
java clubmanagement.Main
```

## 7. Thành viên nhóm

| Vai trò | Phụ trách |
|---|---|
| Tech Lead (Anh Tú) | Kiến trúc, skeleton, tầng `ui`, `Main.java`, `exception`, integration, code review |
| Biên | Module Quản lý thành viên — `Member.java`, `MemberService.java` |
| Nhật Minh | Module Quản lý sự kiện — `Event*.java`, `EventService.java` |

## 8. Git workflow

```text
main
  |
develop
  |
  +-- feature/bien        (Biên - module thành viên)
  |
  +-- feature/nhat-minh   (Nhật Minh - module sự kiện)
```

- Không push trực tiếp vào `main`.
- Commit convention: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`.
- Anh Tú (Tech Lead) là người duy nhất merge `develop` → `main`.
- Chi tiết task xem tại `docs/task-breakdown.md`.
