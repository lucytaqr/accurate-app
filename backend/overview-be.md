# Backend Overview - MockAPI Contract and Integration Plan

Dokumen ini menggabungkan overview backend, API contract, sync contract, error handling, dan task list backend untuk MVP Accurate User Directory App.

Backend dalam project ini tidak dibuat sebagai server baru karena scope dikonfirmasi menggunakan MockAPI saja sesuai brief. Folder backend berfungsi sebagai dokumentasi kontrak, validasi endpoint, strategi integrasi, dan rencana handling data agar frontend tidak bergantung pada asumsi yang tidak jelas.

---

## 1. Backend Scope

### 1.1 Included

- Dokumentasi endpoint MockAPI.
- Dokumentasi DTO user.
- Dokumentasi DTO create user.
- Dokumentasi API city.
- Error handling contract untuk frontend.
- Offline sync contract.
- Validasi asumsi field gender.
- Strategi foto user jika endpoint menerima field tambahan.
- Optional local mock server untuk testing development.

### 1.2 Not Included

- Tidak membuat server production baru.
- Tidak membuat database backend sendiri.
- Tidak membuat authentication.
- Tidak membuat role/permission.
- Tidak membuat admin panel.
- Tidak membuat object storage file upload khusus.

---

## 2. API Base Information

```text
Base URL:
https://661f555f16358961cd940b83.mockapi.io/api/v2/accurate/
```

Endpoints:

```text
GET  /user
POST /user
GET  /city
```

---

## 3. User API Contract

### 3.1 GET User List

Request:

```http
GET /api/v2/accurate/user
```

Expected response:

```json
[
  {
    "name": "Tiko",
    "address": "Tangerang",
    "email": "tiko@gmail.com",
    "phoneNumber": "081398302869",
    "city": "Tangerang",
    "gender": 0,
    "id": "1"
  }
]
```

Frontend DTO:

```kotlin
@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String?,
    val name: String?,
    val address: String?,
    val email: String?,
    val phoneNumber: String?,
    val city: String?,
    val gender: Int?,
    val photoUri: String? = null,
    val photoUrl: String? = null
)
```

Notes:

- `id` dari MockAPI diperlakukan sebagai `remoteId`.
- DTO dibuat nullable agar parser aman jika data MockAPI berubah.
- `photoUri` dan `photoUrl` opsional karena tidak ada di brief resmi, hanya digunakan jika MockAPI menerima field tambahan.

### 3.2 POST Add User

Request:

```http
POST /api/v2/accurate/user
Content-Type: application/json
```

Body resmi sesuai brief:

```json
{
  "name": "Tiko",
  "address": "Tangerang",
  "email": "tiko@gmail.com",
  "phoneNumber": "0849098434",
  "city": "Tangerang",
  "gender": 0
}
```

Extended body jika MockAPI menerima field tambahan:

```json
{
  "name": "Tiko",
  "address": "Tangerang",
  "email": "tiko@gmail.com",
  "phoneNumber": "0849098434",
  "city": "Tangerang",
  "gender": 0,
  "photoUri": "content://media/picker/123"
}
```

Frontend request DTO:

```kotlin
@JsonClass(generateAdapter = true)
data class CreateUserRequestDto(
    val name: String,
    val address: String,
    val email: String,
    val phoneNumber: String,
    val city: String,
    val gender: Int,
    val photoUri: String? = null
)
```

Notes:

- Kirim field wajib resmi agar tetap sesuai brief.
- `photoUri` hanya opsional.
- Jika POST dengan `photoUri` menyebabkan error, fallback kirim tanpa `photoUri`.
- Untuk MVP yang aman, foto tetap disimpan lokal di Room.

### 3.3 Gender Contract

```text
0 = Male
1 = Female
```

Frontend rule:

- Jika API mengirim `0`, tampilkan `Male`.
- Jika API mengirim `1`, tampilkan `Female`.
- Jika API mengirim nilai selain itu, fallback ke `Male` atau tampilkan `Unknown` bila ingin lebih aman.

Recommended MVP:

```kotlin
fun Int?.toGender(): Gender = when (this) {
    1 -> Gender.Female
    else -> Gender.Male
}
```

---

## 4. City API Contract

### 4.1 GET City List

Request:

```http
GET /api/v2/accurate/city
```

Possible response format harus divalidasi langsung karena brief hanya memberi endpoint, bukan DTO lengkap.

Kemungkinan 1:

```json
[
  {
    "id": "1",
    "name": "Jakarta"
  }
]
```

Kemungkinan 2:

```json
[
  {
    "id": "1",
    "city": "Jakarta"
  }
]
```

Kemungkinan 3:

```json
[
  "Jakarta",
  "Bandung"
]
```

Frontend strategy:

- Buat parser defensif.
- Jika city API gagal, fallback ambil daftar kota unik dari user list.
- Jika city kosong, tetap izinkan input kota manual atau tampilkan dropdown dari data user.

Recommended DTO:

