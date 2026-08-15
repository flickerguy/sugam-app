# Sugam — Project Plan

**Document:** `PROJECT_PLAN.md`  
**Purpose:** Development roadmap, milestones, progress, and next steps  
**Current phase:** Phase 1 — Local Android application  
**Last updated:** 2026-08-15

## 1. Project

**Application:** Sugam

Sugam is a physiotherapy clinic scheduling Android application.

The initial version is designed for a doctor using the application on one Android phone. It will later evolve into a multi-user, cloud-backed system.

## 2. Development Approach

The application is being built incrementally.

- Do not dump a complete codebase.
- Teach Android concepts while implementing features.
- Introduce technologies only when needed.
- Explain important Android code.
- Use small implementation checkpoints.
- Run the application frequently.
- Keep architecture production-oriented without over-engineering Phase 1.

## 3. Phase 1 Scope

Phase 1 is:

- Android-only
- Local-first
- Single user initially
- No login
- No authentication
- No REST API
- No cloud backend
- No AWS integration
- Room/SQLite persistence

Core features:

1. Dashboard
2. Appointment management
3. Patient management
4. Patient appointment history
5. Physio/assistant management
6. Appointment statuses
7. Patient search
8. Double-booking prevention
9. Clinic settings

Appointment statuses:

```text
SCHEDULED
CONFIRMED
COMPLETED
CANCELLED
NO_SHOW
```

## 4. Technology Stack

| Area | Technology |
|---|---|
| IDE | Android Studio |
| Language | Kotlin |
| UI | Jetpack Compose |
| Design | Material 3 |
| Architecture | MVVM |
| Persistence | Room / SQLite |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines |
| State | StateFlow / Flow |
| Build | Gradle / Kotlin DSL |

Dependency injection starts simple. Hilt may be introduced later if justified.

## 5. Development Environment

Completed:

- Android Studio setup
- Sugam Android project
- Kotlin + Jetpack Compose
- Physical OnePlus Nord 5
- Wireless debugging
- API 33 development device
- Successful builds and phone testing

Project configuration:

```text
App name:       Sugam
Package name:   com.example.sugam
Language:       Kotlin
UI:             Jetpack Compose
Minimum SDK:    API 26
```

## 6. Architecture Reference

The technical architecture is maintained separately in:

`ARCHITECTURE.md`

High-level Phase 1 architecture:

```text
Compose UI
    |
    v
ViewModel
    |
    v
Repository
    |
    v
Room
    |
    v
SQLite
```

Important rule:

```text
Appointment -> physioId
```

not a hard-coded doctor/physio name.

## 7. Milestone Roadmap

```text
Phase 1
|
+-- Milestone 1  Android Foundation      IN PROGRESS
+-- Milestone 2  Navigation              COMPLETED
+-- Milestone 3  Room Database            COMPLETED
+-- Milestone 4  Patient Management       COMPLETED
+-- Milestone 5  Physio Management        COMPLETED — BASIC FLOW
+-- Milestone 6  Appointment Management   IN PROGRESS — CORE FLOW WORKING
+-- Milestone 7  Double Booking            COMPLETED AS PART OF MILESTONE 6
+-- Milestone 8  Patient History           COMPLETED
+-- Milestone 9  Dashboard                 COMPLETED
+-- Milestone 10 Clinic Settings           COMPLETED
+-- Milestone 11 Validation/Polish        IN PROGRESS
```

Milestone 1 remains **IN PROGRESS** because Android foundation learning and cleanup continue alongside feature implementation.

## 8. Milestone 1 — Android Foundation

**Status: IN PROGRESS**

Completed:

- Android Studio and project setup
- Kotlin + Compose project
- Physical device testing
- Wireless debugging
- MainActivity foundation
- Sugam application shell
- Compose fundamentals
- Dashboard UI
- Appointment domain model
- DashboardViewModel
- AppointmentRepository
- Repeated successful builds and phone verification

Current result: the Android foundation and application shell are working.

