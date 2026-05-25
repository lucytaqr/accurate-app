# Accurate User Directory — Android App

Aplikasi Android untuk melihat daftar user, mencari, mengurutkan, memfilter, menambahkan, mengedit, menghapus user, dan tetap berfungsi saat offline. Dibangun dengan **Kotlin**, **Jetpack Compose**, **Clean Architecture + MVVM**.

---

## 📱 Cara Penggunaan Aplikasi

### Splash Screen → User List
- App terbuka → **splash screen** 1,5 detik dengan logo Accurate
- Trigger refresh data dari MockAPI, simpan ke Room
- Masuk **User List** — data dari cache lokal, API refresh di background

### 🔍 Search User
- Ketik di search bar `Cari nama, email, atau kota...`
- Filter langsung real-time dari data Room (tanpa API call)
- Klik ikon ❌ untuk clear

### 📊 Sort User
- Dropdown `Urutkan: A-Z` / `Z-A`
- Klik pilihan → list langsung terurut

### 🎛️ Filter User
- Klik tombol **Filter** → **bottom sheet** muncul
- Pilih **Kota** (checkbox multi-select) + cari kota di dalam bottom sheet
- Pilih **Gender** (radio: Semua / Male / Female)
- Klik **Terapkan Filter** (tombol sticky di bawah, selalu terlihat)
- Filter aktif muncul sebagai **removable chips**
- Klik **Reset** untuk hapus semua filter

### ➕ Tambah User
- Klik **FAB ➕** di kanan bawah
- Isi form full screen:
  - **Nama Lengkap** — wajib
  - **Email** — wajib, format dicek
  - **No. Handphone** — wajib
  - **Alamat** — opsional
  - **Kota** — dropdown dari data city
  - **Jenis Kelamin** — radio Male / Female
- Online → POST ke MockAPI → **Synced** di list
- Offline → simpan pending → label **"Pending"** kuning

### ✏️ Edit User
- Klik icon **✏️** (pencil) di card user
- Form Add User muncul pre-filled dengan data existing
- Ubah data → klik **Simpan Perubahan**
- Online → PUT ke API + update Room
- Offline → update langsung di Room

### 🗑️ Delete User
- Klik icon **🗑️** (trash) di card user
- Konfirmasi dialog muncul → klik **Hapus**
- Online → DELETE ke API + hapus dari Room

### 📴 Offline Mode
- Matikan internet → **banner "Anda sedang offline"** kuning muncul di atas
- Data cache dari Room tetap tampil normal
- Search, sort, filter tetap jalan
- Tambah user offline → label **"Pending"** di card
- Edit user offline → tetap tersimpan lokal

### 🔄 Sync (WorkManager + Settings)
- Saat online kembali → WorkManager otomatis sync pending user
- Manual: **Settings tab** → klik **Sinkronkan Pending User**
- Bisa juga **Refresh Data** untuk ambil data terbaru dari API

### 📋 Activity Tab
- Lihat log aktivitas: refresh data, tambah/edit/hapus user, sync berhasil/gagal

### ⚙️ Settings Tab
- Info App: versi, API source (MockAPI)
- Status sync: last sync time, jumlah pending
- Tombol: **Refresh Data**, **Sinkronkan Pending User**

---
## 🛠️ Teknologi

| Layer | Teknologi | Versi |
|-------|-----------|-------|
| Language | Kotlin | 1.9.22 |
| UI Framework | Jetpack Compose + Material 3 | BOM 2024.02 |
| Architecture | Clean Architecture + MVVM | — |
| DI | Hilt | 2.51 |
| Navigation | Navigation Compose | 2.7.7 |
| Networking | Retrofit + OkHttp | 2.9.0 / 4.12.0 |
| JSON | Moshi + KSP Codegen | 1.15.1 |
| Database | Room | 2.6.1 |
| Background | WorkManager | 2.9.0 |
| Images | Coil Compose | 2.6.0 |
| Photo Picker | Android Photo Picker | — |
| Logging | Timber | 5.0.1 |
| Unit Test | JUnit 4, Coroutines Test, Turbine | 1.0.0 |
| Min SDK | Android 8 (API 26) | — |
| Target SDK | Android 14 (API 34) | — |

