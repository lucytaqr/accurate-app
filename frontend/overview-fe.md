# Frontend Overview - Accurate User Directory Android App

Dokumen ini menggabungkan overview frontend, UI breakdown, architecture plan, dan list task detail untuk membuat MVP Android App Accurate User Directory.

---

## 1. Tujuan Frontend

Frontend bertugas menyediakan pengalaman pengguna yang cepat, jelas, modern, dan tetap aman saat offline. Aplikasi harus terasa seperti directory bisnis internal yang ringan, bukan sekadar list API mentah. Fokus utama adalah user list, search, sorting, filtering, add user, offline state, dan sync state.

---

## 2. Analisis Desain yang Diberikan

Desain yang dilampirkan menunjukkan arah UI yang cukup matang. Pola desainnya mengarah ke aplikasi Android modern dengan gaya clean, profesional, dan dekat dengan branding Accurate.

### 2.1 Branding dan Visual Style

- Warna utama menggunakan pink/merah Accurate sebagai primary action.
- Background dominan putih dan abu terang untuk menjaga kesan bersih.
- Kartu user memakai rounded rectangle, shadow ringan, dan spacing cukup luas.
- Typography sederhana, readable, dan tidak terlalu dekoratif.
- Action utama seperti `Filter`, `Tambah User`, dan `Simpan User` selalu dibuat jelas.
- Bottom navigation menggunakan tiga tab: Users, Activity, Settings.

### 2.2 Screen yang Harus Dibangun

1. Splash Screen
2. User List/Main Screen
3. Filter Bottom Sheet
4. Active Filter State
5. Add New User Screen
6. Empty State
7. Offline State
8. Loading State/Skeleton List
9. Error State
10. Activity Screen
11. Settings Screen

### 2.3 UI Pattern Penting

- Search bar selalu berada di bagian atas list.
- Filter button selalu terlihat agar user mudah menyaring data.
- Active filter ditampilkan sebagai removable chips.
- Sort dropdown sederhana, minimal `A-Z` dan `Z-A`.
- Bottom sheet dipakai untuk filter agar tidak memindahkan user dari konteks daftar.
- FAB digunakan untuk tambah user.
- Status sync dan last updated ditampilkan sebagai status indicator kecil.
- Offline state tidak boleh memblokir aplikasi selama data cache masih ada.

### 2.4 UX Decision

| Area | Keputusan UX |
|---|---|
| Search | Search langsung memfilter list lokal agar cepat. |
| Sort | Sort dilakukan lokal berdasarkan nama. |
| Filter | Filter kota dan gender dilakukan lokal. |
| Add User | Form full screen agar validasi lebih jelas dan input lebih nyaman. |
| Foto | Gunakan Photo Picker. Preview foto muncul sebelum submit. |
| Offline | Data cache tetap muncul, banner memberi tahu status offline. |
| Error | Error state hanya muncul penuh jika tidak ada data cache sama sekali. |
| Sync | Status sync kecil agar tidak mengganggu flow utama. |

---

## 3. Tech Stack Frontend

| Layer | Teknologi |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | Clean Architecture + MVVM |
| Dependency Injection | Hilt |
| Async/Reactive | Coroutines, Flow, StateFlow |
| Networking | Retrofit, OkHttp |
| JSON | Moshi |
| Local Database | Room |
| Background Job | WorkManager |
| Image Input | Android Photo Picker |
| Analytics | Firebase Analytics, optional jika waktu cukup |
| Testing | JUnit, Turbine, MockWebServer, Compose UI Test |

---

## 4. Architecture Frontend

### 4.1 Modular Package Structure