## 9. Milestone 2 — Navigation

**Status: COMPLETED**

Implemented:

- Navigation Compose
- `NavController`
- `NavHost`
- Routes
- Single-Activity Compose navigation
- Material 3 `Scaffold`
- Bottom navigation
- Scaffold content padding
- Separate `SugamApp.kt`
- Separate screen composables

Primary screens:

```text
Dashboard
Appointments
Patients
Settings
```

Additional Physio destination:

```text
Physios
```

The Physio destination is currently reached through a temporary Dashboard **Manage Physios** action.

## 10. Dashboard Implementation

**Status: EARLY IMPLEMENTATION**

The Dashboard currently displays:

- Appointment count
- Remaining count
- Today's appointments
- Appointment time
- Patient name
- Physio name

The Dashboard is separated from its data source through ViewModel and Repository.

The final Dashboard milestone remains later because it requires real appointment status/count calculations and a quick-create action.

## 11. Milestone 3 — Room Database

**Status: COMPLETED**

Room/KSP setup is successful.

### Current database version

```text
3
```

### Entities

```text
AppointmentEntity
PatientEntity
PhysioEntity
```

### DAOs

```text
AppointmentDao
PatientDao
PhysioDao
```

### Repositories

```text
AppointmentRepository
PatientRepository
PhysioRepository
```

Architecture:

```text
Entity
  |
DAO
  |
RoomDatabase
  |
SQLite
```

The Repository remains the boundary between UI/ViewModel code and Room.

## 12. Milestone 4 — Patient Management

**Status: COMPLETED — BASIC FLOW**

Implemented:

```text
PatientEntity
      |
      v
PatientDao
      |
      v
PatientRepository
      |
      v
PatientViewModel
      |
      v
PatientScreen
```

Completed:

- Patient entity
- Patient DAO
- Patient repository
- Patient ViewModel
- Patient ViewModel factory
- Patient screen
- Patient navigation
- Add patient
- Local persistence
- Successful phone verification

Further Patient capabilities such as richer search, edit, details, and appointment history remain future work.

## 13. Milestone 5 — Physio Management

**Status: COMPLETED — BASIC WORKFLOW**

### Implemented

```text
PhysioEntity
      |
      v
PhysioDao
      |
      v
PhysioRepository
      |
      v
PhysioViewModel
      |
      v
PhysioViewModelFactory
      |
      v
PhysioScreen
```

### Physio fields

```text
id
name
phone
specialization
active
```

`active` defaults to `true`.

### Completed

- Physio entity
- Physio DAO
- Physio repository
- Physio ViewModel
- Physio ViewModel factory
- Physio screen
- Physio navigation destination
- Repository injection through `SugamApplication`
- Add Physio
- List Physios
- Room persistence
- Reactive `Flow<List<PhysioEntity>>`
- Successful build
- Successful phone test
- Multiple physios can be added
- Edit Physio
- Cancel Edit
- Delete Physio with confirmation
- Activate/deactivate Physio
- Successful persistence verification after reopening the screen

### Deferred / future work

- Finalize Physio navigation placement
- Verify historical-appointment behavior when appointments exist

Physio Management is complete for the current Phase 1 basic workflow.

## 14. Milestone 6 — Appointment Management

**Status: IN PROGRESS — CORE FLOW WORKING**

### Appointment creation

Implemented:

```text
Select patient
       |
       v
Select physio
       |
       v
Select date
       |
       v
Select start time
       |
       v
Calculate end time
       |
       v
Check availability
       |
       v
Save appointment
```

Appointment fields:

- Patient ID
- Physio ID
- Date
- Start time
- End time/duration
- Status
- Notes

Statuses:

```text
SCHEDULED
CONFIRMED
COMPLETED
CANCELLED
NO_SHOW
```

### Completed features

