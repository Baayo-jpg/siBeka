# Aset Gambar siBeka (halaman Peta)

Semua gambar Peta dimuat dari `app/src/main/assets/`. Tidak ada aset yang
di-hardcode di kode: komponen mencari file berdasarkan nama, dan bila tidak ada
otomatis memakai cadangan (emoji atau latar Canvas), sehingga aplikasi tidak
pernah gagal hanya karena gambar belum tersedia.

## Struktur folder

```
app/src/main/assets/
├── ikon/     maskot, lencana simpul (ikon level & ikon last)
└── latar/    background tiap habitat
```

Ekstensi yang dicari berurutan: **webp → png → svg**. Untuk nama yang sama, file
`webp` yang dipakai lebih dulu.

## 1. `assets/ikon/` — lencana simpul

| File | Ukuran | Dipakai di |
|---|---|---|
| `ikon_level_hutan.webp` | 200 × 200 | 3 simpul kuis habitat Hutan |
| `ikon_level_laut.webp` | 200 × 200 | 3 simpul kuis habitat Laut |
| `ikon_level_sungai.webp` | 200 × 200 | 3 simpul kuis habitat Sungai (menggunakan aset Laut) |
| `ikon_level_rawa.webp` | 200 × 200 | 3 simpul kuis habitat Rawa |
| `ikon_level_gunung.webp` | 200 × 200 | 3 simpul kuis habitat Gunung |
| `ikon_last_hutan.webp` | 204 × 204 | simpul terakhir (level akhir) habitat Hutan |
| `ikon_last_laut.webp` | 204 × 204 | simpul terakhir habitat Laut |
| `ikon_last_sungai.webp` | 204 × 204 | simpul terakhir habitat Sungai (menggunakan aset Laut) |
| `ikon_last_rawa.webp` | 204 × 204 | simpul terakhir habitat Rawa |
| `ikon_last_gunung.webp` | 204 × 204 | simpul terakhir habitat Gunung |

Lencana ini berbentuk bulat penuh dengan latar transparan di luar lingkaran, jadi
dipakai sebagai **muka simpul** (mengisi seluruh lingkaran 74 dp), bukan ikon
kecil di tengah lingkaran warna. Saat habitat tuntas, ikon tetap tampil dan
status selesai ditandai lencana centang kecil di sudut simpul.

### Aturan penamaan (prioritas)

1. `ikon_level_<habitat>` / `ikon_last_<habitat>`
2. `mode_<mode>_<habitat>` (mis. `mode_pasangkan_rawa`)
3. `mode_<mode>` / `simpul_hadiah` / `simpul_trofi`
4. emoji

Alias nama habitat: `hutan_hujan_tropis`, `hutanhujantropis`, `hutan`, `hh`;
`laut`, `lt`; `sungai`, `sg`; `rawa`, `rw`; `gunung`, `gn`.

Nama lain yang masih dicari bila ada: `maskot` (header Peta),
`habitat_hh/lt/sg/rw/gn` (ikon banner habitat).

## 2. `assets/latar/` — background habitat

| File | Ukuran | Dipakai di |
|---|---|---|
| `background_hutanhujantropis.webp` | 704 × 1520 | bagian jalur Hutan |
| `background_laut.webp` | 704 × 1520 | bagian jalur Laut |
| `background_sungai.webp` | 704 × 1520 | bagian jalur Sungai |
| `background_rawa.webp` | 704 × 1520 | bagian jalur Rawa |
| `background_gunung.webp` | 704 × 1520 | bagian jalur Gunung |

Nama `latar_*` juga diterima sebagai alias (`latar_hh`, `latar_lt`, dst.).

Perilaku tampilan:

- tiap bagian habitat setinggi **± 86% layar**; jarak antar simpul direnggangkan
  mengisi tinggi itu sehingga latar portrait tampil hampir utuh;
- gambar memenuhi kartu habitat (sudut membulat 24 dp, margin 16 dp) dengan
  skala `Crop`, jadi tepi atas/bawah terpotong sedikit mengikuti rasio layar;
- di atas gambar ada gradien putih tipis (16% → 2% → 30%) agar garis jalur dan
  simpul tetap terbaca;
- ± 100 dp teratas tertutup banner habitat yang menempel saat digulir.

## 3. Asal dan pemrosesan aset

File asli dari Canva berupa SVG yang isinya sebenarnya **gambar raster base64**
(PNG 704 × 1520 untuk latar, PNG 246 × 204 untuk lencana), ditambah metadata C2PA
± 22,9 KB per file. Untuk aplikasi, raster tersebut diekstrak lalu disimpan
sebagai WebP:

- latar: WebP kualitas 90, tanpa alpha → 15–43 KB per file;
- lencana: WebP kualitas 95 dengan alpha dari masker SVG, dipotong ke kotak pas
  → 6–11 KB per file.

Hasilnya total ± 165 KB, dari 5,4 MB berkas SVG asli. File SVG aslinya tetap ada
di `C:\Users\HP\Downloads\ikon\` (tidak diubah).

## 4. Setelah menambah atau mengganti aset

```
./gradlew :app:assembleDebug --offline
```

Aset dibaca saat aplikasi dibangun, jadi build ulang diperlukan. File yang belum
tersedia otomatis kembali ke emoji/latar Canvas — tidak pernah error.