```kotlin
@JsonClass(generateAdapter = true)
data class CityDto(
    val id: String? = null,
    val name: String? = null,
    val city: String? = null
)
```

Mapper:

```kotlin
fun CityDto.toCityName(): String? {
    return name?.takeIf { it.isNotBlank() }
        ?: city?.takeIf { it.isNotBlank() }
}
```

---

## 5. Photo Upload Strategy

### 5.1 Problem

Brief API resmi tidak mencantumkan field foto dan tidak menyediakan endpoint upload file. Karena itu, upload foto remote penuh tidak bisa dijamin.

### 5.2 MVP Strategy

Gunakan pendekatan bertingkat:

1. Android Photo Picker memilih foto dari device.
2. Simpan URI foto di Room sebagai `photoUri`.
3. Tampilkan foto lokal di UI memakai Coil.
4. Saat POST user, coba kirim `photoUri` sebagai field opsional jika ingin.
5. Jika MockAPI tidak menyimpan field tambahan, aplikasi tetap valid karena foto berfungsi lokal.

### 5.3 Alternative If More Time

Jika ingin benar-benar remote photo URL:

- Tambahkan field `photoUrl` di MockAPI schema bila bisa diatur.
- Gunakan temporary image hosting/manual URL untuk development.
- Simpan URL tersebut ke MockAPI.

Namun untuk test 3 hari, ini tidak wajib karena brief inti tidak meminta foto. Foto berasal dari desain UI dan permintaan user.

---

## 6. Error Contract

Frontend harus mengubah error teknis menjadi pesan UI yang ramah.

| Error Type | Backend/Network Cause | UI Message |
|---|---|---|
| Offline | Tidak ada koneksi | `Anda sedang offline. Data terakhir tetap ditampilkan.` |
| Timeout | Request terlalu lama | `Koneksi lambat. Coba lagi beberapa saat.` |
| 4xx | Request tidak valid | `Data belum valid. Periksa kembali input Anda.` |
| 5xx | Server error | `Server sedang bermasalah. Coba lagi nanti.` |
| Parse error | Format response berubah | `Format data tidak sesuai. Coba lagi nanti.` |
| Unknown | Error tidak diketahui | `Terjadi kesalahan. Coba lagi.` |

Rule:

- Jika cache ada, jangan tampilkan full error page.
- Jika cache kosong, tampilkan full error page dengan retry.
- Jika add user gagal online, simpan sebagai pending jika error network/offline.
- Jika add user gagal karena validasi API, jangan masukkan pending otomatis kecuali input sudah valid lokal.

---

## 7. Offline Sync Contract

### 7.1 Local Pending User

Saat user menambah data offline:

```json
{
  "localId": "uuid-local",
  "remoteId": null,
  "name": "Alex River",
  "email": "alex@example.com",
  "phoneNumber": "08123456789",
  "address": "Jakarta",
  "city": "Jakarta",
  "gender": 0,
  "photoUri": "content://...",
  "syncStatus": "PendingCreate"
}
```

### 7.2 Sync Process

```text
1. WorkManager berjalan ketika network connected.
2. Ambil semua user dengan syncStatus PendingCreate.
3. POST setiap user ke MockAPI.
4. Jika sukses:
   - simpan remoteId dari response.
   - ubah syncStatus ke Synced.
   - tulis activity log.
5. Jika gagal karena network/server temporary:
   - tetap PendingCreate.
   - retry nanti.
6. Jika gagal karena data invalid:
   - ubah ke FailedCreate atau tetap PendingCreate dengan error message.
```

### 7.3 Duplicate Prevention

Karena MockAPI tidak mendukung idempotency key, duplicate bisa terjadi jika POST sukses tetapi app tidak menerima response karena network putus.

Mitigasi MVP:

- Simpan `localId` dan jangan hapus pending sampai response sukses diterima.
- Setelah sync sukses, update pending menjadi synced.
- Jika duplicate remote terjadi, ini diterima sebagai limitation MVP.

Mitigasi lanjutan:

- Tambahkan field `clientRequestId` pada POST jika MockAPI menerima field tambahan.
- Sebelum POST ulang, cek apakah user dengan email dan phone yang sama sudah ada di remote.

---

## 8. Backend Validation Assumptions

Validasi utama tetap dilakukan di frontend karena MockAPI biasanya menerima payload secara longgar.

Frontend validation contract:

| Field | Rule |
|---|---|
| name | required, min 2 char recommended |
| email | required, valid email format |
| phoneNumber | required, numeric-ish format |
| address | optional atau required ringan sesuai desain final |
| city | required |
| gender | required, 0 or 1 |
| photo | optional |

---

## 9. API Testing Checklist

Manual test dengan curl/Postman/HTTP client:

### 9.1 GET Users

- [ ] Endpoint return 200.
- [ ] Response berupa array.
- [ ] Setiap item punya minimal id/name/email/city.
- [ ] Gender bernilai 0 atau 1.
- [ ] Response tetap bisa diparse jika field kosong.

