package app.template.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.SupportedAbi

object Constants {
    val COMPATIBILITY_CRIMERADAR = Compatibility(
        name = "CrimeRadar",
        packageName = "com.newsbreak.crimeradar",
        apkFileType = ApkFileType.APK,
        appIconColor = 0xB71C1C, // Dark red, matching CrimeRadar icon background
        targets = listOf(
            AppTarget(
                version = "26.33.1"
            )
        )
    )

    val COMPATIBILITY_EXAMPLE = Compatibility(
        name = "XYZ app",
        packageName = "com.example.app",
        apkFileType = ApkFileType.APK,
        appIconColor = 0xFF0045,
        targets = listOf(
            AppTarget(
                version = "2.0.0"
            ),
            AppTarget(
                version = "1.0.2"
            )
        )
    )
}
