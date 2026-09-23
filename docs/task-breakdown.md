# Task Breakdown

## Biên — Module Quản lý thành viên

**File phụ trách:** `model/Member.java` (chỉ sửa nếu thật sự cần), `service/MemberService.java`
**Branch:** `feature/bien`

### Task 1.1 — Thêm & xoá thành viên
- Method: `addMember(Member)`, `removeMember(String memberId)`
- Business rules:
  - `addMember`: member null hoặc id/name/email rỗng → `InvalidInputException`; id trùng → `DuplicateMemberException`.
  - `removeMember`: id không tồn tại → `MemberNotFoundException`.
- Edge cases: id trùng nhưng khác hoa/thường (mặc định coi là trùng), name/email chỉ toàn khoảng trắng.
- Acceptance criteria: thêm 2 member cùng id → lần 2 phải bị chặn; xoá id không tồn tại → bị chặn với thông báo rõ ràng.
- Commit message: `feat: implement addMember and removeMember in MemberService`

### Task 1.2 — Tìm kiếm thành viên
- Method: `searchMemberById(String)`, `searchMemberByName(String keyword)`
- Business rules:
  - `searchMemberById`: không tìm thấy → `MemberNotFoundException`.
  - `searchMemberByName`: tìm gần đúng, không phân biệt hoa/thường; không tìm thấy → trả về danh sách rỗng (không throw).
- Acceptance criteria: tìm theo id đúng trả về đúng member; tìm theo tên "an" tìm ra cả "Nguyễn Văn An" lẫn "Anh".
- Commit message: `feat: implement member search by id and name`

### Task 1.3 — Liệt kê, sắp xếp, cập nhật
- Method: `listAllMembers()`, `sortMembersByName()`, `updateMember(String, String, String)`
- Business rules:
  - `listAllMembers`/`sortMembersByName` trả về **bản sao** danh sách (không trả về tham chiếu gốc).
  - `updateMember`: id không tồn tại → `MemberNotFoundException`.
- Acceptance criteria: sửa danh sách trả về từ `listAllMembers()` không làm thay đổi dữ liệu gốc trong service.
- Commit message: `feat: implement list, sort and update member`

---

## Nhật Minh — Module Quản lý sự kiện

**File phụ trách:** `model/Workshop.java`, `model/Competition.java`, `model/SocialEvent.java`, `service/EventService.java`
**Branch:** `feature/nhat-minh`

### Task 2.1 — Tính phí sự kiện (Polymorphism)
- Method: `Workshop.calculateFee()`, `Competition.calculateFee()`, `SocialEvent.calculateFee()`
- Business rules:
  - Workshop: phí = `baseFee`; nếu số người đăng ký > 80% `maxParticipants` → giảm 10%.
  - Competition: phí = `entryFee` (có thể áp dụng giảm giá theo `MembershipType` nếu nhóm muốn mở rộng).
  - SocialEvent: mặc định miễn phí → trả về 0.
- Acceptance criteria: gọi `calculateFee()` trên từng loại event cho ra kết quả khác nhau đúng rule.
- Commit message: `feat: implement calculateFee for Workshop, Competition, SocialEvent`

### Task 2.2 — Quản lý danh sách sự kiện
- Method: `addEvent(Event)`, `listEvents()`, `listEventsByStatus(EventStatus)`, `findEventById(String)`
- Business rules: `listEvents`/`listEventsByStatus` trả về bản sao danh sách; `findEventById` trả `null` nếu không có.
- Acceptance criteria: thêm 3 event thuộc 3 loại khác nhau, lọc theo `UPCOMING` phải ra đúng số lượng.
- Commit message: `feat: implement event listing and lookup`

### Task 2.3 — Đăng ký & huỷ đăng ký thành viên
- Method: `registerMember(String eventId, Member member)`, `cancelRegistration(String eventId, String memberId)`
- Business rules:
  - Event không tồn tại → `MemberNotFoundException` (hoặc tự tạo `EventNotFoundException` nếu nhóm muốn rõ ràng hơn).
  - Đã đủ số lượng (`participants.size() >= maxParticipants`) → `EventFullException`.
  - Member đã đăng ký rồi → không thêm trùng.
- Acceptance criteria: đăng ký đến khi đầy chỗ → lần tiếp theo bị chặn bởi `EventFullException`.
- Commit message: `feat: implement event registration and cancellation`

---

## Nguyên tắc chung khi làm task

- Biên chỉ sửa file trong module của mình (`Member.java`, `MemberService.java`); Nhật Minh chỉ sửa file trong module của mình (`Event*.java`, `EventService.java`) để hạn chế conflict khi merge.
- Trước khi bắt đầu, cả hai đọc kỹ interface/abstract class liên quan (đã được Anh Tú — Tech Lead chốt trong skeleton).
- Test thủ công qua `ConsoleUI` trước khi tạo Pull Request từ nhánh của mình vào `develop`.
- Không tự ý đổi signature của method đã có trong skeleton — nếu thấy cần đổi, trao đổi với Anh Tú trước.
- Anh Tú (Tech Lead) chịu trách nhiệm review, merge `develop` → `main` và xử lý conflict nếu phát sinh giữa hai nhánh.
