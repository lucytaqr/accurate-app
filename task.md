# TASK.md — Accurate User Directory App

Dokumen ini berisi panduan kerja dari nol sampai project Android MVP selesai. Scope mengikuti brief: aplikasi Android untuk melihat daftar user, mencari user, mengurutkan berdasarkan nama, memfilter berdasarkan kota, menambahkan user baru, dan tetap bisa digunakan saat offline. Backend menggunakan MockAPI sesuai brief.

---

## 0. Target Akhir Project

### Output yang harus selesai
- Aplikasi Android berjalan normal.
- User dapat melihat daftar user dari MockAPI.
- User dapat mencari berdasarkan nama, email, atau kota.
- User dapat sort nama A-Z dan Z-A.
- User dapat filter berdasarkan kota.
- User dapat menambahkan user baru.
- User dapat memilih foto dari galeri jika memungkinkan.
- Aplikasi tetap menampilkan data terakhir saat offline.
- Aplikasi punya state UI: loading, empty, error, offline, success.
- Repository GitHub memiliki commit incremental dan pesan commit rapi.
- README menjelaskan cara menjalankan aplikasi, teknologi, arsitektur, dan alasan UI/UX.
- APK debug/release bisa dibuat.

### Scope MVP
- Backend tetap menggunakan MockAPI.
- Foto user disimpan lokal menggunakan URI/path, karena API brief tidak menyediakan endpoint upload foto file.
- Tab Activity dan Settings dibuat sederhana. Jika waktu terbatas, cukup placeholder informatif.
- Offline support minimal wajib: data list tersimpan di Room dan tetap muncul saat tidak ada internet.
- Offline add user bersifat nice-to-have. Jika waktu cukup, simpan pending user lalu sync saat online memakai WorkManager.

---

## 1. Aplikasi dan Tools yang Harus Disiapkan

### Wajib
1. Android Studio versi stabil terbaru
   - Digunakan untuk membuat, menjalankan, dan build project Android.

2. JDK 17
   - Biasanya sudah ikut dari Android Studio.
   - Pastikan Gradle menggunakan JDK 17.

3. Git
   - Untuk version control dan commit history.

4. GitHub Account
   - Untuk upload repository project.

5. Postman atau Bruno
   - Untuk mengecek endpoint MockAPI sebelum coding.

6. Browser
   - Untuk membuka endpoint API dan dokumentasi dependency.

7. Emulator Android atau HP Android fisik
   - Minimal Android 8 ke atas.
   - Lebih baik test di emulator dan HP fisik.

### Opsional tetapi disarankan
1. Firebase Console
   - Untuk Firebase Analytics jika ingin nilai tambahan.

2. Figma
   - Untuk membuka atau menata ulang desain jika diperlukan.

3. GitHub Desktop
   - Jika belum nyaman menggunakan Git CLI.

---

## 2. Tech Stack yang Digunakan

### Bahasa dan platform
- Kotlin
- Android Native

### UI
- Jetpack Compose
- Material 3
- Navigation Compose

### Architecture
- Clean Architecture
- MVVM
- Repository Pattern

### Dependency Injection
- Hilt

### Networking
- Retrofit
- Moshi Converter
- OkHttp Logging Interceptor

### Async dan reactive
- Kotlin Coroutine
- Flow
- StateFlow

### Local Database
- Room Database

### Offline dan background task
- WorkManager

### Image loading
- Coil Compose

### Utility
- Timber untuk logging

### Testing
- JUnit
- MockK atau Mockito
- Turbine untuk Flow testing jika diperlukan

---

## 3. Persiapan Awal Sebelum Coding

### 3.1 Validasi brief
Checklist:
- [ ] Baca ulang requirement utama.
- [ ] Catat fitur wajib: list, search, sort, filter city, add user.
- [ ] Catat nice-to-have: SOLID, Clean Architecture, adaptive layout, Hilt, Compose, Flow, Moshi, Room, WorkManager, Firebase Event.
- [ ] Tentukan MVP tidak melebar.

### 3.2 Cek endpoint API
Endpoint user:
```text
GET  https://661f555f16358961cd940b83.mockapi.io/api/v2/accurate/user
POST https://661f555f16358961cd940b83.mockapi.io/api/v2/accurate/user
```

Endpoint city:
```text
GET https://661f555f16358961cd940b83.mockapi.io/api/v2/accurate/city
```

