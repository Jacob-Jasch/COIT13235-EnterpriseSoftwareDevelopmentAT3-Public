# COIT13235 Enterprise Software Development - Assignment 3 Project

**Code:** 00219C | **RTO Code:** 40939

## Overview

You will work as part of a team to analyse requirements, design, implement, build, test, and document an application using an enterprise framework and quality processes.

## Project Structure

### Groupwork
- You must work in groups of 4 to 6 people
- Your group may include people from other campuses

### Repository
- Create a private Git code repository
- Invite your tutor and the unit coordinator
- One code repository is to be used by all group members
- Each member of the group is expected to make regular contributions to Git

### Due and Return
- The project is due in Weeks 8-11
- Feedback will be returned on the day of certification of grades

### Submission Overview
- Submit artefacts to your private Git repository weekly from Week 8 (or earlier)
- By the due date, submit a link to your private repository to the unit website
- All group members must be identified in the groupwork artefacts

## Scenario

### Client Requests

You are to develop a X/Twitter+Spotify system that shall allow you to chat with your friends and share audiotracks. To encourage the development of vertical slices of functionality, only the following requests should be considered for the first two weeks of the project:

#### Initial Requirements (Weeks 8-9):

- **Week 8:** That system shall allow you to chat with your friends
- **Week 9:** The system shall store audio files. The system shall allow you to share audiotrack playlists
- The system shall support either a web or console user interface. This request is strengthened in Week 10
- The testing of web user interfaces does not need to be automated, however, console user interfaces should have automated testing
- Your web user interface may be primarily text based. That is, your HTML forms may use text input fields in lieu of features such as checkboxes, radio buttons and drop down menus. CSS styling is encouraged but there are no associated marks
- You may use in-memory H2 database(s) and you may load hardcoded data into your database(s) using ApplicationRunners
- The system only needs to support 1 user. This request is deprecated in Week 10
- All web user interfaces must use the Thymeleaf web template engine
- The system must use Spring Data JPA to persist data
- The system must be built using Maven

#### Extended Requirements (After Week 9):

- The system shall allow you to rate audiotracks and maintain a list of favourite audiotracks and artists. The system shall use an external public API such as iTunes to retrieve information about audiotracks
- The system shall enable 10 users to be active simultaneously
- The system shall enable chat functionality to be scaled separately from the audio functionality
- The system shall support both web and console user interfaces
- You may use the console app for automated testing of your user interface instead of automated testing of your web app's user interface
- The system shall enable the web or console user interface to be replaced by native mobile application user interfaces
- The system shall enable the database to be replaced
- Each user action shall be authorised

## Team Management

### Managers

Your group should choose different people to be:

- **Project manager/Group manager:** Oversees project planning and people coordination. Your group manager should have excellent communication and organisational skills
- **Technical manager:** Responsible for managing technical decisions, including requirements analysis and the design of the architecture
- **Quality manager:** Manages testing, ensures adherence to quality standards and manages bug tracking and resolution

## Project Components

### Project Management (8 person hours)

Your group must manage project aspects including risks and schedule. Your group should at least:

- Maintain a list of risks with a weekly review of those risks. The five core risks of ICT projects are:
  - Schedule flaws
  - Incomplete or incorrect specifications
  - Poor productivity
  - Scope creep, e.g. someone indiscriminately adding new features
  - Staff turnover

- Develop a weekly sprint plan. You must break the project up into vertical slices of functionality (use cases):
  - In your first week, you must implement at least one complete use case related to chat. Your first week prototype(s) must contain either a web or console user interface with persisted data
  - In your second week, you must implement at least one complete use case related to music

Each week, the project manager must submit the following to your private Git repository:

- Project management artefacts including weekly sprint plan and updated risk register
- 5 minute video reviewing the project management status using the project management artefacts
- A video of the group's task allocation negotiations which shows members choosing their tasks

### Requirements Analysis and Design (12 person hours)

You are expected to conduct an analysis of the systems requirements. This includes identifying and specify both the functional and nonfunctional requirements and design constraints and assumptions. The requirements should be captured use case diagrams, high level class diagrams, a simple ERD, component diagrams and/or deployment diagrams. The purpose of these requirements is to help us understand what the system is intended to do and to provide an understanding of the scope of the system. The requirements should remain technology agnostic and must not include any design choices. There is no need to provide detailed user stories or legal and ethical considerations.

You should create a set of design artefacts. These artefacts are expected to include detailed class diagrams, activity diagrams, state diagrams, object diagrams, detailed ERDs, sequence diagrams and/or user interface wireframe diagrams.

You are expected to use generative AI to assist in drafting your diagrams. However, you must critically evaluate the AI-generated output. Submitting diagrams that are conflicting, incorrect, vague or confusing is worse than no diagram.

Each week, the technical manager must submit the following to your Git repository:

- Updated requirements and design artefacts
- 5 minute video explaining your updated requirements analysis and design using your diagrams

### Implementation (32 person hours)

Each week you should submit your updated code to your private Git repository. All submitted code should be of high quality, for example, well commented, tested, with excellent layout and variable naming.

Each week you should submit a working system. Each week your service(s) need to be completely designed, implemented, tested and documented.

You are encouraged to search for and reuse quality code in this unit. You are expected to use generative AI to help draft low-level Spring code. However, generative AI tools often produce poor quality and deprecated Spring code. Generated code should be critically evaluated and edited to align with the conventions of the unit. All reused code must be referenced. For websites and web services, including a commented URL within the reused code is considered a sufficient reference. Deprecated code must not be used. Sharing of artefacts between groups is not permitted.

