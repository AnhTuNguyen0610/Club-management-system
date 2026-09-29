# Task Breakdown

> **Cập nhật (đợt GUI + Database):** dự án chuyển từ lưu trong bộ nhớ (console) sang
> lưu SQLite + giao diện Swing. Phần bên dưới (mục "Biên — Module Quản lý thành viên"
> và "Nhật Minh — Module Quản lý sự kiện") là các task đã hoàn thành ở bản console
> (Giai đoạn 1, giữ nguyên để tham khảo lịch sử). Task **MỚI** của đợt này nằm ở 2 mục
> "Giai đoạn 2" và "Giai đoạn 3" phía cuối file — **đọc từ đó để biết việc cần làm.**

## Biên — Module Quản lý thành viên

**File phụ trách:** `model/Member.java` (chỉ sửa nếu thật sự cần), `service/MemberService.java`
**Branch:** `feature/bien`

### Task 1.1 — Thêm & xoá thành viên ✅ DONE
- Method: `addMember(Member)`, `removeMember(String memberId)`
- Business rules:
  - `addMember`: member null hoặc id/name/email rỗng → `InvalidInputException`; id trùng → `DuplicateMemberException`.
  - `removeMember`: id không tồn tại → `MemberNotFoundException`.
- Edge cases: id trùng nhưng khác hoa/thường (mặc định coi là trùng), name/email chỉ toàn khoảng trắng.
- Acceptance criteria: thêm 2 member cùng id → lần 2 phải bị chặn; xoá id không tồn tại → bị chặn với thông báo rõ ràng.
- Commit message: `feat: implement addMember and removeMember in MemberService`

### Task 1.2 — Tìm kiếm thành viên ✅ DONE
- Method: `searchMemberById(String)`, `searchMemberByName(String keyword)`
- Business rules:
  - `searchMemberById`: không tìm thấy → `MemberNotFoundException`.
  - `searchMemberByName`: tìm gần đúng, không phân biệt hoa/thường; không tìm thấy → trả về danh sách rỗng (không throw).
- Acceptance criteria: tìm theo id đúng trả về đúng member; tìm theo tên "an" tìm ra cả "Nguyễn Văn An" lẫn "Anh".
- Commit message: `feat: implement member search by id and name`

### Task 1.3 — Liệt kê, sắp xếp, cập nhật ✅ DONE
- Method: `listAllMembers()`, `sortMembersByName()`, `updateMember(String, String, String)`
- Business rules:
  - `listAllMembers`/`sortMembersByName` trả về **bản sao** danh sách (không trả về tham chiếu gốc).
  - `updateMember`: id không tồn tại → `MemberNotFoundException`.
- Acceptance criteria: sửa danh sách trả về từ `listAllMembers()` không làm thay đổi dữ liệu gốc trong service.
- Commit message: `feat: implement list, sort and update member`

> Ghi chú của Tech Lead: cả 3 task của module Thành viên đã cài đặt đầy đủ và đã được
> nối vào `ConsoleUI` (menu Quản lý thành viên có đủ 7 chức năng). Milestone "khung đầu
> tiên" của module này đã hoàn thành.

---

## Nhật Minh — Module Quản lý sự kiện

**File phụ trách:** `model/Workshop.java`, `model/Competition.java`, `model/SocialEvent.java`, `service/EventService.java`
**Branch:** `feature/nhat-minh`

### Task 2.1 — Tính phí sự kiện (Polymorphism) ✅ DONE
- Method: `Workshop.calculateFee()`, `Competition.calculateFee()`, `SocialEvent.calculateFee()`
- Business rules (bản chính thức, đã đính chính lại rule "early-bird" cho đúng nghĩa):
  - Workshop: phí = `baseFee`; `fillRate = participants.size() / (double) maxParticipants`; nếu `fillRate <= 0.2` (đang ở giai đoạn 20% suất đăng ký đầu tiên) → giảm 10%. Đây là mức giá hiện tại tính theo thời điểm gọi hàm, không phải giá "chốt" lúc đăng ký.
  - Competition: `calculateFee()` (không tham số) = `entryFee`; có thêm overload `calculateFee(Member member)` áp dụng giảm giá theo `MembershipType.getDiscountRate()` cho từng thành viên cụ thể.
  - SocialEvent: mặc định miễn phí → trả về 0.