---

## 🎨 Kenapa Tampilan/Interaksi Seperti Itu

| Keputusan UX | Alasan |
|---|---|
| **Search real-time client-side** | Memfilter dari Room langsung, tanpa API call. Lebih cepat, tidak delay, tidak ada loading spinner |
| **Filter via bottom sheet** | Tidak pindah screen, user tetap melihat list di balik sheet. Konteks tidak hilang |
| **Filter chips removable** | User selalu tahu filter apa yang aktif, bisa hapus satu per satu tanpa reset semua |
| **Sort dropdown sederhana** | Hanya A-Z / Z-A, tidak perlu tombol terpisah. Hemat space, intuitif |
| **Tombol Reset + Terapkan sticky di bawah bottom sheet** | Selalu terlihat, tidak perlu scroll jauh. UX konsisten |
| **FAB untuk tambah user** | Akses cepat dari mana saja di Users tab, standar Material Design |
| **Form full-screen untuk add/edit** | Input banyak field, perlu fokus penuh. Validasi per field, error di bawah input |
| **Edit/Delete icon di card** | Akses satu klik, tidak perlu buka detail dulu. Pencil ✏️ dan Trash 🗑️ ikon universal |
| **Konfirmasi delete dialog** | Mencegah hapus tidak sengaja. Konfirmasi eksplisit sebelum eksekusi |
| **Offline banner (kuning, tidak intrusive)** | Informasi penting tanpa memblokir app. Data cache tetap bisa diakses |
| **Label "Pending" di card** | User tahu status sync. Kuning = belum tersinkron, transparan = synced |
| **Loading skeleton** | UI tidak blank saat loading. User tahu apa yang akan muncul (bentuk card) |
| **Empty state + CTA** | Bukan pesan error. Ada tombol "Tambah User" untuk next action |
| **Error state + retry** | Hanya full page jika cache kosong. Kalau cache ada, cukup snackbar kecil |
| **Bottom navigation 3 tab** | Users (utama), Activity (log), Settings (kontrol). Skema standar apps directory |
| **Status bar pink** | Sesuai warna branding Accurate, konsisten dari splash sampai settings |
| **Card rounded + shadow ringan** | Bersih, modern, mengikuti tren Material 3 |
| **Warna gender (biru = Male, pink = Female)** | Kontras visual, langsung dikenali tanpa baca teks |
| **Phone number wajib** | Data user bisnis perlu kontak telepon. Tidak opsional seperti alamat |

---

## 🏗️ Architecture

### Struktur Package (Clean Architecture)

```
app/src/main/java/com/accurate/userdirectory/
├── MainActivity.kt                  # @AndroidEntryPoint entry point
├── AccurateApp.kt                   # @HiltAndroidApp
│
├── presentation/                    # Layer 1: UI + ViewModel
│   ├── navigation/                  # NavGraph, routes, bottom nav
│   ├── splash/                      # SplashScreen + ViewModel
│   ├── users/                       # UserList (search, sort, filter)
│   ├── adduser/                     # AddUser / EditUser form
│   ├── activity/                    # Activity log screen
│   └── settings/                    # Settings screen
│
├── domain/                          # Layer 2: Business Logic (pure Kotlin)
│   ├── model/                       # User, City, Gender, SyncStatus, dll
│   ├── repository/                  # Interface: UserRepository, dll
│   └── usecase/                     # Use cases: AddUser, UpdateUser, dll
│
├── data/                            # Layer 3: Data Sources
│   ├── remote/                      # Retrofit API, DTOs
│   ├── local/                       # Room Entity, DAO
│   ├── mapper/                      # DTO ↔ Entity ↔ Domain
│   └── repository/                  # Repository implementations
│
├── core/                            # Shared utilities
│   ├── common/                      # UiText
│   ├── designsystem/                # Colors, Typography, Components
│   ├── network/                     # NetworkMonitor
│   └── database/                    # Room Database
│
├── di/                              # Hilt modules
└── worker/                          # WorkManager (UserSyncWorker)
```

