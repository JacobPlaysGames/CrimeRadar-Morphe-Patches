package app.template.patches.crimeradar

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_CRIMERADAR

/**
 * Increases the notification alert radius sent to the server.
 *
 * ManagementAlertsAdapter.buildNewSetAlertSettings() passes newRadius (Integer, p2)
 * directly to setNotifyRadius(). The slider options are {null, 10, 5, 3, 1} miles.
 *
 * Patch: when newRadius != null, multiply by 5 before passing to protobuf builder.
 *   10 mi → 50 mi, 5 → 25, 3 → 15, 1 → 5
 *
 * Caveat: the server may reject values >10. If so, the server clamps to its max.
 */
@Suppress("unused")
val notificationRangePatch = bytecodePatch(
    name = "Notification Range Extended",
    description = "Multiplies alert radius by 5x (10mi→50mi). Server may reject values >10.",
    default = false
) {
    compatibleWith(COMPATIBILITY_CRIMERADAR)

    execute {
        // p0=this, p1=alertSettings, p2=newRadius(Integer), p3=smart, p4=intensity, p5=noDisturbNight
        // If newRadius != null, unbox, multiply by 5, re-box
        NotificationRangeFingerprint.method.addInstructions(
            0,
            """
                # if (newRadius == null) goto :skip
                if-eqz p2, :skip
                
                # newRadius = Integer.valueOf(newRadius.intValue() * 5)
                invoke-virtual {p2}, Ljava/lang/Integer;->intValue()I
                move-result v0
                const/4 v1, 0x5
                mul-int/2addr v0, v1
                invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
                move-result-object p2
                
                :skip
            """
        )
    }
}
