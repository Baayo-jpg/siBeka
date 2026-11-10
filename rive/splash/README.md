# Splash melambai (Rive)

Sumber animasi layar pembuka siBeka.

## Isi

- `scene.rml` — artboard 741x765 berisi dua gambar: `assets/maskot_badan.png`
  (maskot tanpa tangan) dan `assets/maskot_tangan.png` (tangan yang dipotong di
  pergelangan). Tangan diputar -0.20..+0.20 rad lewat `rotation` (key 15) pada
  poros pergelangan (origin 0.80/0.87), ditambah goyangan badan 10 px. Animasi
  `Melambai`, 150 frame, loop.

## Status

- `rive . --verify` bersih (0 error, 0 warning).
- Previewer `rive .` membangun file dengan gambar ter-embed dan bisa dilihat
  langsung di jendela.
- **Ekspor `.riv` belum bisa dilakukan di mesin ini**: perintah `--publish`
  (satu-satunya yang menulis `.riv` ke disk) memerlukan `rive login`, dan
  capture headless (`--screenshot`) tidak menggambar aset gambar.

## Kalau ingin memakai Rive di aplikasi

```bash
rive login
rive . --publish          # menulis build/splash.riv
```

Lalu salin `build/splash.riv` ke `app/src/main/res/raw/`, tambahkan
`implementation("app.rive:rive-android:11.14.0")`, dan pasang `RiveAnimation`
di `ui/splash/SplashScreen.kt`.

Saat ini aplikasi memakai versi Compose dari rig yang sama
(`ui/splash/SplashScreen.kt` + `assets/maskot/`), jadi tidak menambah dependensi.