### Peer Programming (8 person hours)

By the end of the project, each member of your group should (each) record a 30 minute peer programming session in which you are coding while at least two other member(s) of your group help you.

### Test (16 person hours)

Your group must develop unit and user acceptance test suites. For simplicity, you can include your test code alongside your production code. For example, you could provide an endpoint, for example, GET /tests, in your web service(s) to execute tests. Document your test suite in your code.

Each week, the quality manager should submit a 5 minute video for the purpose of user acceptance to your Git repository. The video should demonstrate:

- The newly implemented use cases and nonfunctional requirements
- The automated tests running and discuss test coverage detailing what has and has not been tested

### Demonstration of Understanding (4 person hours)

By the end of the project, each group member should choose a (different) use case and record a 10 minute video explaining the use case including the design, implementation and testing. You should explain using the requirements analysis and design artefacts, e.g. UML diagrams, and implementation and testing code.

### Individual Contributions (4 person hours)

By the end of the project, you must provide a written summary of your individual contributions. The group must collate these summaries and all members must submit the same summary.

## Submission Schedule

| Week | Member | PM | TM | QM | PP | DoU | IC |
|------|--------|----|----|----|----|-----|-----|
| 8    | ✓      | ✓  | ✓  | ✓  |    |     |     |
| 9    | ✓      | ✓  | ✓  | ✓  |    |     |     |
| 10   | ✓      | ✓  | ✓  | ✓  |    |     |     |
| 11   | ✓      | ✓  | ✓  | ✓  | ✓  | ✓   | ✓   |

### Submission Legend:

- **Member:** each week, each member of the group should update project management, requirements, design, coding and testing artefacts
- **PM:** each week, the project manager must submit a new sprint plan, an updated risk register, a 5 minute project management review video, and a group video showing task negotiations
- **TM:** each week, the technical manager should submit updated requirement analysis and designs artefacts including diagrams and a 5 minute video describing the updates to the requirements and designs
- **QM:** each week, the quality manager should submit a 5 minute video demonstrating new use cases, demonstrating the automated test cases and discussing test coverage
- **PP:** by the end of the project, each member should submit a 30 minute peer programming video
- **DoU:** by the end of the project each member should submit a 10 minute video explaining the design, implementation and testing of a use case
- **IC:** by the end of the project, the group should submit a summary of individual contributions

## Assessment Criteria

The assignment criteria includes documentation quality (design, code and test), software implementation correctness and project management artefacts. There are cross-cutting aspects of each criteria including completeness of implementation, process quality, groupwork and code compilation and execution. For example, to be eligible for full marks, you must have evidence of quality contributions to your team's private Git over 4 weeks, your code must run and execute, you have followed quality processes, your group must have coordinated well, and all requirements must be implemented.

### Detailed Criteria

| Criteria | Indicative of 100% | 75% | 50% | 25% | 0% |
|----------|-------------------|-----|-----|-----|-----|
| **Requirements analysis & Design** | Quality, clear communicated requirements analysis & design artefacts. E.g. excellent use case, deployment, class, sequence, activity & ERD diagrams; E.g. 4+ TM videos which explain design clearly | Issues with reqs/design docs or comms, e.g. unclear design due to missing diagrams; poor TM videos; a major req. not designed &/or communicated, e.g. security. Unhelpful diagrams included. | | | |
| **Project management** | Quality, clearly communicated PM plans including sprint plans & risks, e.g. 4+ quality sprint plans with task decomposition & risk plan updates with responses; 4+ PM videos | PM issues, e.g. poor sprint planning, e.g. not vertical slices or insufficient task breakdown; poor PM videos; poor risk reviews | | | |
| **Implementation** | Quality code; well commented; Git; 4+ QM videos review & demo of working UCs | Poor QM's videos; deprecated or poor code, e.g. Autowired | | | |
| **Testing** | Quality, clearly communicated testing suite; quality code; well commented; Git; excellent test coverage; 4+ QM video review & demo of automated testing | Poor QM's videos; poor test coverage | | | |
| **Peer programming & DoU (UC demo)** | 30 min video of peer programming. 5min explanation of use case demonstrates deep understanding of system's reqs, design, code & testing. | Missing DoU or PP video. E.g. superficial coverage of ERD or code for DoU. E.g. failed to discuss relevant NFRs in DoU | | | |

### Cross-cutting Criteria

| Criteria | Indicative of 100% | 75% | 50% | 25% | 0% |
|----------|-------------------|-----|-----|-----|-----|
| **Features implemented** | All NFR & functional requirements implemented | E.g. missing several NFRs, e.g. security, evolvability or elasticity or several fun reqs | | | No fun. reqs or NFRs implemented |
| **Quality processes & groupwork** | Coordination & quality processes with evidence, e.g. regular Git; code standards; multiple realistic designs; artefacts regularly updated (e.g. reqs, design & risks); 4+ PM, TM, QM review & task negotiation videos; all peer programming videos; chat histories; videos of meetings | Issues with processes or groupwork, e.g. poor task negotiations; TM coded majority; missing some peer programming; irregular reviews of design or risks | Major issues, e.g. little group comms; poor evidence of quality processes, e.g. single Git commit; out of date designs | | |
| **Individual contributions** | Excellent individual contributed contributions with evidence, e.g. Git, videos & group summary | Okay contributions but lacking evidence, e.g. no group summary or tutor must verify contributions | Few individual contributions, e.g. evidenced by group summary | | |

### Final Mark Calculation

**The final mark is the average of the criteria multiplied by the average of the cross-cuts.**
