# 铁壳训练 (GymPulse)

[![License: MIT](https://img.shields.io/badge/License-MIT-1C1A18?style=flat-square&labelColor=070504&color=D33D2C)](LICENSE)
[![Platform: Android](https://img.shields.io/badge/Platform-Android-1C1A18?style=flat-square&labelColor=070504&color=4CAF6E)](https://developer.android.com)
[![Min SDK: 26](https://img.shields.io/badge/Min_SDK-26-1C1A18?style=flat-square&labelColor=070504&color=ECEBEA)](https://developer.android.com/about/versions/8.0)
[![Release: v0.0.1](https://img.shields.io/badge/Release-v0.0.1-1C1A18?style=flat-square&labelColor=070504&color=D9733A)](https://github.com/cwj214228/GymPulse/releases/tag/v0.0.1)

一款为健身爱好者设计的极简训练记录 Android App。暗色工业锻造风格界面、本地存储、零网络依赖。

## 📥 下载

**最新版本: [v0.0.1](https://github.com/cwj214228/GymPulse/releases/tag/v0.0.1)** · 1.5 MB · arm64

[⬇ 下载 APK (GymPulse-Android-v0.0.1-arm64.apk)](https://github.com/cwj214228/GymPulse/releases/download/v0.0.1/GymPulse-Android-v0.0.1-arm64.apk)

```bash
# 通过 ADB 安装
adb install GymPulse-Android-v0.0.1-arm64.apk
```

## 📱 截图

> 真实设备截屏 (Xiaomi 14, 1200×2670) — 严格依据 Open Design 项目「铁壳训练」的 `screens/*.html` 设计源实现。

| 首页 | 记录 | 统计 |
|:---:|:---:|:---:|
| ![Home](docs/screenshots/home.png) | ![Log](docs/screenshots/log.png) | ![Stats](docs/screenshots/stats.png) |

---

## ✨ 功能

### 🏋️ 首页
- 动态问候语（早上好 / 下午好 / 晚上好）
- 8 个训练部位多选芯片：胸部 / 背部 / 肩部 / 腿部 / 肱二头 / 肱三头 / 腹部 / 有氧
- 一键保存今日训练，绿色闪动反馈
- 显示最近 3 条训练记录

### 📋 记录
- 按月份动态筛选
- 总天数 / 总次数 / 最爱练 统计摘要
- 按日期分组的训练日志列表
- **长按**任意记录可删除

### 📊 统计
- 2×2 统计大数字：训练天数 / 总部位次 / 最爱练 / 每次平均
- 部位训练频次水平条形图
- 本月训练日历热力图

### ⚙️ 设置与数据持久化
- 所有数据存储在手机本地 Room 数据库
- 入口：首页右上角 ⚙ 按钮
- 训练数据 **JSON 导入/导出** — 防止卸载丢失

#### 导出训练记录
1. 主页 → 右上角 ⚙
2. 点击「↓ 导出训练记录」
3. 选择「保存到 Download 目录」(或分享到网盘/电脑)
4. 备份文件：`/sdcard/Download/gympulse_backup_yyyyMMdd_HHmmss.json`

#### 导入训练记录 (重装后恢复)
1. 主页 → 右上角 ⚙
2. 点击「↑ 导入训练记录」
3. 选择之前的 JSON 备份文件
4. Toast 提示「已导入 N 条记录」

#### 建议备份策略
- 每月导出一次
- 复制到网盘 (iCloud / 百度网盘 / OneDrive) 或电脑
- MIUI / ColorOS 等定制系统卸载 app 时会清空内部数据,但 Download 目录是用户可见存储,**不会**被清掉

---

## 🏗️ 技术栈

| 项目 | 版本 |
|------|------|
| 语言 | Kotlin 2.0 |
| UI | Jetpack Compose (Material 3) |
| 数据库 | Room 2.6 |
| 导航 | Navigation Compose |
| 构建 | Gradle 8.9 + AGP 8.5.2 |
| JDK | 17 / 21 |
| minSdk | 26 (Android 8.0) |
| targetSdk | 35 (Android 15) |

## 🏛️ 架构

MVVM + Repository + 单向数据流：

```
UI (Compose)
  ↕ StateFlow
ViewModel
  ↕ suspend / Flow
Repository
  ↕ DAO
Room Database
```

## 🎨 设计系统

- **配色** — 工业锻造方向（锻铁黑底 + 锈橙强调色）
- **色值** — OKLCH 色彩空间，sRGB 精确转换
- **字体** — 三族体系：Display / Body / Mono
- **组件** — Material 3 + 自定义 12px 圆角卡片、8px 芯片

## 📁 项目结构

```
GymPulse/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/
│       │   ├── values/colors.xml, themes.xml
│       │   ├── drawable/ic_launcher_*.xml
│       │   ├── mipmap-anydpi-v26/ic_launcher.xml
│       │   └── xml/file_paths.xml
│       └── java/com/gympulse/app/
│           ├── GymPulseApp.kt          # Application + 单例
│           ├── MainActivity.kt         # 入口 + 底部导航
│           ├── data/
│           │   ├── entity/TrainingLog.kt
│           │   ├── dao/TrainingLogDao.kt
│           │   ├── AppDatabase.kt
│           │   ├── TrainingRepository.kt
│           │   ├── DataManager.kt      # 导入/导出
│           │   └── PreferenceManager.kt
│           └── ui/
│               ├── theme/              # Color / Type / Theme
│               ├── common/             # 共享组件
│               ├── home/               # 首页
│               ├── log/                # 记录
│               ├── stats/              # 统计
│               ├── settings/           # 设置
│               ├── workout/            # 训练确认
│               └── navigation/NavGraph.kt
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/
├── gradlew
├── gympulse.keystore                  # Release 签名
└── docs/screenshots/                  # README 截图 (adb 真实设备截屏)
```

> 📐 **设计源文件**: `screens/01-home.html` / `02-log.html` / `03-workout.html` / `04-stats.html` 位于 Open Design 项目「铁壳训练」中，使用 OKLCH 色彩空间，Android 端经 Canvas API 精确转换为 sRGB 实现像素级还原。

## 🚀 构建

### Debug
```bash
./gradlew assembleDebug
```
输出：`app/build/outputs/apk/debug/app-debug.apk`

### Release
```bash
./gradlew assembleRelease
```
输出：`app/build/outputs/apk/release/app-release.apk`（已 R8 压缩 + 签名）

## 📲 安装

```bash
# 1. 下载或构建 APK
# 2. 通过 ADB 安装 (Bypass MIUI 弹窗)
adb push GymPulse-Android-v0.0.1-arm64.apk /data/local/tmp/
adb shell pm install -r /data/local/tmp/app-release.apk
adb shell rm /data/local/tmp/app-release.apk
```

---

## 📄 License

[MIT License](LICENSE) © 2026 cwj214228
