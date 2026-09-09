package app.template.patches.scannerradio

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

// ── Premium Bypass ──────────────────────────────────────────────────────────

object IsProVersionFingerprint : Fingerprint(
    definingClass = "Ldefpackage/hm0;",
    name = "a0",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

// ── Telemetry Kill ──────────────────────────────────────────────────────────

object InMobiInitFingerprint : Fingerprint(
    definingClass = "Lcom/inmobi/sdk/InMobiSdk;",
    name = "init",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf(
        "Landroid/content/Context;",
        "Ljava/lang/String;",
        "Lorg/json/JSONObject;",
        "Lcom/inmobi/sdk/InMobiSdk\$SdkInitializationListener;"
    )
)

object FairBidInitFingerprint : Fingerprint(
    definingClass = "Lcom/fyber/a;",
    name = "start",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "Landroid/content/Context;")
)

// ── Ad Kill ─────────────────────────────────────────────────────────────────

object ShowBannerAdsFingerprint : Fingerprint(
    definingClass = "Ldefpackage/j8;",
    name = "i",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList()
)

object ShowInterstitialAdsFingerprint : Fingerprint(
    definingClass = "Ldefpackage/j8;",
    name = "j",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList()
)