Checklist Postman/Bruno:
- [ ] Test GET user.
- [ ] Test POST user.
- [ ] Test GET city.
- [ ] Simpan contoh response.
- [ ] Cek apakah field `gender` bernilai 0/1.
- [ ] Cek apakah API menerima field tambahan seperti `photoUrl`. Jika tidak, foto cukup lokal.

### 3.3 Tentukan aturan data
- [ ] Gender: 0 = Male, 1 = Female.
- [ ] Foto: simpan URI lokal.
- [ ] Search: client-side dari data Room.
- [ ] Sort: client-side dari data Room.
- [ ] Filter city: client-side dari data Room atau city API.
- [ ] Data utama yang ditampilkan selalu dari Room.
- [ ] API hanya sumber refresh dan create remote.

---

## 4. Setup Project Android dari Nol

### 4.1 Buat project baru
Langkah:
1. Buka Android Studio.
2. Pilih New Project.
3. Pilih Empty Activity dengan Jetpack Compose.
4. Isi:
   - Name: AccurateUserDirectory
   - Package: com.example.accurateuserdirectory
   - Language: Kotlin
   - Minimum SDK: 26 atau 24
5. Klik Finish.

Checklist:
- [ ] Project berhasil dibuat.
- [ ] App bisa dijalankan di emulator.
- [ ] Commit awal dibuat.

Commit:
```bash
git init
git add .
git commit -m "chore: initialize android project"
```

### 4.2 Buat repository GitHub
Langkah:
1. Buka GitHub.
2. Buat repository baru.
3. Nama repository: accurate-user-directory-android.
4. Push project lokal ke GitHub.

Command:
```bash
git remote add origin https://github.com/USERNAME/accurate-user-directory-android.git
git branch -M main
git push -u origin main
```

Checklist:
- [ ] Repo GitHub aktif.
- [ ] Branch main sudah terisi.

---

## 5. Setup Dependency Gradle

### 5.1 Tambahkan dependency utama
Checklist dependency:
- [ ] Compose Material 3
- [ ] Navigation Compose
- [ ] Hilt Android
- [ ] Hilt Navigation Compose
- [ ] Retrofit
- [ ] Moshi
- [ ] OkHttp Logging Interceptor
- [ ] Room Runtime
- [ ] Room KTX
- [ ] Room Compiler/KSP
- [ ] WorkManager KTX
- [ ] Coil Compose
- [ ] Timber
- [ ] Lifecycle ViewModel Compose

### 5.2 Setup plugin
Checklist plugin:
- [ ] Kotlin Android
- [ ] Compose Compiler
- [ ] KSP
- [ ] Hilt

### 5.3 Sync Gradle
Checklist:
- [ ] Gradle sync berhasil.
- [ ] App tetap bisa running.
- [ ] Tidak ada dependency conflict.

Commit:
```bash
git add .
git commit -m "chore: add project dependencies"
```

---

## 6. Buat Struktur Folder Project

Struktur yang disarankan:
```text
app/src/main/java/com/example/accurateuserdirectory/
├── AccurateApp.kt
├── MainActivity.kt
├── core/
│   ├── common/
│   ├── network/
│   ├── database/
│   ├── designsystem/
│   └── util/
├── data/
│   ├── local/
│   ├── remote/
│   ├── mapper/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── navigation/
│   ├── splash/
│   ├── userlist/
│   ├── adduser/
│   ├── activity/
│   ├── settings/
│   └── components/
├── di/
└── worker/
```

Checklist:
- [ ] Folder dibuat.
- [ ] Nama package konsisten.
- [ ] Tidak ada logic besar di MainActivity.

Commit:
```bash
git add .
git commit -m "chore: setup clean architecture package structure"
```

---

## 7. Setup Application dan Hilt

### 7.1 Buat Application class
File:
```text
AccurateApp.kt
```

Checklist:
- [ ] Tambahkan `@HiltAndroidApp`.
- [ ] Daftarkan di AndroidManifest.
- [ ] Setup Timber debug tree jika digunakan.

### 7.2 Update MainActivity
Checklist:
- [ ] Tambahkan `@AndroidEntryPoint`.
- [ ] Gunakan Compose setContent.
- [ ] Panggil root navigation.

Commit:
```bash
git add .
git commit -m "chore: configure hilt application setup"
```

---

## 8. Buat Design System

