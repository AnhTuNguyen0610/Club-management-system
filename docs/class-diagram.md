# Class Diagram

```mermaid
classDiagram
    class Person {
        <<abstract>>
        #String id
        #String name
        #String email
        #String phone
        +getRoleDescription() String
    }

    class Member {
        -MembershipType membershipType
        -LocalDate joinDate
        -boolean active
        +getRoleDescription() String
    }

    class MembershipType {
        <<enumeration>>
        REGULAR
        VIP
        HONORARY
        -double discountRate
    }

    class Payable {
        <<interface>>
        +calculateFee() double
    }

    class Event {
        <<abstract>>
        #String eventId
        #String eventName
        #String date
        #int maxParticipants
        #List~Member~ participants (chi doc ben ngoai)
        #EventStatus status
        +addParticipant(Member)
        +isFull() boolean
        +getEventTypeDescription() String
    }

    class Workshop {
        -double baseFee
        -String speaker
        +calculateFee() double
    }

    class Competition {
        -double entryFee
        -double prizeValue
        +calculateFee() double
        +calculateFee(Member) double
    }

    class SocialEvent {
        -String location
        +calculateFee() double
    }

    class EventStatus {
        <<enumeration>>
        UPCOMING
        ONGOING
        FINISHED
        CANCELLED
    }

    class Club {
        -String clubName
        -String description
    }

    class DatabaseConnection {
        -String dbFilePath
        +open() Connection
        +initSchema()
        +isConnected() bool
    }

    class MemberRepository {
        -DatabaseConnection db
        +insert(Member)
        +update(Member)
        +deleteById(String) bool
        +findById(String) Member
        +existsById(String) bool
        +findAll() List~Member~
        +searchByName(String) List~Member~
    }

    class EventRepository {
        -DatabaseConnection db
        +insert(Event)
        +updateStatus(String, EventStatus) bool
        +findById(String) Event
        +existsById(String) bool
        +findAll() List~Event~
        +findByStatus(EventStatus) List~Event~
        +addParticipant(String, String)
        +removeParticipant(String, String) bool
        +memberExists(String) bool
    }

    class MemberService {
        -MemberRepository memberRepository
        +addMember(Member)
        +removeMember(String)
        +searchMemberById(String) Member
        +searchMemberByName(String) List~Member~
        +listAllMembers() List~Member~
        +sortMembersByName() List~Member~
        +updateMember(String, String, String)  ~InvalidInputException~
    }

    class EventService {
        -EventRepository eventRepository
        +addEvent(Event)
        +registerMember(String, Member) bool
        +cancelRegistration(String, String) bool
        +listEvents() List~Event~
        +listEventsByStatus(EventStatus) List~Event~
        +findEventById(String) Event
        +updateEventStatus(String, EventStatus)
    }

    class ClubService {
        -Club club
        -MemberService memberService
        -EventService eventService
        +printClubSummary()
    }

    class Refreshable {
        <<interface>>
        +refresh()
    }

    class MainFrame {
        -ClubService clubService
        +MainFrame(ClubService, DatabaseConnection)
    }

    class DashboardPanel {
        +refresh()
    }

    class MemberPanel {
        -MemberService memberService
        +refresh()
    }

    class EventPanel {
        -EventService eventService
        -MemberService memberService
        +refresh()
    }

    class ConsoleUI {
        -Scanner scanner
        -ClubService clubService
        +run()
    }

    Person <|-- Member
    Event <|-- Workshop
    Event <|-- Competition
    Event <|-- SocialEvent
    Event ..|> Payable
    Member "1" --> "1" MembershipType
    Event "1" --> "1" EventStatus
    Event "1" o-- "many" Member : participants

    MemberRepository "1" --> "1" DatabaseConnection
    EventRepository "1" --> "1" DatabaseConnection
    MemberService "1" --> "1" MemberRepository
    EventService "1" --> "1" EventRepository

    ClubService "1" --> "1" Club
    ClubService "1" --> "1" MemberService
    ClubService "1" --> "1" EventService
    ConsoleUI "1" --> "1" ClubService

    DashboardPanel ..|> Refreshable
    MemberPanel ..|> Refreshable
    EventPanel ..|> Refreshable
    MainFrame "1" --> "1" ClubService
    MainFrame "1" o-- "many" Refreshable : cac trang
    MemberPanel "1" --> "1" MemberService
    EventPanel "1" --> "1" EventService
    EventPanel "1" --> "1" MemberService : chi doc
```

## Giải thích quan hệ chính

- **Inheritance**: `Member` kế thừa `Person`; `Workshop`, `Competition`, `SocialEvent` kế thừa `Event`.
- **Interface**: `Event` implement `Payable` → mỗi loại sự kiện override `calculateFee()` khác nhau (Polymorphism). `DashboardPanel`/`MemberPanel`/`EventPanel` cùng implement `Refreshable` → `MainFrame` gọi `refresh()` qua interface mà không cần biết bên trong là màn hình nào (Polymorphism ở tầng giao diện).
- **Composition**: `Event` sở hữu danh sách `participants` (List&lt;Member&gt;).
- **Association**: `ClubService` giữ tham chiếu tới `Club`, `MemberService`, `EventService`; `Service` giữ tham chiếu tới `Repository` tương ứng (không sở hữu vòng đời chặt — `Repository` chỉ là cổng vào database, dữ liệu thật nằm ngoài chương trình).
- **Tách tầng repository**: `Service` chỉ biết gọi method trên `Repository` (ví dụ `memberRepository.findAll()`), không biết bên trong dùng SQLite hay bất kỳ cách lưu trữ nào khác — nhờ vậy đổi công nghệ lưu trữ sau này (nếu cần) chỉ phải viết lại `Repository`, không đụng đến `Service` hay `ui`.