```text
app/src/main/java/com/accurate/userdirectory/
├── AccurateApp.kt
├── MainActivity.kt
├── core/
│   ├── common/
│   │   ├── ResultState.kt
│   │   ├── UiText.kt
│   │   ├── DispatchersProvider.kt
│   │   └── DateTimeProvider.kt
│   ├── designsystem/
│   │   ├── AccurateColors.kt
│   │   ├── AccurateTypography.kt
│   │   ├── AccurateTheme.kt
│   │   └── components/
│   │       ├── AccurateButton.kt
│   │       ├── AccurateTextField.kt
│   │       ├── AccurateCard.kt
│   │       ├── FilterChip.kt
│   │       ├── UserCard.kt
│   │       └── StateView.kt
│   ├── network/
│   │   ├── NetworkMonitor.kt
│   │   ├── RetrofitModule.kt
│   │   └── ApiErrorHandler.kt
│   └── database/
│       ├── AccurateDatabase.kt
│       └── Converters.kt
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   ├── UserApiService.kt
│   │   │   └── CityApiService.kt
│   │   └── dto/
│   │       ├── UserDto.kt
│   │       ├── CreateUserRequestDto.kt
│   │       └── CityDto.kt
│   ├── local/
│   │   ├── dao/
│   │   │   ├── UserDao.kt
│   │   │   ├── CityDao.kt
│   │   │   └── ActivityLogDao.kt
│   │   └── entity/
│   │       ├── UserEntity.kt
│   │       ├── CityEntity.kt
│   │       └── ActivityLogEntity.kt
│   ├── mapper/
│   │   ├── UserMapper.kt
│   │   └── CityMapper.kt
│   └── repository/
│       ├── UserRepositoryImpl.kt
│       ├── CityRepositoryImpl.kt
│       └── ActivityLogRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── City.kt
│   │   ├── Gender.kt
│   │   ├── SortOption.kt
│   │   ├── UserFilter.kt
│   │   └── SyncStatus.kt
│   ├── repository/
│   │   ├── UserRepository.kt
│   │   ├── CityRepository.kt
│   │   └── ActivityLogRepository.kt
│   └── usecase/
│       ├── ObserveUsersUseCase.kt
│       ├── RefreshUsersUseCase.kt
│       ├── AddUserUseCase.kt
│       ├── ObserveCitiesUseCase.kt
│       ├── SyncPendingUsersUseCase.kt
│       └── ObserveActivityLogsUseCase.kt
├── presentation/
│   ├── navigation/
│   │   ├── AppNavGraph.kt
│   │   ├── AppRoute.kt
│   │   └── BottomNavItem.kt
│   ├── splash/
│   │   ├── SplashScreen.kt
│   │   └── SplashViewModel.kt
│   ├── users/
│   │   ├── UserListScreen.kt
│   │   ├── UserListViewModel.kt
│   │   ├── UserListUiState.kt
│   │   ├── components/
│   │   │   ├── UserSearchBar.kt
│   │   │   ├── UserSortDropdown.kt
│   │   │   ├── ActiveFilterChips.kt
│   │   │   ├── UserFilterBottomSheet.kt
│   │   │   ├── UserListContent.kt
│   │   │   └── SyncStatusBanner.kt
│   ├── adduser/
│   │   ├── AddUserScreen.kt
│   │   ├── AddUserViewModel.kt
│   │   ├── AddUserUiState.kt
│   │   └── AddUserFormValidator.kt
│   ├── activity/
│   │   ├── ActivityScreen.kt
│   │   ├── ActivityViewModel.kt
│   │   └── ActivityUiState.kt
│   └── settings/
│       ├── SettingsScreen.kt
│       ├── SettingsViewModel.kt
│       └── SettingsUiState.kt
├── sync/
│   ├── UserSyncWorker.kt
│   └── SyncScheduler.kt
└── di/
    ├── AppModule.kt
    ├── DatabaseModule.kt
    ├── NetworkModule.kt
    ├── RepositoryModule.kt
    └── UseCaseModule.kt
```

### 4.2 Clean Architecture Rule

- Presentation hanya tahu domain use case.
- Domain tidak tahu data, Retrofit, Room, Compose, atau Android framework.
- Data mengimplementasikan repository interface dari domain.
- Mapper memisahkan DTO, entity, dan domain model.
- Room menjadi single source of truth untuk list user.

### 4.3 Data Flow

```text
UI Event
  -> ViewModel
  -> UseCase
  -> Repository Interface
  -> Repository Implementation
  -> Remote API and Local DB
  -> Room Flow
  -> UseCase
  -> ViewModel StateFlow
  -> Compose UI
```

### 4.4 Offline First Flow

```text
App opened
  -> UI observes Room data
  -> Repository triggers remote refresh if online
  -> API response saved to Room
  -> UI updates automatically from Room

App offline
  -> UI still observes Room data
  -> offline banner appears
  -> add user saves local pending user
  -> WorkManager syncs pending user when network returns
```

---

## 5. Domain Model

### 5.1 User