### 8.1 Warna
Warna berdasarkan desain:
- Primary Pink: #E91E63
- Primary Pink Light: #FF4F7D
- Background: #F6F7FA
- Surface: #FFFFFF
- Text Primary: #1F2937
- Text Secondary: #6B7280
- Success: #22C55E
- Warning: #F59E0B
- Error: #EF4444
- Info: #3B82F6

Checklist:
- [ ] Buat color scheme.
- [ ] Buat typography.
- [ ] Buat spacing constants.
- [ ] Buat shape constants.
- [ ] Buat reusable button, chip, card, top bar, search field.

### 8.2 Komponen reusable
Komponen:
- [ ] AccurateButton
- [ ] AccurateOutlinedButton
- [ ] AccurateSearchField
- [ ] AccurateFilterButton
- [ ] UserCard
- [ ] FilterChip
- [ ] EmptyStateView
- [ ] ErrorStateView
- [ ] OfflineBanner
- [ ] LoadingSkeleton
- [ ] BottomNavigationBar
- [ ] FloatingAddButton

Commit:
```bash
git add .
git commit -m "feat: add design system components"
```

---

## 9. Setup Domain Layer

### 9.1 Buat domain model
File:
```text
domain/model/User.kt
domain/model/City.kt
domain/model/Gender.kt
domain/model/SyncStatus.kt
```

Checklist User model:
- [ ] id: String
- [ ] name: String
- [ ] email: String
- [ ] phoneNumber: String
- [ ] address: String
- [ ] city: String
- [ ] gender: Gender
- [ ] photoUri: String?
- [ ] syncStatus: SyncStatus

### 9.2 Buat repository interface
File:
```text
domain/repository/UserRepository.kt
domain/repository/CityRepository.kt
```

Checklist UserRepository:
- [ ] observeUsers(): Flow<List<User>>
- [ ] refreshUsers(): Result<Unit>
- [ ] addUser(user): Result<Unit>
- [ ] syncPendingUsers(): Result<Unit>

Checklist CityRepository:
- [ ] observeCities(): Flow<List<City>>
- [ ] refreshCities(): Result<Unit>

### 9.3 Buat use case
Use case:
- [ ] ObserveUsersUseCase
- [ ] RefreshUsersUseCase
- [ ] AddUserUseCase
- [ ] ObserveCitiesUseCase
- [ ] RefreshCitiesUseCase
- [ ] SyncPendingUsersUseCase
- [ ] FilterSortSearchUsersUseCase

Commit:
```bash
git add .
git commit -m "feat: add domain models and use cases"
```

---

## 10. Setup Remote Layer

### 10.1 Buat DTO
File:
```text
data/remote/dto/UserDto.kt
data/remote/dto/CreateUserRequest.kt
data/remote/dto/CityDto.kt
```

Checklist UserDto:
- [ ] name
- [ ] address
- [ ] email
- [ ] phoneNumber
- [ ] city
- [ ] gender
- [ ] id

### 10.2 Buat API service
File:
```text
data/remote/api/AccurateApiService.kt
```

Endpoint:
- [ ] GET user
- [ ] POST user
- [ ] GET city

### 10.3 Buat network module
File:
```text
di/NetworkModule.kt
```

Checklist:
- [ ] Provide Moshi.
- [ ] Provide OkHttpClient.
- [ ] Provide Retrofit.
- [ ] Provide AccurateApiService.
- [ ] Base URL benar.

Commit:
```bash
git add .
git commit -m "feat: add mockapi remote data source"
```

---

## 11. Setup Local Database Room

### 11.1 Buat Entity
File:
```text
data/local/entity/UserEntity.kt
data/local/entity/CityEntity.kt
```

Checklist UserEntity:
- [ ] id sebagai PrimaryKey.
- [ ] name.
- [ ] email.
- [ ] phoneNumber.
- [ ] address.
- [ ] city.
- [ ] gender.
- [ ] photoUri nullable.
- [ ] syncStatus.
- [ ] createdAt.
- [ ] updatedAt.

### 11.2 Buat DAO
File:
```text
data/local/dao/UserDao.kt
data/local/dao/CityDao.kt
```

Checklist UserDao:
- [ ] observeUsers(): Flow<List<UserEntity>>
- [ ] getPendingUsers(): List<UserEntity>
- [ ] upsertUsers(users)
- [ ] insertUser(user)
- [ ] updateSyncStatus(id, status)
- [ ] deleteAllSyncedUsers jika diperlukan

