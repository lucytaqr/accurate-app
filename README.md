# Accurate User Directory — Android App

Aplikasi Android untuk melihat daftar user, mencari, mengurutkan, memfilter, menambah user, dan tetap berfungsi saat offline. Dibangun dengan Kotlin, Jetpack Compose, Clean Architecture + MVVM.

---

## Fitur Utama

| Fitur | Status |
|---|---|
| Daftar user dari MockAPI | Selesai |
| Search user (nama, email, kota) | Selesai |
| Sort A-Z / Z-A | Selesai |
| Filter kota (multi-select) | Selesai |
| Filter gender (Male/Female) | Selesai |
| Active filter chips (removable) | Selesai |
| Filter bottom sheet | Selesai |
| Tambah user (form + validasi) | Selesai |
| Photo picker (foto profil lokal) | Selesai |
| Offline cache (Room) | Selesai |
| Add user offline (pending queue) | Selesai |
| Sync pending dengan WorkManager | Selesai |
| Activity log screen | Selesai |
| Settings screen (info + sync) | Selesai |
| Firebase Analytics events | Selesai |
| UI states: loading, empty, error, offline | Selesai |
| Unit tests | Selesai |

---

## Tech Stack

| Layer | Teknologi |
|---|---|
| Language | Kotlin 1.9.22 |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt 2.51 |
| Networking | Retrofit 2.9 + Moshi 1.15 + OkHttp 4.12 |
| Local DB | Room 2.6.1 |
| Background | WorkManager 2.9 |
| Images | Coil Compose 2.6 |
| Logging | Timber 5.0 |
| Analytics | Firebase Analytics |
| Testing | JUnit 4, Mockito-Kotlin, Coroutines Test, Turbine |

---

## Architecture

```
app/src/main/java/com/accurate/userdirectory/
├── AccurateApp.kt              # @HiltAndroidApp
├── MainActivity.kt             # @AndroidEntryPoint + Compose
├── core/
│   ├── common/                  # ResultState, UiText, Dispatchers
│   ├── designsystem/            # Theme, colors, components
│   ├── network/                 # NetworkMonitor, ApiErrorHandler
│   └── database/                # AccurateDatabase
├── data/
│   ├── remote/                  # DTO, API service (Retrofit)
│   ├── local/                   # Entity, DAO (Room)
│   ├── mapper/                  # DTO <-> Entity <-> Domain
│   └── repository/              # Repository implementations
├── domain/
│   ├── model/                   # User, City, Gender, etc.
│   ├── repository/              # Repository interfaces
│   └── usecase/                 # Use cases
├── presentation/
│   ├── navigation/              # NavGraph, routes, bottom nav
│   ├── splash/                  # Splash screen
│   ├── users/                   # User list + search + filter
│   ├── adduser/                 # Add user form + photo picker
│   ├── activity/                # Activity log
│   └── settings/                # App settings + sync
├── di/                          # Hilt modules
├── worker/                      # UserSyncWorker (WorkManager)
└── analytics/                   # Firebase Analytics helper
```

### Data Flow (Offline-First)

```
UI <-> ViewModel <-> UseCase <-> Repository <-> Remote API + Room DB
                                  Room adalah single source of truth
                                  UI selalu observe data dari Room
                                  API hanya untuk refresh + POST remote
```

---

## API

| Method | Endpoint | Base URL |
|---|---|---|
| GET | `/api/v2/accurate/user` | `https://661f555f16358961cd940b83.mockapi.io/` |
| POST | `/api/v2/accurate/user` | |
| GET | `/api/v2/accurate/city` | |

### Gender Mapping

- `0` = Male
- `1` = Female

---

## Cara Menjalankan Project

1. Clone repository
2. Buka project di Android Studio (Hedgehog 2024.1+)
3. Sync Gradle
4. Tambahkan `google-services.json` dari Firebase Console di `app/`
5. Jalankan di emulator atau device (min SDK 26)

## Cara Build APK

1. **Debug:** `Build > Build Bundle(s) / APK(s) > Build APK(s)`
2. **Release:** `Build > Generate Signed Bundle / APK` (perlu keystore)

APK hasil build ada di `app/build/outputs/apk/debug/`.

---

## Offline Behavior

| Kondisi | Behavior |
|---|---|
| Online | Data di-refresh dari API, disimpan ke Room, UI tampilkan dari Room |
| Offline | Data cache Room tetap ditampilkan, banner offline muncul |
| Add user offline | Disimpan lokal dengan status `PendingCreate` |
| Kembali online | WorkManager sync pending users ke MockAPI |

---

## UI/UX Decisions

| Area | Keputusan |
|---|---|
| Search | Client-side filtering dari data Room agar instan |
| Sort | Sort lokal A-Z/Z-A berdasarkan nama |
| Filter | Bottom sheet agar user tidak kehilangan konteks list |
| Active chips | Removable filter chips untuk feedback visual |
| Add user | Form full-screen dengan validasi di setiap field |
| Photo | Android Photo Picker, URI disimpan lokal |
| Offline | Banner ringan, tidak memblokir app jika cache ada |
| Error | Full error state hanya jika cache kosong |
| Sync | Status kecil, tidak mengganggu flow utama |

---

## Known Limitations & Trade-offs

1. **Foto tidak di-upload ke server** — MockAPI tidak menyediakan endpoint upload file. Foto disimpan sebagai URI lokal di Room. Jika endpoint menerima field `photoUri`, dikirim sebagai string opsional.
2. **City API format defensif** — Parser dibuat defensif; jika city API gagal, fallback ke daftar kota unik dari data user.
3. **Duplicate prevention terbatas** — MockAPI tidak mendukung idempotency key. Duplicate bisa terjadi jika POST sukses tapi response gagal diterima.
4. **Firebase Analytics** — Memerlukan `google-services.json` untuk berfungsi. Aplikasi tetap berjalan tanpa crash jika file tidak ada (analytics nonaktif).

---

## Future Improvements

- Image hosting/cloud storage untuk foto user
- Pagination untuk large dataset
- Full adaptive tablet layout
- Advanced sync conflict resolution
- Pull-to-refresh gesture
- Dark mode support

---

## Commit History

Gunakan Conventional Commits. Lihat `git log --oneline` untuk history lengkap.

```
chore: initialize android project structure
chore: add project dependencies and app setup
feat: add design system and core utilities
feat: define domain layer with models and use cases
feat: integrate mockapi network and room database cache
feat: implement offline first repositories with mappers
feat: add navigation shell and splash screen
feat: implement user list screen with search sort and filters
feat: implement add user form with photo picker
feat: add activity and settings tabs
feat: add pending user sync worker and firebase analytics
test: add unit tests for filtering sorting and form validation
docs: add final project readme
```