```kotlin
data class User(
    val id: String,
    val localId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val gender: Gender,
    val photoUri: String?,
    val syncStatus: SyncStatus,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 5.2 Gender

```kotlin
enum class Gender(val apiValue: Int) {
    Male(0),
    Female(1)
}
```

### 5.3 SyncStatus

```kotlin
enum class SyncStatus {
    Synced,
    PendingCreate,
    FailedCreate
}
```

### 5.4 UserFilter

```kotlin
data class UserFilter(
    val keyword: String = "",
    val selectedCities: Set<String> = emptySet(),
    val selectedGender: Gender? = null,
    val sortOption: SortOption = SortOption.NameAsc
)
```

### 5.5 SortOption

```kotlin
enum class SortOption {
    NameAsc,
    NameDesc
}
```

---

## 6. UI Screen Detail

### 6.1 Splash Screen

Tujuan:

- Memberi first impression sesuai branding Accurate.
- Menampilkan logo, tagline singkat, dan loading indicator.
- Durasi maksimal 1 sampai 1,5 detik.
- Tidak boleh membuat user menunggu terlalu lama.

Komponen:

- Accurate logo.
- Text `accurate`.
- Caption: `Memudahkan pengelolaan bisnis Anda`.
- Progress indicator kecil warna primary.

Behavior:

- Saat splash tampil, aplikasi dapat mulai melakukan initial sync.
- Setelah minimum delay selesai, navigasi ke Main Screen.
- Jika initial sync gagal tetapi cache ada, tetap masuk ke User List.

Acceptance criteria:

- Splash tidak blank.
- Splash tidak stuck.
- Splash tetap lanjut walau network error.

### 6.2 User List Screen

Tujuan:

- Menjadi main screen aplikasi.
- Menampilkan daftar user dengan search, filter, sort, status sync, dan FAB tambah user.

Komponen:

- Top app bar:
  - hamburger icon.
  - title `User Directory`.
  - search icon optional.
  - notification icon optional.
- Search field:
  - placeholder: `Cari nama, email, atau kota...`.
- Filter button:
  - icon filter.
  - label `Filter`.
- Quick city chips:
  - `Semua Kota`, `Jakarta`, `Bandung`, `Surabaya`, dst.
- Sort dropdown:
  - `Urutkan: A-Z`.
  - `Urutkan: Z-A`.
- Sync summary card:
  - jumlah user tampil.
  - last updated.
  - sync status.
- User cards:
  - avatar/foto.
  - name.
  - email.
  - city.
  - gender chip.
  - pending sync indicator bila belum tersinkron.
- Bottom navigation.
- FAB tambah user.

Behavior:

- List membaca data dari Room.
- Refresh remote dilakukan saat screen pertama kali dibuka dan saat pull/manual refresh.
- Search/filter/sort dilakukan secara lokal terhadap data Room.
- Jika search kosong, tampilkan semua data sesuai filter.
- Jika filter aktif, tampilkan chips yang bisa dihapus satu per satu.
- Jika tidak ada hasil search/filter, tampilkan empty filtered state, bukan error.

Acceptance criteria:

- Data muncul dari API saat online.
- Data tetap muncul saat offline setelah pernah berhasil load.
- Search cepat dan tidak menyebabkan API call berulang.
- Sort tidak merusak filter aktif.
- Filter kota dan gender dapat digabung.
- FAB membuka Add User Screen.

### 6.3 Filter Bottom Sheet

Tujuan:

- Memberi cara filter yang rapi tanpa meninggalkan list screen.

Komponen:

- Header `Filter`.
- Search city input.
- Section `Kota`.
- Checkbox `Semua Kota`.
- Checkbox daftar kota.
- Section `Jenis Kelamin`.
- Radio/checkbox `Semua`, `Male`, `Female`.
- Button `Reset`.
- Button `Terapkan Filter`.

Behavior:

- Bottom sheet membaca filter sementara.
- Perubahan belum diterapkan sampai user menekan `Terapkan Filter`.
- `Reset` menghapus seluruh filter kota/gender.
- Search kota hanya memfilter pilihan kota di dalam bottom sheet.
- Jika `Semua Kota` aktif, selectedCities menjadi emptySet.

Acceptance criteria:

- Filter bottom sheet dapat dibuka dari button Filter.
- Filter dapat diterapkan dan list berubah.
- Filter aktif muncul sebagai chip.
- Reset menghapus filter dan list kembali normal.

### 6.4 Add New User Screen

Tujuan:

- Menambahkan user baru dengan input yang jelas dan validasi yang ramah.

Field:

- Foto opsional.
- Nama lengkap wajib.
- Email wajib.
- No. Handphone wajib.
- Alamat opsional atau wajib ringan, mengikuti desain.
- Kota wajib.
- Jenis kelamin wajib.

Komponen:

- Top app bar dengan back button.
- Photo picker box.
- Text field nama.
- Text field email.
- Text field phone.
- Text field alamat.
- City dropdown.
- Gender radio button.
- Button `Simpan User`.

Validation:

- Nama tidak boleh kosong.
- Email harus format email valid.
- Phone hanya angka, `+`, atau spasi bila ingin fleksibel.
- Kota harus dipilih.
- Gender harus dipilih.
- Foto opsional.

Behavior online:

- Tekan save.
- Validasi form.
- Kirim POST ke MockAPI.
- Jika sukses, simpan hasil response ke Room dengan `Synced`.
- Tampilkan snackbar sukses.
- Kembali ke User List.

Behavior offline:

- Tekan save.
- Validasi form.
- Simpan user ke Room dengan id lokal dan `PendingCreate`.
- Jadwalkan WorkManager.
- Tampilkan snackbar `User disimpan offline dan akan disinkronkan nanti`.
- Kembali ke User List.

Photo behavior:

- Gunakan Android Photo Picker.
- Simpan URI lokal di Room.
- Tampilkan preview pada card dan form.
- Jika MockAPI bisa menerima field tambahan, kirim `photoUri` atau `photoUrl` string.
- Jika tidak bisa, foto tetap lokal untuk MVP.

Acceptance criteria:

- Form tidak bisa submit jika field wajib invalid.
- Error field muncul di bawah input.
- User baru muncul di list setelah save, baik online maupun offline.
- Pending user punya indicator kecil.
- Foto tampil lokal setelah dipilih.

### 6.5 Empty State

Jenis empty state:

1. Belum ada user sama sekali.
2. Tidak ada hasil untuk search/filter aktif.

Behavior:

- Jika database kosong, tampilkan ikon empty dan button `Tambah User`.
- Jika hasil search/filter kosong, tampilkan pesan `Tidak ada user yang sesuai` dan button `Reset Filter`.

Acceptance criteria:

- Empty state tidak membingungkan.
- User punya next action yang jelas.

### 6.6 Offline State

Tujuan:

- Memberi tahu user bahwa aplikasi sedang offline tanpa membuat aplikasi terasa rusak.

Behavior:

- Jika offline dan cache ada, tampilkan banner kecil.
- Jika offline dan cache kosong, tampilkan state offline penuh.
- Add user tetap boleh dilakukan dan masuk pending queue.
- Manual refresh disabled atau menampilkan snackbar offline.

Acceptance criteria:

- Aplikasi tidak crash saat internet mati.
- Data cache tetap terbaca.
- User pending tetap terlihat di list.

### 6.7 Loading State

Jenis loading:

- Initial loading: skeleton list.
- Refresh loading: small progress indicator di sync card.
- Submit loading: button disabled + progress.

Acceptance criteria:

- Loading tidak membuat layout lompat terlalu banyak.
- User tahu sedang terjadi proses.

### 6.8 Error State

Jenis error:

- Network error.
- Server error.
- Parse error.
- Validation error.

Behavior:

- Jika cache ada, tampilkan snackbar/banner kecil.
- Jika cache kosong, tampilkan full error state dengan button `Coba Lagi`.

Acceptance criteria:

- Error message tidak teknis berlebihan.
- Retry bekerja.

### 6.9 Activity Screen

Karena user meminta Activity berfungsi bila tidak terlalu ribet, MVP dibuat ringan.

Tujuan:

- Menampilkan log aktivitas lokal agar tab tidak kosong.

Isi:

- User added locally.
- User synced successfully.
- Sync failed.
- Data refreshed.
- App went offline/online jika mudah.

Data source:

- `ActivityLogEntity` di Room.

Komponen:

- Top app bar.
- List activity log.
- Empty state `Belum ada aktivitas`.
- Timestamp relatif atau format sederhana.

Acceptance criteria:

- Activity screen bisa dibuka.
- Minimal log muncul setelah add user atau refresh.
- Tidak perlu fitur detail activity untuk MVP.

### 6.10 Settings Screen

Karena user meminta Settings berfungsi bila tidak terlalu ribet, MVP dibuat sederhana.

Isi:

- App name and version.
- API source: MockAPI.
- Last sync time.
- Pending sync count.
- Manual sync/refresh button.
- Clear local cache button optional, bisa disembunyikan jika rawan.
- Theme placeholder optional.

Acceptance criteria:

- Settings screen bisa dibuka.
- Manual sync memicu refresh atau sync pending users.
- Pending sync count tampil.

---

## 7. Room Database Design

### 7.1 UserEntity

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val localId: String,
    val remoteId: String?,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val gender: Int,
    val photoUri: String?,
    val syncStatus: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

Notes:

- `localId` selalu ada.
- `remoteId` bisa null untuk user pending offline.
- Saat POST sukses, update `remoteId` dan `syncStatus`.

### 7.2 CityEntity

```kotlin
@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val name: String,
    val updatedAt: Long
)
```

### 7.3 ActivityLogEntity

```kotlin
@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val description: String,
    val createdAt: Long
)
```

---

## 8. Repository Strategy

### 8.1 Observe Users

- UI observe `UserDao.observeUsers()`.
- ViewModel menerima Flow list user dari use case.
- Search/filter/sort bisa dilakukan di ViewModel agar state mudah dikontrol.

### 8.2 Refresh Users

```text
refreshUsers()
  -> check network
  -> GET /user
  -> map DTO to Entity
  -> upsert only Synced users
  -> preserve PendingCreate users
  -> write activity log