Checklist CityDao:
- [ ] observeCities(): Flow<List<CityEntity>>
- [ ] upsertCities(cities)

### 11.3 Buat Database
File:
```text
core/database/AccurateDatabase.kt
```

Checklist:
- [ ] Register entity.
- [ ] Register dao.
- [ ] Version 1.
- [ ] exportSchema optional.

### 11.4 Buat DatabaseModule
File:
```text
di/DatabaseModule.kt
```

Checklist:
- [ ] Provide Room database.
- [ ] Provide UserDao.
- [ ] Provide CityDao.

Commit:
```bash
git add .
git commit -m "feat: add room database cache"
```

---

## 12. Buat Mapper

File:
```text
data/mapper/UserMapper.kt
data/mapper/CityMapper.kt
```

Checklist mapper:
- [ ] DTO ke Entity.
- [ ] Entity ke Domain.
- [ ] Domain ke Entity.
- [ ] Domain ke CreateUserRequest.
- [ ] City DTO ke Entity.
- [ ] City Entity ke Domain.
- [ ] Gender mapping 0/1 aman.
- [ ] Null/default handling aman.

Commit:
```bash
git add .
git commit -m "feat: add data mappers"
```

---

## 13. Buat Repository Implementation

### 13.1 UserRepositoryImpl
File:
```text
data/repository/UserRepositoryImpl.kt
```

Flow utama:
1. UI observe data dari Room.
2. Saat refresh, ambil data dari API.
3. Simpan API response ke Room.
4. UI otomatis update dari Room.
5. Jika refresh gagal, data lama tetap tampil.

Checklist:
- [ ] observeUsers dari UserDao.
- [ ] refreshUsers fetch API lalu upsert Room.
- [ ] addUser saat online POST ke API lalu simpan response ke Room.
- [ ] addUser saat offline simpan local dengan PENDING_CREATE.
- [ ] syncPendingUsers kirim pending user ke API.
- [ ] Error tidak membuat app crash.

### 13.2 CityRepositoryImpl
Checklist:
- [ ] observeCities dari Room.
- [ ] refreshCities dari API.
- [ ] fallback jika city API gagal.

### 13.3 RepositoryModule
File:
```text
di/RepositoryModule.kt
```

Checklist:
- [ ] Bind UserRepository.
- [ ] Bind CityRepository.

Commit:
```bash
git add .
git commit -m "feat: implement offline first repositories"
```

---

## 14. Setup Navigation

Routes:
- Splash
- UserList
- AddUser
- Activity
- Settings

File:
```text
presentation/navigation/AppNavigation.kt
presentation/navigation/AppRoute.kt
```

Checklist:
- [ ] Splash menuju UserList.
- [ ] Bottom navigation untuk Users, Activity, Settings.
- [ ] FAB atau button menuju AddUser.
- [ ] Back dari AddUser kembali ke list.
- [ ] Navigation tidak duplikatif.

Commit:
```bash
git add .
git commit -m "feat: add app navigation shell"
```

---

## 15. Buat Splash Screen

UI sesuai desain:
- Logo Accurate.
- Text singkat.
- Loading indicator.

Checklist:
- [ ] Splash tampil 1-2 detik.
- [ ] Trigger initial refresh user dan city jika ingin.
- [ ] Navigate ke UserList.

Commit:
```bash
git add .
git commit -m "feat: add splash screen"
```

---

## 16. Buat User List Screen

### 16.1 UI utama
Komponen:
- [ ] Top bar.
- [ ] Search input.
- [ ] Filter button.
- [ ] Active filter chips.
- [ ] Sort dropdown.
- [ ] Summary card.
- [ ] User card list.
- [ ] FAB tambah user.
- [ ] Bottom navigation.

### 16.2 ViewModel
File:
```text
presentation/userlist/UserListViewModel.kt
presentation/userlist/UserListState.kt
presentation/userlist/UserListEvent.kt
```

State:
- [ ] users
- [ ] filteredUsers
- [ ] query
- [ ] selectedCities
- [ ] selectedGender
- [ ] sortOrder
- [ ] isLoading
- [ ] isRefreshing
- [ ] isOffline
- [ ] errorMessage
- [ ] lastUpdatedAt

