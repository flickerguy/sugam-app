# Sugam — Architecture

**Document:** `ARCHITECTURE.md`  
**Purpose:** Technical source of truth for the Sugam application  
**Current phase:** Phase 1 — Local Android application  
**Status:** Phase 1 — Local Android Application Development COMPLETED  
**Last updated:** 2026-08-16

## 1. Purpose

Sugam is a physiotherapy clinic scheduling Android application.

Phase 1 is a local-first Android application initially running on the doctor's phone. It manages patients, physios/assistants, appointments, appointment history, and clinic settings.

The architecture must remain suitable for a future multi-user, cloud-backed Phase 2.

### Phase 1

```text
Android App
    |
    v
Jetpack Compose UI
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

### Future Phase 2

```text
Android App
    |
    v
Jetpack Compose
    |
    v
ViewModel
    |
    v
Repository
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

## 2. Architectural Principles

- Phase 1 is local-first: no login, authentication, REST API, cloud backend, cloud synchronization, or AWS dependency.
- Appointments must reference `physioId`, not a mutable physio name.
- UI must not access Room directly.
- Repository is the persistence boundary.
- Important business rules, such as double-booking prevention, must not depend solely on UI validation.
- The data model must support multiple physios and future synchronization.

## 3. Technology Stack

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
| Dependency Injection | Manual/simple construction; Hilt later if justified |

Development device: **OnePlus Nord 5, API 33, Android Studio Wireless Debugging**.

## 4. Android Application Architecture

Use one `MainActivity` initially.

```text
MainActivity
    |
    v
SugamApp
    |
    v
Scaffold
    |
    +--> NavigationBar
    |
    +--> NavHost
           |
           +--> Dashboard
           +--> Appointments
           +--> Add/Edit Appointment
           +--> Patients
           +--> Patient Details
           +--> Physios
           +--> Settings
```

Physio management currently has a `physios` destination reached from the Dashboard's temporary **Manage Physios** action. It is not yet a primary bottom-navigation destination.

## 5. Current Package Structure

```text
com.example.sugam
|
+-- MainActivity.kt
+-- domain/model/Appointment.kt
|
+-- data
|   +-- local
|   |   +-- AppDatabase.kt
|   |   +-- entity
|   |   |   +-- AppointmentEntity.kt
|   |   |   +-- PatientEntity.kt
|   |   |   +-- PhysioEntity.kt
|   |   +-- dao
|   |       +-- AppointmentDao.kt
|   |       +-- PatientDao.kt
|   |       +-- PhysioDao.kt
|   +-- repository
|       +-- AppointmentRepository.kt
|       +-- PatientRepository.kt
|       +-- PhysioRepository.kt
|
+-- ui
    +-- SugamApp.kt
    +-- screens
        +-- DashboardScreen.kt
        +-- AppointmentsScreen.kt
        +-- PatientsScreen.kt
        +-- SettingsScreen.kt
        +-- patient/
        |   +-- PatientScreen.kt
        |   +-- PatientViewModel.kt
        |   +-- PatientViewModelFactory.kt
        +-- physio/
        |   +-- PhysioScreen.kt
        |   +-- PhysioViewModel.kt
        |   +-- PhysioViewModelFactory.kt
        +-- dashboard/
            +-- DashboardViewModel.kt
            +-- DashboardViewModelFactory.kt
```

## 6. Current Data Flows

### Dashboard

```text
DashboardScreen
       |
       v
DashboardViewModel
       |
       v
AppointmentRepository
       |
       v
AppointmentDao
       |
       v
RoomDatabase
       |
       v
SQLite
```

### Patient

```text
PatientListScreen
       |
       v
PatientDetailsScreen
       |
       v
PatientViewModel
       |
       v
PatientRepository
       |
       v
PatientDao
       |
       v
RoomDatabase
```

### Physio

```text
PhysioScreen
       |
       v
PhysioViewModel
       |
       v
PhysioRepository
       |
       v
PhysioDao
       |
       v
RoomDatabase
       |
       v
SQLite
```

The Physio screen observes `Flow<List<PhysioEntity>>`, allowing database changes to update the UI reactively.

## 7. Domain and Entity Models

### Appointment

Current early domain model:

```kotlin
data class Appointment(
    val id: Long,
    val time: String,
    val patientName: String,
    val physioName: String
)
```

For production persistence, appointment identity must use `physioId` and patient ID rather than mutable names.

### PhysioEntity

Current implementation:

```kotlin
@Entity(tableName = "physios")
data class PhysioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val specialization: String? = null,
    val active: Boolean = true
)
```