```

Important:

- Jangan hapus pending user saat refresh.
- Jika server data berubah, update synced users.

### 8.3 Add User

Online:

```text
addUser(input)
  -> validate
  -> if online POST /user
  -> save response as Synced
  -> log success
```

Offline:

```text
addUser(input)
  -> validate
  -> save local PendingCreate
  -> enqueue WorkManager
  -> log pending
```

### 8.4 Sync Pending Users

```text
syncPendingUsers()
  -> get all PendingCreate users
  -> for each user:
       POST /user
       if success update remoteId and Synced
       if failed keep PendingCreate or mark FailedCreate depending error
  -> log result
```

---

## 9. ViewModel State Design

### 9.1 UserListUiState

```kotlin
data class UserListUiState(
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val users: List<User> = emptyList(),
    val displayedUsers: List<User> = emptyList(),
    val cities: List<City> = emptyList(),
    val filter: UserFilter = UserFilter(),
    val lastUpdatedText: String = "-",
    val pendingSyncCount: Int = 0,
    val errorMessage: UiText? = null,
    val emptyReason: EmptyReason? = null
)
```

### 9.2 AddUserUiState

```kotlin
data class AddUserUiState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val city: String = "",
    val gender: Gender? = null,
    val photoUri: String? = null,
    val cities: List<City> = emptyList(),
    val fieldErrors: Map<String, UiText> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val message: UiText? = null
)
```

### 9.3 ActivityUiState

```kotlin
data class ActivityUiState(
    val logs: List<ActivityLog> = emptyList(),
    val isLoading: Boolean = false
)
```

### 9.4 SettingsUiState

```kotlin
data class SettingsUiState(
    val appVersion: String = "1.0.0",
    val apiSource: String = "MockAPI",
    val lastSyncText: String = "-",
    val pendingSyncCount: Int = 0,
    val isSyncing: Boolean = false,
    val message: UiText? = null
)
```

---

## 10. Design System Token

### 10.1 Colors

Approximation from design:

```text
PrimaryPink      #E91E63 or adjusted from Accurate brand
PrimaryPinkLight #FF4F8B
Background       #FFFFFF
Surface          #FFFFFF
SurfaceSoft      #F8F8F8
TextPrimary      #1F2937
TextSecondary    #6B7280
Border           #E5E7EB
Success          #22C55E
Warning          #F59E0B
Error            #EF4444
Info             #3B82F6
```

### 10.2 Component Rules

- Button height: 48dp.
- Card corner radius: 12dp to 16dp.
- Input height: 48dp to 52dp.
- Main horizontal padding: 16dp.
- Card spacing: 8dp to 12dp.
- Bottom nav height: 64dp.
- FAB size: 56dp.

---

## 11. Detailed Task List Frontend

### Phase 0 - Repository and Project Setup

- [ ] Create new Android project with Kotlin.
- [ ] Set minimum SDK, target SDK, and app namespace.
- [ ] Add Gradle dependencies:
  - [ ] Compose BOM.
  - [ ] Navigation Compose.
  - [ ] Hilt.
  - [ ] Retrofit.
  - [ ] OkHttp logging interceptor.
  - [ ] Moshi.
  - [ ] Room.
  - [ ] WorkManager.
  - [ ] Coil for image rendering.
  - [ ] Firebase Analytics optional.
  - [ ] Test dependencies.
- [ ] Setup `AccurateApp.kt` with `@HiltAndroidApp`.
- [ ] Setup `MainActivity.kt` with Compose theme.
- [ ] Create base package structure.
- [ ] Add `.gitignore` for Android.
- [ ] Add initial README placeholder.
- [ ] Commit: `feat: initialize android project structure`.

### Phase 1 - Design System

- [ ] Create `AccurateTheme`.
- [ ] Define color tokens.
- [ ] Define typography tokens.
- [ ] Define spacing constants if needed.
- [ ] Create reusable button component.
- [ ] Create reusable text field component.
- [ ] Create reusable card component.
- [ ] Create user avatar component.
- [ ] Create status chip component.
- [ ] Create empty/loading/error state component.
- [ ] Match UI with design screenshot:
  - [ ] white background.
  - [ ] primary pink CTA.
  - [ ] rounded cards.
  - [ ] light border inputs.
  - [ ] subtle shadows.
- [ ] Commit: `feat: add accurate design system components`.

### Phase 2 - Navigation Shell

- [ ] Define app routes:
  - [ ] Splash.
  - [ ] MainTabs.
  - [ ] AddUser.
- [ ] Define bottom nav tabs:
  - [ ] Users.
  - [ ] Activity.
  - [ ] Settings.
- [ ] Implement `AppNavGraph`.
- [ ] Implement main scaffold with bottom navigation.
- [ ] Implement FAB only on Users tab.
- [ ] Implement placeholder Activity and Settings first.
- [ ] Commit: `feat: add navigation shell and bottom tabs`.

### Phase 3 - Domain Layer

- [ ] Create `User` domain model.
- [ ] Create `City` domain model.
- [ ] Create `Gender` enum.
- [ ] Create `SyncStatus` enum.
- [ ] Create `SortOption` enum.
- [ ] Create `UserFilter` model.
- [ ] Create repository interfaces:
  - [ ] `UserRepository`.
  - [ ] `CityRepository`.
  - [ ] `ActivityLogRepository`.
- [ ] Create use cases:
  - [ ] `ObserveUsersUseCase`.
  - [ ] `RefreshUsersUseCase`.
  - [ ] `AddUserUseCase`.
  - [ ] `ObserveCitiesUseCase`.
  - [ ] `SyncPendingUsersUseCase`.
  - [ ] `ObserveActivityLogsUseCase`.
- [ ] Keep domain layer pure Kotlin.
- [ ] Commit: `feat: define user directory domain layer`.

### Phase 4 - Network Layer

- [ ] Create Retrofit interface `UserApiService`.
- [ ] Add `GET /user`.
- [ ] Add `POST /user`.
- [ ] Create Retrofit interface `CityApiService`.
- [ ] Add `GET /city`.
- [ ] Create DTO models:
  - [ ] `UserDto`.
  - [ ] `CreateUserRequestDto`.
  - [ ] `CityDto`.
- [ ] Configure Moshi.
- [ ] Configure OkHttp logging interceptor only for debug.
- [ ] Create `ApiErrorHandler`.
- [ ] Create network module with base URL.
- [ ] Test endpoint manually with app or unit test.
- [ ] Commit: `feat: integrate mockapi network layer`.

### Phase 5 - Room Database Layer

- [ ] Create `AccurateDatabase`.
- [ ] Create `UserEntity`.
- [ ] Create `CityEntity`.
- [ ] Create `ActivityLogEntity`.
- [ ] Create `UserDao`:
  - [ ] observe all users.
  - [ ] upsert users.
  - [ ] insert pending user.
  - [ ] get pending users.
  - [ ] update sync status.
  - [ ] delete synced users optional.
- [ ] Create `CityDao`.
- [ ] Create `ActivityLogDao`.
- [ ] Create database module for Hilt.
- [ ] Add migration strategy, for MVP use destructive migration only in debug if acceptable.
- [ ] Commit: `feat: add room database cache`.

### Phase 6 - Mapper Layer

- [ ] Map `UserDto` to `UserEntity`.
- [ ] Map `UserEntity` to `User`.
- [ ] Map add user input to `CreateUserRequestDto`.
- [ ] Map `CityDto` to `CityEntity`.
- [ ] Add defensive handling:
  - [ ] null remote id fallback.
  - [ ] missing city fallback.
  - [ ] unknown gender fallback to Male or safe default.
- [ ] Unit test mappers.
- [ ] Commit: `feat: add dto entity domain mappers`.

### Phase 7 - Repository Implementation

- [ ] Implement `UserRepositoryImpl.observeUsers()` from Room.
- [ ] Implement `refreshUsers()`:
  - [ ] call API.
  - [ ] map response.
  - [ ] save to Room.
  - [ ] preserve pending users.
  - [ ] log refresh.
- [ ] Implement `addUser()` online/offline branching.
- [ ] Implement `syncPendingUsers()`.
- [ ] Implement `CityRepositoryImpl`.
- [ ] Implement `ActivityLogRepositoryImpl`.
- [ ] Inject repositories with Hilt.
- [ ] Add repository unit tests with fake DAO/API if time allows.
- [ ] Commit: `feat: implement offline first repositories`.

### Phase 8 - Network Monitor

- [ ] Create `NetworkMonitor` interface.
- [ ] Implement Android connectivity based monitor.
- [ ] Expose `isOnline: Flow<Boolean>`.
- [ ] Inject into repository and ViewModels.
- [ ] Update UI state when offline.
- [ ] Commit: `feat: add network monitor for offline state`.

### Phase 9 - Splash Screen

- [ ] Create Splash UI matching design.
- [ ] Add logo placeholder or asset.
- [ ] Add progress indicator.
- [ ] Trigger initial refresh/city load if suitable.
- [ ] Navigate to main screen after short delay.
- [ ] Ensure no stuck state on API failure.
- [ ] Commit: `feat: add branded splash screen`.

### Phase 10 - User List Screen

- [ ] Create `UserListViewModel`.
- [ ] Observe users from use case.
- [ ] Observe cities.
- [ ] Observe network status.
- [ ] Maintain search keyword state.
- [ ] Maintain selected filters.
- [ ] Maintain sort option.
- [ ] Compute `displayedUsers`.
- [ ] Create `UserListScreen`.
- [ ] Create top app bar.
- [ ] Create search bar.
- [ ] Create filter button.
- [ ] Create quick city chips.
- [ ] Create sort dropdown.
- [ ] Create sync summary card.
- [ ] Create lazy column user cards.
- [ ] Create pull-to-refresh or manual refresh.
- [ ] Commit: `feat: implement user list screen`.

### Phase 11 - Search, Sort, and Filter

- [ ] Search by name.
- [ ] Search by email.
- [ ] Search by city.
- [ ] Sort name A-Z.
- [ ] Sort name Z-A.
- [ ] Filter by one or many cities.
- [ ] Filter by gender.
- [ ] Combine search + sort + filter.
- [ ] Add active filter chips.
- [ ] Add remove chip behavior.
- [ ] Add reset all filters.
- [ ] Unit test filtering logic.
- [ ] Commit: `feat: add search sort and filters`.

### Phase 12 - Filter Bottom Sheet

- [ ] Create bottom sheet component.
- [ ] Add city search inside bottom sheet.
- [ ] Add city checkbox list.
- [ ] Add gender filter.
- [ ] Add reset button.
- [ ] Add apply button.
- [ ] Make temporary filter state separate from applied filter state.
- [ ] Add close behavior.
- [ ] Match spacing and visual hierarchy from design.
- [ ] Commit: `feat: add filter bottom sheet`.

### Phase 13 - Add User Screen

- [ ] Create `AddUserViewModel`.
- [ ] Create `AddUserUiState`.
- [ ] Create form validator.
- [ ] Create Add User screen layout.
- [ ] Implement photo picker.
- [ ] Show selected photo preview.
- [ ] Implement name input.
- [ ] Implement email input.
- [ ] Implement phone input.
- [ ] Implement address input.
- [ ] Implement city dropdown.
- [ ] Implement gender radio buttons.
- [ ] Implement field validation.
- [ ] Implement submit online.
- [ ] Implement submit offline pending.
- [ ] Show snackbar success/error.
- [ ] Navigate back after success.
- [ ] Commit: `feat: implement add user form`.

### Phase 14 - WorkManager Sync

- [ ] Create `UserSyncWorker`.
- [ ] Inject dependencies using Hilt Worker.
- [ ] Configure constraints:
  - [ ] network connected.
- [ ] Sync pending users.
- [ ] Retry on network/server temporary error.
- [ ] Mark failed only for non-retryable validation error if any.
- [ ] Schedule one-time sync after offline add.
- [ ] Schedule periodic sync optional.
- [ ] Add manual sync trigger from Settings.
- [ ] Commit: `feat: add pending user sync worker`.

### Phase 15 - Activity Screen

- [ ] Create activity log domain model.
- [ ] Observe logs from Room.
- [ ] Display log list.
- [ ] Add empty state.
- [ ] Write logs for:
  - [ ] refresh success.
  - [ ] add online success.
  - [ ] add offline pending.
  - [ ] sync success.
  - [ ] sync failure.
- [ ] Commit: `feat: add activity log screen`.

### Phase 16 - Settings Screen

- [ ] Create `SettingsViewModel`.
- [ ] Observe pending sync count.
- [ ] Observe last sync time.
- [ ] Add manual sync button.
- [ ] Add app version display.
- [ ] Add API source display.
- [ ] Add simple about section.
- [ ] Optional: Add clear local cache button only if safe.
- [ ] Commit: `feat: add settings screen`.

### Phase 17 - UI States and Polish

- [ ] Implement empty state for no users.
- [ ] Implement empty state for no search/filter result.
- [ ] Implement offline banner.
- [ ] Implement full offline state when cache empty.
- [ ] Implement full error state when cache empty.
- [ ] Implement snackbar for minor errors.
- [ ] Implement skeleton loading list.
- [ ] Improve card spacing and alignment.
- [ ] Ensure keyboard behavior works on form.
- [ ] Ensure bottom sheet safe area works.
- [ ] Ensure portrait screen responsiveness.
- [ ] Commit: `fix: polish user directory ui states`.

### Phase 18 - Firebase Analytics Optional

- [ ] Add Firebase project config if available.
- [ ] Track `screen_view_users`.
- [ ] Track `search_user` without sensitive keyword if privacy concern.
- [ ] Track `apply_filter` with filter type only.
- [ ] Track `add_user_success`.
- [ ] Track `offline_add_user`.
- [ ] Track `sync_pending_success`.
- [ ] Keep analytics optional to avoid delaying MVP.
- [ ] Commit: `feat: add basic firebase analytics events`.

### Phase 19 - Testing

- [ ] Unit test `AddUserFormValidator`.
- [ ] Unit test search/filter/sort logic.
- [ ] Unit test `UserMapper`.
- [ ] Unit test `CityMapper`.
- [ ] Unit test repository online refresh success.
- [ ] Unit test repository offline add pending.
- [ ] Unit test sync pending success.
- [ ] Compose UI test:
  - [ ] user list appears.
  - [ ] search filters result.
  - [ ] filter button opens sheet.
  - [ ] add user validation works.
- [ ] Commit: `test: add frontend unit and ui tests`.

### Phase 20 - README and Final Submission

- [ ] Write app usage steps.
- [ ] Explain tech stack.
- [ ] Explain UI/UX decisions.
- [ ] Explain offline behavior.
- [ ] Explain photo limitation with MockAPI.
- [ ] Add screenshots if available.
- [ ] Add build/run instructions.
- [ ] Add known limitations.
- [ ] Final smoke test:
  - [ ] install app.
  - [ ] online list load.
  - [ ] search.
  - [ ] sort.
  - [ ] filter.
  - [ ] add online.
  - [ ] add offline.
  - [ ] sync after online.
- [ ] Commit: `docs: add final project readme`.

---

## 12. MVP Priority Order

Jika waktu mepet, kerjakan dengan urutan ini:

1. Project setup.
2. API GET user.
3. Room cache.
4. User list UI.
5. Search and sort.
6. Filter kota.
7. Add user online.
8. Offline cache display.
9. Offline add pending.
10. WorkManager sync.
11. Empty/loading/error states.
12. Activity and Settings basic.
13. Photo picker local.
14. Testing.
15. Analytics.

---

## 13. Definition of Done Frontend

- [ ] App builds successfully.
- [ ] User list displays API data.
- [ ] User list uses local cache as source.
- [ ] Search works.
- [ ] Sort works.
- [ ] Filter kota works.
- [ ] Filter gender works.
- [ ] Add user works online.
- [ ] Add user works offline as pending.
- [ ] Pending user syncs when online.
- [ ] Photo can be selected and previewed locally.
- [ ] Empty state exists.
- [ ] Loading state exists.
- [ ] Error state exists.
- [ ] Offline state exists.
- [ ] Activity tab is not empty and shows logs.
- [ ] Settings tab has useful basic controls.
- [ ] README explains how to run and why UI is designed that way.
