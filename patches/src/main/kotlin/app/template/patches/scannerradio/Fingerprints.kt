package app.template.patches.scannerradio

import app.morphe.patcher.Fingerprint

// ── Premium Bypass ──────────────────────────────────────────────────────────

object IsProVersionFingerprint : Fingerprint(
    definingClass = "Lhm0;",
    name = "a0",
    returnType = "Z",
    parameters = emptyList()
)

// ── Telemetry Kill ──────────────────────────────────────────────────────────

object InMobiInitFingerprint : Fingerprint(
    definingClass = "Lcom/inmobi/sdk/InMobiSdk;",
    name = "init",
    returnType = "V",
    parameters = listOf(
        "Landroid/content/Context;",
        "Ljava/lang/String;",
        "Lorg/json/JSONObject;",
        "Lcom/inmobi/sdk/SdkInitializationListener;"
    )
)

object FairBidInitFingerprint : Fingerprint(
    definingClass = "Lcom/fyber/a;",
    name = "start",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "Landroid/content/Context;")
)

// ── Ad Kill ─────────────────────────────────────────────────────────────────

object ShowBannerAdsFingerprint : Fingerprint(
    definingClass = "Lj8;",
    name = "i",
    returnType = "V",
    parameters = emptyList()
)

object ShowInterstitialAdsFingerprint : Fingerprint(
    definingClass = "Lj8;",
    name = "j",
    returnType = "V",
    parameters = emptyList()
)
