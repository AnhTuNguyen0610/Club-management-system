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
        #List~Member~ participants
        #EventStatus status
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

    class MemberService {
        -List~Member~ members
        +addMember(Member)
        +removeMember(String)
        +searchMemberById(String) Member
        +searchMemberByName(String) List~Member~
        +listAllMembers() List~Member~
        +sortMembersByName() List~Member~
        +updateMember(String, String, String)
    }

    class EventService {
        -List~Event~ events
        +addEvent(Event)
        +registerMember(String, Member)
        +cancelRegistration(String, String)
        +listEvents() List~Event~
        +listEventsByStatus(EventStatus) List~Event~
        +findEventById(String) Event
    }

    class ClubService {
        -Club club
        -MemberService memberService
        -EventService eventService
        +printClubSummary()
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
    MemberService "1" o-- "many" Member
    EventService "1" o-- "many" Event
    ClubService "1" --> "1" Club
    ClubService "1" --> "1" MemberService
    ClubService "1" --> "1" EventService
    ConsoleUI "1" --> "1" ClubService
```

## Giải thích quan hệ chính

- **Inheritance**: `Member` kế thừa `Person`; `Workshop`, `Competition`, `SocialEvent` kế thừa `Event`.
- **Interface**: `Event` implement `Payable` → mỗi loại sự kiện override `calculateFee()` khác nhau (Polymorphism).
- **Composition**: `Event` sở hữu danh sách `participants` (List&lt;Member&gt;); `MemberService`/`EventService` sở hữu danh sách đối tượng quản lý — nếu service bị huỷ thì danh sách cũng mất theo.
- **Association**: `ClubService` giữ tham chiếu tới `Club`, `MemberService`, `EventService` để điều phối, nhưng không sở hữu vòng đời của chúng theo kiểu composition chặt.