Event:
- [ ] OnSearchChanged
- [ ] OnSortChanged
- [ ] OnFilterClicked
- [ ] OnApplyFilter
- [ ] OnResetFilter
- [ ] OnRefresh
- [ ] OnAddUserClicked
- [ ] OnRetryClicked

### 16.3 Logic
Checklist:
- [ ] Search by name/email/city.
- [ ] Sort A-Z.
- [ ] Sort Z-A.
- [ ] Filter by city.
- [ ] Filter by gender.
- [ ] Active chips removable.
- [ ] Summary count sesuai hasil filter.

Commit:
```bash
git add .
git commit -m "feat: add user list screen with search and sorting"
```

---

## 17. Buat Filter Bottom Sheet

Komponen:
- Search city input.
- Checkbox city.
- Radio/checkbox gender.
- Reset button.
- Apply button.

Checklist:
- [ ] Bottom sheet muncul dari tombol filter.
- [ ] City list dari Room/API city.
- [ ] Bisa pilih lebih dari satu kota.
- [ ] Bisa pilih gender.
- [ ] Reset menghapus semua filter.
- [ ] Apply menerapkan filter.
- [ ] Filter aktif muncul sebagai chip.

Commit:
```bash
git add .
git commit -m "feat: add filter bottom sheet"
```

---

## 18. Buat Add User Screen

### 18.1 UI form
Field:
- [ ] Foto optional.
- [ ] Nama lengkap wajib.
- [ ] Email wajib.
- [ ] No. handphone wajib.
- [ ] Alamat.
- [ ] Kota wajib.
- [ ] Gender wajib.
- [ ] Button Simpan User.

### 18.2 Photo Picker
Checklist:
- [ ] Klik area foto membuka Android Photo Picker.
- [ ] Preview foto tampil setelah dipilih.
- [ ] Simpan URI lokal ke state.
- [ ] Jika upload tidak didukung API, jangan kirim file ke API.

### 18.3 Validasi form
Checklist:
- [ ] Nama tidak boleh kosong.
- [ ] Email harus format valid.
- [ ] Phone tidak boleh kosong.
- [ ] Kota tidak boleh kosong.
- [ ] Gender harus dipilih.
- [ ] Button disabled saat form invalid.
- [ ] Loading saat submit.
- [ ] Error message jika gagal.
- [ ] Success kembali ke UserList.

### 18.4 ViewModel
File:
```text
presentation/adduser/AddUserViewModel.kt
presentation/adduser/AddUserState.kt
presentation/adduser/AddUserEvent.kt
```

Commit:
```bash
git add .
git commit -m "feat: add create user form"
```

---

## 19. Offline dan Sync

### 19.1 Network Monitor
File:
```text
core/network/NetworkMonitor.kt
```

Checklist:
- [ ] Deteksi online/offline.
- [ ] Expose Flow<Boolean>.
- [ ] UI bisa menampilkan offline banner.

### 19.2 Offline list
Checklist:
- [ ] Matikan internet.
- [ ] App tetap menampilkan data terakhir dari Room.
- [ ] Search/filter tetap jalan dari data lokal.
- [ ] Banner offline muncul.

### 19.3 Offline add user
Checklist nice-to-have:
- [ ] Saat offline, user baru disimpan lokal dengan PENDING_CREATE.
- [ ] Tampilkan label pending/sync.
- [ ] Saat online kembali, WorkManager menjalankan sync.
- [ ] Setelah sync berhasil, status berubah SYNCED.

### 19.4 WorkManager
File:
```text
worker/SyncUserWorker.kt
```

Checklist:
- [ ] Worker mengambil pending user.
- [ ] Worker POST ke API.
- [ ] Worker update status setelah berhasil.
- [ ] Worker retry saat gagal.
- [ ] Constraint network connected.

Commit:
```bash
git add .
git commit -m "feat: add offline sync support"
```

---

## 20. UI States

### 20.1 Loading state
Checklist:
- [ ] Skeleton list saat initial loading.
- [ ] Tidak hanya blank screen.

### 20.2 Empty state
Checklist:
- [ ] Jika tidak ada user, tampilkan empty state.
- [ ] Ada CTA Tambah User.

### 20.3 Error state
Checklist:
- [ ] Jika API gagal dan Room kosong, tampilkan error state.
- [ ] Ada tombol Coba Lagi.