- Acceptance criteria: gọi `calculateFee()` trên từng loại event cho ra kết quả khác nhau đúng rule.
- Commit message: `feat: implement calculateFee for Workshop, Competition, SocialEvent`

### Task 2.2 — Quản lý danh sách sự kiện ✅ DONE
- Method: `addEvent(Event)`, `listEvents()`, `listEventsByStatus(EventStatus)`, `findEventById(String)`
- Business rules:
  - `addEvent`: eventId trùng → `DuplicateEventException` (class đã được thêm chính thức vào `exception/`).
  - `listEvents`/`listEventsByStatus` trả về bản sao danh sách; `findEventById` trả `null` nếu không có, so sánh id bằng `.equalsIgnoreCase()` (không dùng `==` cho String).
- Acceptance criteria: thêm 3 event thuộc 3 loại khác nhau, lọc theo `UPCOMING` phải ra đúng số lượng.
- Commit message: `feat: implement event listing and lookup`

### Task 2.3 — Đăng ký & huỷ đăng ký thành viên ✅ DONE
- Method: `registerMember(String eventId, Member member)`, `cancelRegistration(String eventId, String memberId)`
- Business rules:
  - Event không tồn tại → `EventNotFoundException` (class riêng, đã thêm chính thức, không dùng `MemberNotFoundException` cho trường hợp này nữa vì gây nhầm lẫn khi đọc log lỗi).
  - Đã đủ số lượng (`participants.size() >= maxParticipants`) → `EventFullException`.
  - Member đã đăng ký rồi → không thêm trùng.
  - `cancelRegistration`: nếu event/member không tồn tại → bỏ qua, không throw.
- Acceptance criteria: đăng ký đến khi đầy chỗ → lần tiếp theo bị chặn bởi `EventFullException`.
- Commit message: `feat: implement event registration and cancellation`

> Ghi chú của Tech Lead: tất cả các method trên module Sự kiện đã được cài đặt đầy đủ và
> nối vào `ConsoleUI` (menu Quản lý sự kiện có đủ 6 chức năng). Milestone "khung đầu tiên"
> của module này đã hoàn thành.

---

## Nguyên tắc chung khi làm task (Giai đoạn 1 — bản console, đã xong)

- Biên chỉ sửa file trong module của mình (`Member.java`, `MemberService.java`); Nhật Minh chỉ sửa file trong module của mình (`Event*.java`, `EventService.java`) để hạn chế conflict khi merge.
- Trước khi bắt đầu, cả hai đọc kỹ interface/abstract class liên quan (đã được Anh Tú — Tech Lead chốt trong skeleton).
- Test thủ công qua `ConsoleUI` trước khi tạo Pull Request từ nhánh của mình vào `develop`.
- Không tự ý đổi signature của method đã có trong skeleton — nếu thấy cần đổi, trao đổi với Anh Tú trước.
- Anh Tú (Tech Lead) chịu trách nhiệm review, merge `develop` → `main` và xử lý conflict nếu phát sinh giữa hai nhánh.

---
---

# 🚀 Giai đoạn 2 — Chuyển dữ liệu sang SQLite (JDBC)

**Mục tiêu:** `MemberService`/`EventService` đọc/ghi qua Repository (SQLite) thay vì `ArrayList`, để tắt chương trình xong mở lại **không mất dữ liệu**.

Anh Tú (Tech Lead) đã dựng sẵn toàn bộ hạ tầng — cả nhóm **chỉ việc điền vào chỗ trống**, không cần tự thiết kế:

| Đã có sẵn (đừng động vào trừ khi trao đổi trước) | Việc còn lại của Biên/Nhật Minh |
|---|---|
| `repository/DatabaseConnection.java` — mở kết nối, tạo bảng, có sẵn mẫu code JDBC chuẩn trong Javadoc | Điền phần thân method còn `TODO` |
| `docs/database.md` — sơ đồ bảng, quy tắc bắt buộc (PreparedStatement, try-with-resources...) | Đọc kỹ trước khi viết dòng SQL đầu tiên |
| `repository/MemberRepository.java`, `repository/EventRepository.java` — đã chốt sẵn **chữ ký method**, chỉ thiếu phần thân | Viết phần thân |
| `MemberService`/`EventService` — đã có constructor mới nhận `Repository`, còn field `List` cũ | Chuyển từng method từ thao tác trên `List` sang gọi `Repository`, rồi xoá field `List` |
| `exception/DatabaseException.java` | Ném exception này khi bắt `SQLException` (xem mẫu) |

