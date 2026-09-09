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
 *    Fix: Initialize R8 string dedup fields (sjJeZY) to prevent downstream NPEs,
 *    then call parent onCreate() which runs hiltInternalInject() + super.onCreate().
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
        // Replace with:
        //   a) Initialize R8 string dedup fields (sjJeZY) to prevent NPE in downstream DI.
        //      Pairip's VMRunner was responsible for triggering these field inits. Without it,
        //      sjJeZY.uBi (and siblings) remain null → rd6(ViewedClipsTracker) constructor NPEs
        //      on StringBuilder(null) when accessing them.
        //   b) Call parent (com.scannerradio.a) which runs hiltInternalInject() + super.onCreate().
        //      This is critical — hiltInternalInject() sets up logger, config, workerFactory,
        //      clips, headlines, and all Hilt DI fields.
        MyApplicationOnCreateFingerprint.method.addInstructions(
            0,
            """
                # Initialize R8 string dedup class to prevent null NPEs
                const-string v0, ""
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->AKHhGmPEdtQeTh:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->AlwMp:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->CPgBRDrXIqAdZa:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->FNwZMZLO:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->FjrMQcZ:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->HKrJGMIUEGLGf:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->IYNdR:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->IrWH:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->JDoVaE:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->KEqWVsjtb:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->KsPpkWTv:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->LTIYyhOrnsQ:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->LojOdhQ:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->NHKlIHfaSdylhA:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->NLKMLP:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->PTrTnrxnsS:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->RXoFSgjdW:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->RfjxMKY:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->WsOHIUBQZ:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->ZARsTCUslH:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->dWxRQoreV:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->eOVC:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->fItAOysWM:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->iElRfEOJYu:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->iQIhnhPBYvUJvP:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->jnEmkCGKrq:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->kPYXOUchOjWsALL:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->kkkqjnLTmssnr:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->mqXbvqRXUx:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->olrmYCpjEh:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->otQnrjj:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->sfbnGQrbFMbR:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->tWYrrCprWpZN:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->tvtZTSCaOcbigE:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->uBi:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->wZuvGwePN:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->wjxgBh:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->xcKUSBkAGf:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->yIVln:Ljava/lang/String;
                sput-object v0, Landroidx/media3/common/util/yx/sjJeZY;->ylxvNVigUXZjc:Ljava/lang/String;
                # Now call parent onCreate() for Hilt DI setup
                invoke-super {p0}, Lcom/scannerradio/a;->onCreate()V
                return-void
            """
        )
    }
}
