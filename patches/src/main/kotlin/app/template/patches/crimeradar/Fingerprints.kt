package app.template.patches.crimeradar

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Fingerprint for PremiumEntitlementHelper.isPremiumActive().
 *
 * This is the central choke point for all premium feature checks:
 * - isAdFreeEnabled() -> isPremiumActive()
 * - hasUnlimitedReplayPlayback() -> isPremiumActive()
 * - hasUnlimitedAudioPlayback() -> isPremiumActive()
 * - maxSavedLocationCount() -> isPremiumActive() ? 10 : 1
 *
 * Class names are unobfuscated in this app, so we can match on full class name.
 */
object PremiumActiveFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/PremiumEntitlementHelper;",
    name = "isPremiumActive",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)
