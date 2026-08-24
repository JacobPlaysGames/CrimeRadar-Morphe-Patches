package app.template.patches.crimeradar

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

// ── Premium Bypass ──────────────────────────────────────────────────────────

object PremiumActiveFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/PremiumEntitlementHelper;",
    name = "isPremiumActive",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

object SuppressPremiumPromotionsFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/SubscriptionAccountHelper;",
    name = "shouldSuppressPremiumPromotions",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

object IsActiveNowFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/RadarMapSubscriptionGateway;",
    name = "isActiveNow",
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    parameters = emptyList()
)

// ── Replay Minutes ──────────────────────────────────────────────────────────

object ReplayDailyMinutesFingerprint : Fingerprint(
    definingClass = "LUh/b;",
    name = "Y",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "I",
    parameters = emptyList()
)

// ── Telemetry Kill ──────────────────────────────────────────────────────────

object InstabugInitFingerprint : Fingerprint(
    definingClass = "LAd/E;",
    name = "b",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = emptyList()
)

object AdjustInitFingerprint : Fingerprint(
    definingClass = "Lad/e;",
    name = "f",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Landroid/app/Application;")
)

// ── History Cap ─────────────────────────────────────────────────────────────

object HistoryCapFingerprint : Fingerprint(
    definingClass = "Lcd/d;",
    name = "createQuery",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/String;",
    parameters = emptyList()
)

// ── Notification Limits ─────────────────────────────────────────────────────

object NotificationFrequencyFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/push/frequency/NotificationFrequencyManager;",
    name = "isUnderFreqLimit",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = listOf(
        "Lcom/particlemedia/data/PushData;",
        "Ljava/lang/String;"
    )
)

object HeadsUpPushFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/push/headsup/HeadsUpPushMgr;",
    name = "shouldShowHeadsUpPush",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.FINAL),
    returnType = "Z",
    parameters = listOf("Lcom/particlemedia/data/PushData;")
)

// ── Notification Range ──────────────────────────────────────────────────────

/**
 * Fingerprint for ManagementAlertsAdapter.buildNewSetAlertSettings().
 *
 * Builds the protobuf AlertSettings sent to server. The newRadius parameter
 * (p2, Integer) comes from sliderOptions = {null, 10, 5, 3, 1} miles.
 * Patched to multiply by 5: 10→50, 5→25, 3→15, 1→5 miles.
 */
object NotificationRangeFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/map/ManagementAlertsAdapter;",
    name = "buildNewSetAlertSettings",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Lcom/api/schema/crimeradar/v1/Crimeradar\$AlertSettings;",
    parameters = listOf(
        "Lcom/api/schema/crimeradar/v1/Crimeradar\$AlertSettings;",
        "Ljava/lang/Integer;",
        "Ljava/lang/Boolean;",
        "Ljava/lang/String;",
        "Ljava/lang/Boolean;"
    )
)