Physio support currently includes name, optional phone, optional specialization, active flag, add, list, edit, delete, activate/deactivate, and Room persistence.

## 8. Navigation Architecture

Primary destinations:

```text
Dashboard
Appointments
Patients
Settings
```

Additional destination:

```text
Physios
```

Final placement of Physio management will be decided after the basic Physio workflow is complete.

## 9. Compose State Model

Core principle:

```text
UI = f(state)
```

Conceptual reactive flow:

```text
Room data changes
      |
      v
Repository Flow
      |
      v
ViewModel
      |
      v
Compose state collection
      |
      v
Compose recomposition
```

## 10. Repository Strategy

Phase 1:

```text
Repository -> Room DAO
```

Future Phase 2:

```text
Repository
    |
    +--> Local cache
    |
    +--> REST API
```

The exact synchronization strategy is intentionally deferred.

## 11. Room Architecture

Room is the Phase 1 persistence technology.

Current entities:

```text
AppointmentEntity
PatientEntity
PhysioEntity
ClinicSettingsEntity
```

Current DAOs:

```text
AppointmentDao
PatientDao
PhysioDao
ClinicSettingsDao
```

Conceptual structure:

```text
Entity
  |
DAO
  |
RoomDatabase
  |
SQLite
```

**Current Room database version: 4.**

Room/KSP setup, entities, DAOs, database, and repositories have been successfully built and tested.

## 12. Dependency Injection

Use manual/simple dependency construction initially.

Repositories are provided from `SugamApplication` and supplied to ViewModels through factories/screens as appropriate.

Do not introduce Hilt merely because it is commonly used. Introduce it later if dependency count, lifecycle management, or testing needs justify it.

## 13. Security and Privacy

Phase 1 is local-only and has no authentication or cloud backend.

Patient data should not be unnecessarily written to logs.

Future Phase 2 must explicitly address authentication, authorization, encryption, secure token handling, audit requirements, backup/recovery, retention, and applicable privacy/compliance requirements.

## 14. Architectural Decisions

### ADR-001 — Local-first Phase 1
Use Room/SQLite because Phase 1 requires no backend, authentication, or synchronization.

### ADR-002 — Appointment references physioId
Appointments reference physios by ID because the clinic supports multiple physios and names are mutable.

### ADR-003 — Single Activity
Use one Activity with Compose navigation to keep the initial architecture simple.

### ADR-004 — No Hilt initially
Use manual/simple dependency construction while the application remains small.

### ADR-005 — Reactive Room lists
Use Kotlin `Flow` for database-backed lists that need automatic UI updates.

## 15. Architectural Constraints

1. Appointment must reference `physioId`.
2. UI must not directly access Room.
3. Repository remains the persistence boundary.
4. Phase 1 remains local-only.
5. Multiple physios must be supported.
6. Historical appointments must survive physio deactivation.
7. Double-booking must be enforced outside the UI.
8. Do not hard-code a single doctor's identity into the domain model.
9. Do not introduce cloud dependencies into Phase 1 without a specific requirement.

## 16. Appointment Management Architecture

The current Appointment Management implementation follows:

```text
AppointmentsScreen
       |
       v
AppointmentViewModel
       |
       +--> AppointmentRepository
       |        |
       |        v
       |    AppointmentDao
       |        |
       |        v
       |    RoomDatabase v4
       |
       +--> ClinicSettingsRepository
                |
                v
          ClinicSettingsDao
                |
                v
            Room v4
```

### Appointment data

Appointments use stable identifiers:

```text
id
patientId
physioId
appointmentDate
startTime
endTime
status
notes
```

Patient and physio names are resolved for display from their IDs. Names are not stored as the appointment identity.

### Appointment viewing

The Appointments screen maintains a separate `viewDate` state for filtering the appointment list. This is intentionally separate from the date used when creating or editing an appointment.

### Clinic appointment duration

Appointment duration is now a clinic setting rather than a hard-coded appointment rule. The configured duration is read from `ClinicSettingsRepository` and supplied to the appointment calculation path.

Conceptually:

```text
ClinicSettings
      |
      | appointmentDurationMinutes
      v
AppointmentViewModel
      |
      v
calculateEndTime(startTime, durationMinutes)
      |
      v
AppointmentEntity.endTime
```

The appointment screen also uses the clinic setting when displaying the calculated end time.

### Double-booking rule

An active appointment conflicts when:

```text
same physio
AND same date
AND newStart < existingEnd
AND newEnd > existingStart
AND existing status != CANCELLED
```