- Appointment persistence in Room
- Appointment list
- Date-filtered appointment viewing
- Patient name resolution from `patientId`
- Physio name resolution from `physioId`
- Save success feedback
- Conflict feedback
- Double-booking prevention
- Cancellation with confirmation
- Cancellation as a status change rather than deletion
- Cancelled appointments do not block future bookings
- Confirm appointment
- Complete appointment
- Mark no-show
- Edit scheduled appointments
- Edit confirmed appointments
- Existing appointment is updated rather than duplicated
- Existing status is preserved during edit
- Edit conflict validation
- Current appointment is excluded from its own conflict query
- Successful physical-device verification

### Current status action model

```text
SCHEDULED
 ├── Edit
 ├── Confirm
 └── Cancel

CONFIRMED
 ├── Edit
 ├── Complete
 ├── No Show
 └── Cancel

COMPLETED
 └── no actions

NO_SHOW
 └── no actions

CANCELLED
 └── no actions
```

### Remaining work

- Add/verify a clean **Cancel Edit** action that discards changes without saving.
- Perform final end-to-end Appointment Management verification.
- Verify behavior when a physio is later deactivated while historical appointments still reference that physio.
- Then proceed to Patient Appointment History.

## 15. Milestone 7 — Double-Booking Prevention

**Status: COMPLETED AS PART OF MILESTONE 6**

A physio cannot have overlapping active appointments.

The overlap rule is:

```text
newStart < existingEnd
AND
newEnd > existingStart
AND
same physio
AND
same date
AND
existing status != CANCELLED
```

This validation is implemented in the data access path and is not dependent solely on Compose UI validation.

For appointment updates, the appointment currently being edited is excluded:

```text
existing appointment id != appointment being edited
```

Cancelled appointments are excluded from conflict checks.

## 16. Milestone 8 — Patient Appointment History

**Status: COMPLETED**

Implemented patient appointment history using the existing appointment data.

History includes appointment date/time, physio, status, and available appointment information. Historical appointments remain available even when the associated physio is inactive.

## 17. Milestone 9 — Dashboard

**Status: COMPLETED**

The Dashboard now uses the appointment data flow to provide the current Phase 1 appointment summary and appointment information.

The Dashboard remains separated from Room through the ViewModel and Repository layers.

## 18. Milestone 10 — Clinic Settings

**Status: COMPLETED**

Implemented and verified:

- Clinic name
- Working start time
- Working end time
- Appointment duration
- Room persistence
- Room migration from version 3 to version 4
- Android time picker for working times
- Appointment duration selection
- Settings persistence across app restart
- Appointment end-time calculation uses the configured clinic duration rather than a fixed 30-minute rule

Current settings example:

```text
Clinic:
Sugam Physiotherapy Clinic

Working hours:
09:00 - 19:00

Appointment duration:
30 minutes
```

The next milestone will enforce the configured working hours and complete remaining validation/polish.

## 19. Milestone 11 — Validation and Polish

**Status: IN PROGRESS — STEP 1**

M11 is now the active milestone.

### Completed/verified so far

- Clinic appointment duration is connected to appointment end-time calculation.
- Appointment summary uses the configured clinic duration.
- Create/edit appointment paths use the configured duration.

### Remaining validation and polish

- Required-field validation and user-facing error messages
- Invalid date/time handling
- Working-hour violations using Clinic Settings
- Inactive physio selection rules
- Appointment duration validation
- Empty states
- Loading states
- Error states
- Status handling verification
- Migration verification/regression testing
- UI polish
- Appropriate tests

### Current M11 checkpoint

```text
M11 Step 1 — Clinic appointment duration integration   DONE
M11 Step 2 — Required-field/error validation            NEXT
M11 Step 3 — Working-hour validation                    PLANNED
M11 Step 4 — Inactive physio validation                 PLANNED
M11 Step 5 — Empty/loading/error UI polish              PLANNED
M11 Step 6 — Final regression and tests                 PLANNED
```

## 20. Phase 2 — Future Plan

**Status: PLANNED / NOT IMPLEMENTING YET**

Future architecture:

```text
Android
   |
   v
REST API
   |
   v
Backend
   |
   v
PostgreSQL / Amazon RDS
```

Potential capabilities:

- Multiple users
- Authentication
- Roles/permissions
- Multiple physios
- Cloud synchronization
- Centralized data
- Backend-side scheduling validation
- Backup/recovery
- Audit logging

Phase 2 should not be implemented prematurely during Phase 1.

## 21. Development Working Agreement

For every implementation step:

1. Explain what we are building.
2. Explain why.
3. Show only relevant code.
4. Explain important lines.
5. Run the application.
6. Verify behavior.
7. Record the checkpoint.
8. Continue.

Prefer small, understandable changes.

## 22. Current Checkpoint

**Project:** Sugam

**Current phase:** Phase 1 — Local Android application

**Current milestone:** Milestone 11 — Validation and Polish

**Working application status:** SUCCESSFUL

### Completed milestones

- Milestone 1 — Android Foundation: implementation working; ongoing learning/cleanup remains.
- Milestone 2 — Navigation: COMPLETED
- Milestone 3 — Room Database: COMPLETED
- Milestone 4 — Patient Management: COMPLETED for current basic Phase 1 flow
- Milestone 5 — Physio Management: COMPLETED for current basic Phase 1 workflow
- Milestone 6 — Appointment Management: COMPLETED
- Milestone 7 — Double Booking: COMPLETED as part of Appointment Management
- Milestone 8 — Patient Appointment History: COMPLETED
- Milestone 9 — Dashboard: COMPLETED
- Milestone 10 — Clinic Settings: COMPLETED

### Current implementation highlights

- Room database version 4
- Clinic Settings persisted locally
- Appointment duration driven by Clinic Settings
- Android time picker for working times
- Patient appointment history
- Dashboard appointment summary
- Appointment create/edit/status/cancel flows
- Double-booking prevention including edit conflict exclusion
- Existing appointment status preserved during edit
- Successful physical-device testing

### Current M11 step

The next implementation task is to complete required-field/error validation in the appointment flow, followed by working-hour enforcement and the remaining validation/polish work.

## 23. Next Chat Prompt

When a new chat is needed, upload:

```text
Sugam_ARCHITECTURE_updated_m10.md
Sugam_PROJECT_PLAN_updated_m10.md
sugam_6.zip (or the latest source ZIP)
```

and use:

> Continue the Sugam project from the current checkpoint.
>
> Use `Sugam_ARCHITECTURE_updated_m10.md` as the technical source of truth and `Sugam_PROJECT_PLAN_updated_m10.md` as the development roadmap. Inspect the latest source ZIP before modifying code.
>
> Current status:
> - Phase 1 Android app is running successfully on the physical OnePlus Nord 5.
> - Room database version 4 is working.
> - Patient Management basic flow is complete.
> - Physio Management basic workflow is complete, including Add, List, Edit, Delete, and Activate/Deactivate.
> - Appointment Management is complete, including create/view/status/cancel/edit and double-booking prevention.
> - Patient Appointment History is complete.
> - Dashboard is complete for the current Phase 1 scope.
> - Clinic Settings are complete and persisted using Room v4.
> - Appointment duration is driven by Clinic Settings.
>
> Current milestone: Milestone 11 — Validation and Polish.
>
> Current step: implement required-field/error validation, then proceed to working-hour enforcement using Clinic Settings.
>
> Before modifying code, inspect the actual latest source and explain the smallest relevant change.

## 24. Document Maintenance Rules

### ARCHITECTURE.md

Update when:

- Architecture changes
- Domain model changes
- Database relationships change
- Major technology decisions are made
- Business rules change
- Important architectural decisions are made

It is the technical source of truth.

### PROJECT_PLAN.md

Update when:

- A milestone starts
- A milestone completes
- A task is completed
- A new implementation task is identified
- Current status changes
- The next step changes
- A development checkpoint is reached

It is the progress/roadmap source of truth.
