package app.template.patches.scannerradio

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_SCANNERRADIO

/**
 * Disables Pairip DRM (Google Play Integrity) that crashes patched APKs.
 *
 * Two entry points trigger libpairipcore.so loading → native SIGSEGV:
 *
 * 1. CoreComponentFactory.<clinit>() → StartupLauncher.launch() → VMRunner.invoke()
 *    → System.loadLibrary("pairipcore") → SIGSEGV
 *    Fix: No-op launch() before it calls VMRunner.invoke().
 *
 * 2. com.pairip.application.Application.attachBaseContext():
 *    - VMRunner.setContext()        → triggers class load → loads native lib → CRASH
 *    - SignatureCheck.verifyIntegrity() → throws SignatureTamperedException on re-sign
 *    - LicenseClient.checkLicense()    → Play Store license verification
 *    Fix: Replace entire method body with just super.attachBaseContext(context).
 *
 * 3. MyApplication.onCreate(): pairip injected IronSource reflection
 *    TTvRdCYPAWUKRE.ztV.invoke(null, this) — ztV is null → NPE crash.
 *    Fix: No-op the method; app init runs via attachBaseContext + idle handlers.
 */
@Suppress("unused")
val pairipBypassPatch = bytecodePatch(
    name = "Pairip DRM Bypass",
    description = "Disables Pairip DRM/integrity checks that crash patched APKs on startup.",
    default = true
) {
    compatibleWith(COMPATIBILITY_SCANNERRADIO)

    execute {
        // Fix 1: No-op StartupLauncher.launch() to prevent CoreComponentFactory chain
        StartupLauncherFingerprint.method.addInstructions(
            0,
            "return-void"
        )

        // Fix 2: Replace Application.attachBaseContext() to skip all Pairip checks.
        // Original body: VMRunner.setContext(), SignatureCheck.verifyIntegrity(),
        // LicenseClient.checkLicense(), super.attachBaseContext().
        // We just call super and return — no Pairip code executes.
        PairipApplicationFingerprint.method.addInstructions(
            0,
            """
                invoke-super {p0, p1}, Lcom/scannerradio/MyApplication;->attachBaseContext(Landroid/content/Context;)V
                return-void
            """
        )

        // Fix 3: Replace MyApplication.onCreate() body.
        // Original body: TTvRdCYPAWUKRE.ztV.invoke(null, this) → pairip IronSource reflection, NPE.
        // Replace with call to parent (com.scannerradio.a) which runs hiltInternalInject()
        // and super.onCreate(). This is critical — hiltInternalInject() sets up logger,
        // config, workerFactory, clips, headlines, and all Hilt DI fields.
        MyApplicationOnCreateFingerprint.method.addInstructions(
            0,
            """
                invoke-super {p0}, Lcom/scannerradio/a;->onCreate()V
                return-void
            """
        )
    }
}