### 20.4 Offline state
Checklist:
- [ ] Offline banner muncul.
- [ ] Data cached tetap tampil.
- [ ] Tampilkan last sync time jika ada.

Commit:
```bash
git add .
git commit -m "feat: add loading empty error and offline states"
```

---

## 21. Activity dan Settings Tab

### 21.1 Activity tab
MVP ringan:
- [ ] Tampilkan daftar aktivitas sederhana dari local events.
- [ ] Contoh event: refresh data, tambah user, sync berhasil, sync gagal.
- [ ] Jika terlalu berat, tampilkan placeholder informatif.

### 21.2 Settings tab
MVP ringan:
- [ ] Tampilkan app info.
- [ ] Tampilkan cache status.
- [ ] Button refresh data.
- [ ] Button clear local cache jika aman.
- [ ] Toggle sederhana seperti enable analytics placeholder jika belum pakai Firebase.

Commit:
```bash
git add .
git commit -m "feat: add activity and settings tabs"
```

---

## 22. Firebase Analytics Optional

Jika waktu cukup:
Event yang bisa dicatat:
- [ ] app_opened
- [ ] user_list_viewed
- [ ] user_search_used
- [ ] filter_applied
- [ ] sort_changed
- [ ] add_user_submitted
- [ ] add_user_success
- [ ] add_user_failed
- [ ] offline_mode_shown

Commit:
```bash
git add .
git commit -m "feat: add firebase analytics events"
```

---

## 23. Testing Manual

### 23.1 Test online
Checklist:
- [ ] App buka normal.
- [ ] Data user muncul.
- [ ] Search berhasil.
- [ ] Sort A-Z berhasil.
- [ ] Sort Z-A berhasil.
- [ ] Filter kota berhasil.
- [ ] Reset filter berhasil.
- [ ] Add user berhasil.
- [ ] Data baru muncul di list.

### 23.2 Test offline
Checklist:
- [ ] Buka app saat data sudah pernah tersimpan.
- [ ] Matikan internet.
- [ ] Data tetap muncul.
- [ ] Search tetap jalan.
- [ ] Filter tetap jalan.
- [ ] Offline banner muncul.
- [ ] Retry tidak crash.

### 23.3 Test empty/error
Checklist:
- [ ] Jika API gagal dan DB kosong, error state muncul.
- [ ] Jika hasil filter kosong, empty state muncul.
- [ ] Jika search tidak ditemukan, empty state muncul.

### 23.4 Test form
Checklist:
- [ ] Form invalid tidak bisa submit.
- [ ] Email invalid muncul error.
- [ ] Gender wajib dipilih.
- [ ] City wajib dipilih.
- [ ] Foto bisa dipilih dan preview muncul.
- [ ] Submit loading muncul.

Commit:
```bash
git add .
git commit -m "test: complete manual mvp validation"
```

---

## 24. Unit Testing Minimal

Prioritas test:
- [ ] FilterSortSearchUsersUseCaseTest.
- [ ] AddUserUseCaseTest.
- [ ] UserRepositoryImplTest jika sempat.
- [ ] UserListViewModelTest jika sempat.

Skenario FilterSortSearch:
- [ ] Query kosong mengembalikan semua data.
- [ ] Query nama mengembalikan user sesuai.
- [ ] Query email mengembalikan user sesuai.
- [ ] Filter city mengembalikan user sesuai.
- [ ] Sort A-Z benar.
- [ ] Sort Z-A benar.

Commit:
```bash
git add .
git commit -m "test: add user filtering and sorting tests"
```

---

## 25. Polish UI

Checklist:
- [ ] Jarak antar komponen konsisten.
- [ ] Warna sesuai desain.
- [ ] Button state jelas.
- [ ] Font hierarchy jelas.
- [ ] Card user rapi.
- [ ] Bottom sheet nyaman.
- [ ] Keyboard tidak menutup form.
- [ ] Scroll form lancar.
- [ ] Loading tidak flicker berlebihan.
- [ ] Error message mudah dipahami.

Commit:
```bash
git add .
git commit -m "style: polish user interface"
```

---

## 26. README Final