### 9.2 POST User

- [ ] POST body resmi berhasil.
- [ ] Response return user dengan id.
- [ ] POST dengan gender 0 berhasil.
- [ ] POST dengan gender 1 berhasil.
- [ ] POST dengan optional `photoUri` diuji.
- [ ] Jika `photoUri` tidak disimpan, frontend tetap tidak bergantung padanya.

### 9.3 GET City

- [ ] Endpoint return 200.
- [ ] Response format dikonfirmasi.
- [ ] Mapper city disesuaikan.
- [ ] Fallback dari user city bekerja jika city API gagal.

---

## 10. Local Mock Server Optional

Tidak wajib, tapi berguna jika MockAPI down.

Pilihan cepat:

- Gunakan file JSON statis di Android test.
- Gunakan MockWebServer untuk unit/integration test.
- Gunakan local JSON server jika ingin manual development.

MockWebServer test cases:

- GET users success.
- GET users empty.
- GET users 500.
- POST user success.
- POST user timeout.
- GET cities success.

---

## 11. Detailed Task List Backend

### Phase 1 - API Contract Documentation

- [ ] Document base URL.
- [ ] Document GET user endpoint.
- [ ] Document POST user endpoint.
- [ ] Document GET city endpoint.
- [ ] Document required fields.
- [ ] Document optional photo fields.
- [ ] Document gender mapping.
- [ ] Document frontend fallback behavior.
- [ ] Commit: `docs: add mockapi backend contract`.

### Phase 2 - MockAPI Validation

- [ ] Test GET `/user` manually.
- [ ] Save sample response.
- [ ] Confirm field names:
  - [ ] id.
  - [ ] name.
  - [ ] address.
  - [ ] email.
  - [ ] phoneNumber.
  - [ ] city.
  - [ ] gender.
- [ ] Test POST `/user` with official DTO.
- [ ] Confirm response includes id.
- [ ] Test POST with gender 0.
- [ ] Test POST with gender 1.
- [ ] Test POST with optional `photoUri`.
- [ ] Decide whether frontend sends `photoUri` remotely or local only.
- [ ] Commit: `test: validate mockapi user endpoints`.

### Phase 3 - City API Validation

- [ ] Test GET `/city` manually.
- [ ] Confirm response format.
- [ ] Update city DTO assumption if needed.
- [ ] Define city name extraction rule.
- [ ] Define fallback to unique cities from user list.
- [ ] Commit: `docs: validate mockapi city contract`.

### Phase 4 - Error Contract

- [ ] Define UI message for offline.
- [ ] Define UI message for timeout.
- [ ] Define UI message for 4xx.
- [ ] Define UI message for 5xx.
- [ ] Define UI message for parse error.
- [ ] Define retry behavior.
- [ ] Define cache-first behavior when error occurs.
- [ ] Commit: `docs: add api error handling contract`.

### Phase 5 - Offline Sync Contract

- [ ] Define local pending user shape.
- [ ] Define sync status values.
- [ ] Define WorkManager trigger condition.
- [ ] Define POST retry strategy.
- [ ] Define remote id update after success.
- [ ] Define duplicate limitation.
- [ ] Define activity log entries for sync.
- [ ] Commit: `docs: add offline sync contract`.

### Phase 6 - Optional MockWebServer Test Support

- [ ] Create sample JSON for users success.
- [ ] Create sample JSON for empty users.
- [ ] Create sample JSON for cities success.
- [ ] Create sample JSON for create user success.
- [ ] Create MockWebServer dispatcher.
- [ ] Use it in repository tests.
- [ ] Commit: `test: add mockwebserver api fixtures`.

### Phase 7 - OpenAPI-Lite Documentation Optional

- [ ] Create lightweight endpoint table.
- [ ] Add request and response examples.
- [ ] Add known limitations.
- [ ] Add photo limitation note.
- [ ] Add city fallback note.
- [ ] Commit: `docs: add api reference notes`.

### Phase 8 - Backend Readiness Review

- [ ] All endpoints tested.
- [ ] DTO field names confirmed.
- [ ] Gender mapping confirmed.
- [ ] City response format confirmed.
- [ ] Photo strategy decided.
- [ ] Error handling strategy clear.
- [ ] Offline sync contract clear.
- [ ] Frontend can implement without guessing.
- [ ] Commit: `docs: finalize backend readiness checklist`.

---

## 12. Backend Definition of Done

Backend documentation is done when:

- [ ] MockAPI endpoint is confirmed accessible.
- [ ] GET user contract is confirmed.
- [ ] POST user contract is confirmed.
- [ ] GET city response format is known or fallback defined.
- [ ] Gender mapping `0 = Male`, `1 = Female` is documented.
- [ ] Photo handling limitation is documented.
- [ ] Offline sync behavior is documented.
- [ ] Error handling contract is documented.
- [ ] Frontend task can continue without needing a custom backend.