When editing an appointment, its own ID is excluded from the conflict query.

### Cancellation

Cancellation updates the appointment status to:

```text
CANCELLED
```

The appointment is retained in Room for history. Cancelled appointments are excluded from future conflict checks.

### Status actions

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

### Editing

`SCHEDULED` and `CONFIRMED` appointments can be edited using the existing appointment form.

The edit flow updates the existing appointment row and preserves its current status. Conflict validation is performed before the update. Cancel Edit discards the current selections without saving changes.

## 16A. Patient Appointment History

Patient appointment history is implemented using a dedicated `PatientDetailsScreen`. Historical appointments remain stored and visible even when a physio record is deleted, using a `LEFT JOIN` and a fallback display name in the data query.

Conceptual flow:

```text
Patient
   |
   v
PatientDetailsScreen
   |
   v
Appointment history query (LEFT JOIN)
   |
   v
AppointmentRepository
   |
   v
AppointmentDao
   |
   v
Room
```

History displays appointment date/time, physio, status, and other available appointment information.

## 16B. Dashboard

The Dashboard is implemented as a reactive view over appointment data. It displays appointment summary information and appointment entries using the existing repository/ViewModel architecture.

The Dashboard does not access Room directly.

## 16C. Clinic Settings Architecture

Clinic settings are persisted locally using Room. The settings flow follows:

```text
SettingsScreen
      |
      v
ClinicSettingsViewModel
      |
      v
ClinicSettingsRepository
      |
      v
ClinicSettingsDao
      |
      v
RoomDatabase v4
      |
      v
SQLite
```

The implemented settings include:

- Clinic name
- Working start time
- Working end time
- Appointment duration

Working start/end times use an Android time picker. Appointment duration is selected from supported duration options. Settings are persisted and survive app restart.

## 16D. Room Migration

Clinic Settings introduced the Room schema change from version 3 to version 4. The application currently runs with Room database version 4 and the migration is part of the local database setup.

## 17. Current Implementation Checkpoint

As of **2026-08-15**:

- Kotlin + Jetpack Compose application is working.
- App runs successfully on the physical OnePlus Nord 5 using wireless debugging.
- API 33 development device confirmed.
- Navigation Compose and bottom navigation are implemented.
- Dashboard and appointment data flow are implemented.
- Room/KSP setup is successful.
- Room database version 4 is active.
- Patient Management basic workflow is complete.
- Physio Management basic workflow is complete, including add/list/edit/delete/activate/deactivate.
- Appointment creation is implemented and tested.
- Appointment list and date filtering are implemented.
- Patient and physio names are resolved from appointment IDs.
- Double-booking prevention is implemented.
- Edit conflict validation excludes the appointment being edited.
- Cancelled appointments remain in Room and do not block future bookings.
- Appointment status actions are implemented.
- Appointment edit/reschedule flow is implemented.
- Existing appointment status is preserved during editing.
- Patient Appointment History is completed.
- Dashboard implementation is completed for the current Phase 1 scope.
- Clinic Settings are implemented and persisted using Room v4.
- Clinic appointment duration is now used by appointment end-time calculation rather than being hard-coded in the calculation path.
- Clinic working hours and appointment duration are enforced during appointment validation.
- Inactive physio selection rules are enforced.
- Required-field validation and user-facing error messages are implemented.
- Empty states and UI polish for lists and summaries are implemented.
- Dedicated Add/Edit Appointment screen with professional Cyan theme and dropdowns.
- Shared ViewModel architecture for consistent state across navigation.
- Refined Patient List with search functionality and circular initials avatars.
- Dedicated Add Patient screen.
- Standard Material 3 icons for bottom navigation.
- Builds and physical-device verification have been successful.

Current high-level data flow:

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
Room DAO
    |
    v
RoomDatabase v4
    |
    v
SQLite
```

Current major feature flows:

```text
Patients
   -> PatientViewModel
   -> PatientRepository
   -> PatientDao

Physios
   -> PhysioViewModel
   -> PhysioRepository
   -> PhysioDao

Appointments
   -> AppointmentViewModel
   -> AppointmentRepository
   -> AppointmentDao

Clinic Settings
   -> ClinicSettingsViewModel
   -> ClinicSettingsRepository
   -> ClinicSettingsDao
```

The next implementation focus is **Milestone 11 — Validation and Polish**, beginning with appointment validation and ensuring clinic working hours and configured appointment duration are consistently enforced.

## 17. Related Project Document

See `PROJECT_PLAN.md` for the development roadmap, milestone status, completed tasks, next steps, and checkpoints.