### Alur Data — Clean Architecture + Offline-First

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│                                                             │
│   UserListScreen (Compose)                                  │
│        ↕ observe StateFlow                                  │
│   UserListViewModel                                          │
│        │                                                    │
│        │ call usecase                                       │
│        ▼                                                    │
├─────────────────────────────────────────────────────────────┤
│                     DOMAIN LAYER                             │
│                                                             │
│   ObserveUsersUseCase / AddUserUseCase / UpdateUserUseCase  │
│   DeleteUserUseCase / SyncPendingUsersUseCase               │
│        │                                                    │
│        │ call interface                                     │
│        ▼                                                    │
│   UserRepository (interface — pure Kotlin)                  │
│   CityRepository  (interface — pure Kotlin)                 │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│                      DATA LAYER                              │
│                                                             │
│   UserRepositoryImpl                                         │
│        │                                                    │
│        ├──── Remote:   Retrofit → MockAPI (GET/POST/PUT/DEL)│
│        │                                                    │
│        ├──── Local:    Room Database (Single Source of Truth)│
│        │                                                    │
│        └──── Network:  NetworkMonitor → isOnline: Flow      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow Detail

```
ONLINE:                                    OFFLINE:
  UI observe Room Flow                      UI observe Room Flow
       │                                         │
  data dari Room (cache)                    data dari Room (cache saja)
       │                                         │
  ViewModel.refreshUsers()                  NetworkMonitor.isOnline = false
       │                                         │
  GET /user (Retrofit)                      Banner "Anda sedang offline"
       │                                         │
  Map DTO → Entity                          Add/Edit user:
       │                                    └→ Room (PendingCreate)
  UPSERT ke Room                                   │
       │                                    WorkManager enqueue
  Room Flow auto-emit                              │
       │                                    ┌─────▼─────┐
  UI auto-update                            │ KEMBALI   │
                                            │ ONLINE    │
ADD USER (online):                          │    │      │
  POST /user → Room (Synced)               │    ▼      │
       │                                    │ Worker    │
  UI update                                 │ POST each │
                                            │ pending   │
EDIT USER (online + punya remoteId):        │    │      │
  PUT /user/{id} → Room (Synced)           │ Sukses?   │
       │                                    │ ├─Ya: Sync│
  UI update                                 │ └─No: Ret │
                                            └───────────┘
DELETE USER (online + punya remoteId):
  DELETE /user/{id} → hapus dari Room
       │
  UI update
```

### Offline-First Rule

1. **Room adalah Single Source of Truth** — UI hanya observe dari Room, tidak pernah langsung dari API
2. **API untuk refresh & tulis** — GET untuk refresh data, POST/PUT/DELETE untuk operasi remote
3. **Offline tidak memblokir** — baca tetap jalan dari cache, tulis disimpan pending
4. **WorkManager sync** — pending user dikirim ke API saat online kembali
5. **Error tidak crash** — cache tetap tampil, error state muncul jika cache kosong

### Gender Mapping

| API Value | Gender |
|-----------|--------|
| `0` | Male |
| `1` | Female |

---

### API Endpoints

| Method | Endpoint | Base URL |
|--------|----------|----------|
| `GET` | `/api/v2/accurate/user` | `https://661f555f16358961cd940b83.mockapi.io/` |
| `POST` | `/api/v2/accurate/user` | |
| `PUT` | `/api/v2/accurate/user/{id}` | |
| `DELETE` | `/api/v2/accurate/user/{id}` | |
| `GET` | `/api/v2/accurate/city` | |

---