Isi README:
- [ ] Nama aplikasi.
- [ ] Deskripsi singkat.
- [ ] Fitur utama.
- [ ] Screenshot aplikasi.
- [ ] Cara menjalankan project.
- [ ] Tech stack.
- [ ] Architecture.
- [ ] Offline-first strategy.
- [ ] API yang digunakan.
- [ ] Penjelasan UI/UX.
- [ ] Cara build APK.
- [ ] Catatan trade-off.
- [ ] Hal yang akan dikembangkan jika ada waktu tambahan.

Contoh trade-off:
```text
Photo upload is implemented locally using Android Photo Picker because the provided MockAPI endpoint does not expose a dedicated file upload endpoint. The selected image URI is stored locally and can be extended later with cloud storage or multipart upload API.
```

Commit:
```bash
git add README.md
git commit -m "docs: add project documentation"
```

---

## 27. Build APK

Langkah:
1. Buka Android Studio.
2. Pilih Build.
3. Pilih Build Bundle(s) / APK(s).
4. Pilih Build APK(s).
5. Ambil file APK dari output.

Checklist:
- [ ] APK berhasil dibuat.
- [ ] APK bisa diinstall di HP.
- [ ] APK berjalan normal.

Commit:
```bash
git add .
git commit -m "chore: prepare apk build"
```

---

## 28. Final Git Check

Checklist:
- [ ] Tidak ada file rahasia.
- [ ] Tidak ada local.properties ter-commit.
- [ ] Tidak ada build folder ter-commit.
- [ ] .gitignore benar.
- [ ] Commit history rapi.
- [ ] Branch main terbaru.
- [ ] Push ke GitHub.

Command:
```bash
git status
git log --oneline
git push origin main
```

---

## 29. Urutan Kerja 3 Hari

### Hari 1: Fondasi
- [ ] Setup project.
- [ ] Setup GitHub.
- [ ] Setup dependency.
- [ ] Setup folder Clean Architecture.
- [ ] Setup Hilt.
- [ ] Setup Retrofit.
- [ ] Setup Room.
- [ ] Setup repository offline-first.
- [ ] Tampilkan list user pertama kali.

Target hari 1:
Aplikasi bisa fetch user dari API, simpan ke Room, dan tampilkan list.

### Hari 2: Core Feature
- [ ] Search.
- [ ] Sort.
- [ ] Filter city.
- [ ] Filter bottom sheet.
- [ ] Add user.
- [ ] Photo picker.
- [ ] Offline cached list.
- [ ] UI states.

Target hari 2:
Semua fitur wajib brief sudah jalan.

### Hari 3: Polish dan submit
- [ ] WorkManager sync jika sempat.
- [ ] Activity tab.
- [ ] Settings tab.
- [ ] Testing manual.
- [ ] Unit test minimal.
- [ ] README.
- [ ] Screenshot.
- [ ] Build APK.
- [ ] Push final ke GitHub.

Target hari 3:
Project siap dikirim.

---

## 30. Prioritas Kalau Waktu Mepet

### Wajib selesai
1. List user.
2. Search.
3. Sort.
4. Filter city.
5. Add user.
6. Offline cache Room.
7. Loading/error/empty state.
8. README.

### Bisa dikurangi
1. Firebase Analytics.
2. Full Activity log.
3. Advanced Settings.
4. Adaptive tablet layout.
5. Full offline add sync.

### Jangan dikorbankan
1. App stability.
2. Clean folder structure.
3. README.
4. Git commit history.
5. Basic offline list.

---

## 31. Checklist Sebelum Submit

- [ ] Aplikasi bisa dijalankan dari fresh clone.
- [ ] README jelas.
- [ ] API endpoint benar.
- [ ] Tidak crash saat offline.
- [ ] Add user berhasil.
- [ ] Filter dan sort bekerja.
- [ ] UI sesuai desain besar.
- [ ] GitHub link bisa dibuka.
- [ ] Commit history terlihat incremental.
- [ ] APK tersedia jika ingin dilampirkan.

---

## 32. Cara Pengerjaan yang Disarankan

Jangan coding random. Pakai urutan ini:

1. Jalankan project kosong.
2. Tambahkan dependency.
3. Buat folder architecture.
4. Buat model domain.
5. Buat Retrofit.
6. Buat Room.
7. Buat repository.
8. Tampilkan list user.
9. Tambahkan search.
10. Tambahkan sort.
11. Tambahkan filter.
12. Tambahkan add user.
13. Tambahkan offline state.
14. Tambahkan polish UI.
15. Tambahkan README.
16. Build APK.
17. Submit.

Selesai.
