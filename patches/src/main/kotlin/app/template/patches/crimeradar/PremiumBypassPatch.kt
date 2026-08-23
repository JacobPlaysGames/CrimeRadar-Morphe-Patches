package app.template.patches.crimeradar

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_CRIMERADAR

/**
 * Bypasses premium subscription checks by making PremiumEntitlementHelper.isPremiumActive()
 * always return true.
 *
 * This unlocks:
 * - Ad-free experience
 * - Unlimited replay playback
 * - Unlimited audio playback
 * - 10 saved locations (instead of 1)
 */
@Suppress("unused")
val premiumBypassPatch = bytecodePatch(
    name = "Premium Bypass",
    description = "Bypasses premium subscription checks to unlock all premium features.",
    default = true
) {
    compatibleWith(COMPATIBILITY_CRIMERADAR)

    execute {
        PremiumActiveFingerprint.method.addInstructions(
            0,
            """
                # const/4 v0, 0x1 = true
                const/4 v0, 0x1
                return v0
            """
        )
    }
}