### Task B2.1 — Biên: cài đặt `MemberRepository`
- File: `repository/MemberRepository.java` (**file mới**, không đụng file của Nhật Minh)
- Điền phần thân 7 method đã có sẵn chữ ký: `insert`, `update`, `deleteById`, `findById`, `existsById`, `findAll`, `searchByName`, `count`.
- Bắt buộc đọc `docs/database.md` mục "Bảng members" và "Quy tắc viết Repository" trước khi viết.
- Acceptance criteria: viết 1 file `MemberRepositoryTest` (hoặc test tay qua `main` tạm) — insert 2 member, `findAll()` phải ra đúng 2; `existsById("m1")` sau khi insert `"M1"` phải trả `true` (kiểm tra không phân biệt hoa/thường).
- Commit message: `feat(db): implement MemberRepository with JDBC`

### Task B2.2 — Biên: nối `MemberService` vào `MemberRepository`
- File: `service/MemberService.java`
- Sửa từng method (`addMember`, `removeMember`, `searchMemberById`, `searchMemberByName`, `listAllMembers`, `sortMembersByName`, `updateMember`) để gọi `memberRepository.xxx(...)` thay vì thao tác trên field `members` (List).
- **Giữ nguyên 100% chữ ký method và exception đã ném** — `EventPanel`/`ConsoleUI` gọi các method này, đổi chữ ký sẽ làm mọi thứ khác vỡ.
- Sau khi xong, **xoá** field `private List<Member> members` và `import java.util.ArrayList` nếu không còn dùng.
- Acceptance criteria: chạy `./run.sh --console`, thêm 1 thành viên, tắt chương trình, mở lại, vào "Liệt kê tất cả thành viên" — thành viên vẫn còn.
- Commit message: `feat(db): wire MemberService to MemberRepository`

### Task N2.1 — Nhật Minh: cài đặt `EventRepository`
- File: `repository/EventRepository.java` (**file mới**)
- Điền phần thân 8 method: `insert`, `updateStatus`, `findById`, `existsById`, `findAll`, `findByStatus`, `addParticipant`, `removeParticipant`, `count`.
- Đọc kỹ `docs/database.md` mục "Bảng events" (đặc biệt là bảng ánh xạ loại sự kiện → cột nào dùng) và "Bảng event_participants".
- Chú ý: khi đọc 1 dòng từ bảng `events` lên, phải tạo **đúng lớp con** (`new Workshop(...)` / `new Competition(...)` / `new SocialEvent(...)`) dựa vào cột `event_type`, rồi gọi `setStatus(...)` — đây chính là nơi thể hiện tính **đa hình khi nạp dữ liệu**, sẽ được hỏi lúc bảo vệ.
- Acceptance criteria: insert 1 Workshop + 1 Competition, `findAll()` phải trả về đúng kiểu con tương ứng (kiểm tra bằng `instanceof`), gọi `calculateFee()` ra đúng kết quả.
- Commit message: `feat(db): implement EventRepository with JDBC`

### Task N2.2 — Nhật Minh: nối `EventService` vào `EventRepository`
- File: `service/EventService.java`
- Sửa từng method (`addEvent`, `registerMember`, `cancelRegistration`, `listEvents`, `listEventsByStatus`, `findEventById`) để gọi `eventRepository.xxx(...)` thay vì field `events` (List).
- Cài đặt thêm method **mới** `updateEventStatus(String eventId, EventStatus status)` (đã có chữ ký sẵn, `throws EventNotFoundException`) — gọi `eventRepository.updateStatus(...)`, ném `EventNotFoundException` nếu không tồn tại.
- Giữ nguyên chữ ký các method cũ. Sau khi xong, xoá field `List<Event> events`.
- Acceptance criteria: đăng ký 1 thành viên vào sự kiện, tắt/mở lại chương trình, sự kiện đó vẫn hiện đúng người tham gia.
- Commit message: `feat(db): wire EventService to EventRepository`

---
---

# 🎨 Giai đoạn 3 — Giao diện Swing

