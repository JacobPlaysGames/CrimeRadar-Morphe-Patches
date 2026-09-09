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

// ── Pairip DRM Bypass ──────────────────────────────────────────────────────

/**
 * Entry point 1: CoreComponentFactory.<clinit>() calls StartupLauncher.launch()
 * which triggers VMRunner → System.loadLibrary("pairipcore") → native SIGSEGV.
 * No-op launch() to prevent the chain from starting.
 */
object StartupLauncherFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/StartupLauncher;",
    name = "launch",
    returnType = "V",
    parameters = emptyList()
)

/**
 * Entry point 2: com.pairip.application.Application.attachBaseContext() calls:
 *   VMRunner.setContext(context)       → loads native lib → CRASH
 *   SignatureCheck.verifyIntegrity()   → throws on re-sign
 *   LicenseClient.checkLicense()       → Play Store license check
 * Replace entire body with just super.attachBaseContext(context).
 */
object PairipApplicationFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/application/Application;",
    name = "attachBaseContext",
    returnType = "V",
    parameters = listOf("Landroid/content/Context;")
)

/**
 * Entry point 3: MyApplication.onCreate() has pairip-injected IronSource reflection:
 *   TTvRdCYPAWUKRE.ztV.invoke(null, this)
 * ztV is null because VMRunner never initialized → NPE crash.
 * No-op the entire method — app init happens via attachBaseContext + idle handlers.
 */
object MyApplicationOnCreateFingerprint : Fingerprint(
    definingClass = "Lcom/scannerradio/MyApplication;",
    name = "onCreate",
    returnType = "V",
    parameters = emptyList()
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
