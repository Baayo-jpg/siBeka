# Aset Gambar siBeka

Semua gambar Peta dimuat dari `app/src/main/assets/`. Tidak ada aset yang
di-hardcode di kode: komponen mencari file berdasarkan nama, dan bila tidak ada
otomatis memakai cadangan (emoji atau latar Canvas), sehingga aplikasi tidak
pernah gagal hanya karena gambar belum tersedia.

## Struktur folder

```
app/src/main/assets/
├── ikon/     maskot, lencana simpul (ikon level & ikon last)
├── latar/    background tiap habitat
├── toko/     gambar barang aksesori Toko
└── maskot/   maskot layar pembuka (badan + tangan) dan maskot memakai aksesori
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

## 3. `assets/toko/` dan `assets/maskot/` — aksesori Toko

Empat aksesori, masing-masing punya gambar barang dan gambar maskot yang
memakainya:

| Aksesori (id) | Gambar barang | Maskot memakai | Ukuran |
|---|---|---|---|
| Topi Rimba (`topi_penjelajah`) | `toko/aksesori_topi.webp` | `maskot/pakai_topi.webp` | 358 × 320 / 481 × 640 |
| Kacamata Hitam (`kacamata_keren`) | `toko/aksesori_kacamata.webp` | `maskot/pakai_kacamata.webp` | 447 × 227 / 454 × 640 |
| Mahkota Bunga (`mahkota_bunga`) | `toko/aksesori_mahkota.webp` | `maskot/pakai_mahkota.webp` | 421 × 239 / 534 × 640 |
| Helm Gunung (`helm_gunung`) | `toko/aksesori_helm.webp` | `maskot/pakai_helm.webp` | 296 × 320 / 487 × 640 |

Asal: 8 file SVG (hasil trace) plus PNG 733–1097 px di
`C:\Users\HP\Downloads\maskot\`. Latar putihnya dibuang dengan isian banjir dari
keempat sudut, tepi dikikis 1 px lalu dilembutkan 0,7 px agar tidak ada garis
putih, lalu dipotong ke isi dan disimpan sebagai WebP kualitas 90 dengan alpha
(total ± 237 KB).

`maskot/pakai_topi.webp` masih memuat rumput hijau di bawah kaki karena bagian
dari ilustrasi aslinya.

Dipakai oleh `ui/toko/TokoScreen.kt`: gambar barang tampil di kartu aksesori,
gambar maskot tampil di kartu "Gayaku hari ini!" sesuai aksesori yang dipakai.
Item `default` memakai `MaskotSiBeka` seperti sebelumnya.

## 4. Busur SVG yang dibersihkan

SVG flora/fauna/pasangan berasal dari hasil trace (VTracer). Trace itu sering
mengganti potongan yang nyaris lurus dengan **busur beradius raksasa** — ada yang
sampai 361 juta, padahal kanvasnya hanya 500 px. Pustaka SVG di Android
(androidsvg, dipakai `coil-svg`) menghitung titik pusat busur seperti itu lalu
melempar `ArrayIndexOutOfBoundsException`, sehingga gambarnya gagal dimuat dan
kartu menampilkan ikon pengganti. Kasus yang terlihat: `Babi Hutan`.

Perbaikannya: setiap busur dengan radius > 10.000 diganti perintah garis ke titik
akhir yang sama (`A/a` → `L/l`). Karena radiusnya jauh lebih besar daripada jarak
titiknya, lengkungan yang hilang maksimal 0,005 px — tidak terlihat. Hasilnya:
**2.831 busur di 137 berkas**, dan render sebelum/sesudah dibandingkan per piksel
(selisih maksimum 1 dari 255 di satu berkas, sisanya 0).

Kalau nanti menambah SVG hasil trace baru, jalankan pemeriksaan yang sama:
busur dengan radius ribuan kali ukuran kanvas adalah tanda perlu dibersihkan.

## 5. Asal dan pemrosesan aset

File asli dari Canva berupa SVG yang isinya sebenarnya **gambar raster base64**
(PNG 704 × 1520 untuk latar, PNG 246 × 204 untuk lencana), ditambah metadata C2PA
± 22,9 KB per file. Untuk aplikasi, raster tersebut diekstrak lalu disimpan
sebagai WebP:

- latar: WebP kualitas 90, tanpa alpha → 15–43 KB per file;
- lencana: WebP kualitas 95 dengan alpha dari masker SVG, dipotong ke kotak pas
  → 6–11 KB per file.

Hasilnya total ± 165 KB, dari 5,4 MB berkas SVG asli. File SVG aslinya tetap ada
di `C:\Users\HP\Downloads\ikon\` (tidak diubah).

## 6. Setelah menambah atau mengganti aset

```
./gradlew :app:assembleDebug --offline
```

Aset dibaca saat aplikasi dibangun, jadi build ulang diperlukan. File yang belum
tersedia otomatis kembali ke emoji/latar Canvas — tidak pernah error.