**Mục tiêu:** thay `ConsoleUI` bằng giao diện đồ họa. Anh Tú đã dựng khung `MainFrame` (thanh điều hướng bên trái, 3 trang: Tổng quan / Thành viên / Sự kiện) và bộ tiện ích `UiUtils` (màu sắc, nút bấm, style bảng, hộp thoại) + `InputValidator` (kiểm tra dữ liệu form) dùng chung — **không cần tự vẽ style, chỉ cần gọi**.

Chạy thử khung hiện tại: `./run.sh` (Windows: `run.bat`). Trang Tổng quan đã chạy thật; trang Thành viên/Sự kiện đang hiện "đang chờ hoàn thiện" — đó là 2 màn hình của Biên và Nhật Minh.

### Task B3.1 — Biên: màn hình `MemberPanel`
- File: `ui/MemberPanel.java` (đã có sẵn khung + hướng dẫn chi tiết trong Javadoc đầu file — đọc kỹ trước khi code) và file **mới** `ui/MemberFormDialog.java` (hộp thoại thêm/sửa).
- Yêu cầu giao diện, hành vi, tiêu chí nghiệm thu: xem Javadoc trong chính file `MemberPanel.java` (Anh Tú đã ghi rất chi tiết, không lặp lại ở đây).
- Dùng `InputValidator` để kiểm tra form, dùng `UiUtils.showError/showWarning/confirm` để báo lỗi — không tự viết `JOptionPane` tay.
- Acceptance criteria: 4 tiêu chí đã liệt kê trong Javadoc của `MemberPanel.java`.
- Commit message: `feat(ui): implement MemberPanel and MemberFormDialog`

### Task N3.1 — Nhật Minh: màn hình `EventPanel`
- File: `ui/EventPanel.java` (đã có khung + hướng dẫn chi tiết trong Javadoc) và 2 file **mới** `ui/EventFormDialog.java`, `ui/RegisterMemberDialog.java`.
- Yêu cầu giao diện, hành vi, tiêu chí nghiệm thu: xem Javadoc trong `EventPanel.java`.
- Cần đọc danh sách thành viên để chọn khi đăng ký → gọi `memberService.listAllMembers()` (chỉ **đọc**, không sửa `MemberService`).
- Acceptance criteria: 4 tiêu chí đã liệt kê trong Javadoc của `EventPanel.java`.
- Commit message: `feat(ui): implement EventPanel, EventFormDialog and RegisterMemberDialog`

---
---

# 🧹 Giai đoạn 4 — Polish & tổng duyệt (cả nhóm)

- Rà lại toàn bộ: không còn `System.out.println` nào để báo lỗi cho người dùng trong `ui/` (phải dùng `UiUtils.showError`/`showWarning`).
- Test lại đầy đủ 2 checklist "Tiêu chí nghiệm thu" trong Javadoc của `MemberPanel` và `EventPanel`.
- Tắt hẳn chương trình, xoá thư mục `data/`, mở lại từ đầu — đảm bảo bảng tự tạo lại không lỗi (kiểm tra `initSchema()` chạy được nhiều lần).
- Diễn tập demo 2 lần, phân công ai trả lời phần JDBC/DB khi bảo vệ (phần mới, dễ bị hỏi sâu — đọc câu hỏi mẫu cuối `docs/database.md`).
- Cập nhật ảnh chụp màn hình mới vào README nếu có thời gian.

---

## Nguyên tắc chung — Giai đoạn 2 & 3

- Vẫn giữ đúng ranh giới module: Biên chỉ sửa `MemberRepository.java`, `MemberService.java`, `MemberPanel.java`, `MemberFormDialog.java`; Nhật Minh chỉ sửa `EventRepository.java`, `EventService.java`, `EventPanel.java`, `EventFormDialog.java`, `RegisterMemberDialog.java`.
- **Làm xong Giai đoạn 2 (DB) trước, test kỹ bằng `./run.sh --console`, rồi mới sang Giai đoạn 3 (GUI).** Đừng đổi cả hai cùng lúc — lỗi ở tầng nào sẽ khó biết nếu đổi chung.
- `MainFrame.java`, `UiUtils.java`, `InputValidator.java`, `DatabaseConnection.java` do Anh Tú quản lý — cần đổi thì trao đổi trước, đừng tự sửa (dễ đụng nhau vì cả hai module đều dùng chung các file này).
- Commit thường xuyên hơn giai đoạn trước (khối lượng thay đổi lớn hơn) để tránh conflict khi merge.
