package app.template.patches.crimeradar

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_CRIMERADAR

/**
 * Overrides the daily free replay listening minutes.
 *
 * Uh.b.Y() returns 5 (free) or 20 (premium).
 * Uh.b.Z() returns 15 (reward extra minutes) or 0 (premium).
 *
 * Both patched to return 999999 — effectively unlimited replay time.
 */
@Suppress("unused")
val replayMinutesPatch = bytecodePatch(
    name = "Replay Minutes Override",
    description = "Overrides daily replay listening time limits to unlimited.",
    default = true
) {
    compatibleWith(COMPATIBILITY_CRIMERADAR)

    execute {
        // Daily free minutes: 5 → 999999
        ReplayDailyMinutesFingerprint.method.addInstructions(
            0,
            """
                const v0, 0xF423F
                return v0
            """
        )

        // Reward extra minutes: 15 → 999999
        ReplayExtraMinutesFingerprint.method.addInstructions(
            0,
            """
                const v0, 0xF423F
                return v0
            """
        )
    }
}
