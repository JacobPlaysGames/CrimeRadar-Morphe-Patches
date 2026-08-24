package app.template.patches.crimeradar

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

// ── Premium Bypass ──────────────────────────────────────────────────────────

/**
 * Fingerprint for PremiumEntitlementHelper.isPremiumActive().
 *
 * Entitlement layer choke point: ads, replay/audio limits, saved location count.
 */
object PremiumActiveFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/PremiumEntitlementHelper;",
    name = "isPremiumActive",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

/**
 * Fingerprint for SubscriptionAccountHelper.shouldSuppressPremiumPromotions().
 *
 * Promotion/paywall layer choke point: all premium banners, cards, upgrade popups.
 */
object SuppressPremiumPromotionsFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/SubscriptionAccountHelper;",
    name = "shouldSuppressPremiumPromotions",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList()
)

/**
 * Fingerprint for RadarMapSubscriptionGateway.isActiveNow().
 *
 * Gates the map "follow more locations" limit popup.
 */
object IsActiveNowFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/RadarMapSubscriptionGateway;",
    name = "isActiveNow",
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    parameters = emptyList()
)

// ── Replay Minutes ──────────────────────────────────────────────────────────

/**
 * Fingerprint for Uh.b.Y() — daily free replay minutes.
 *
 * Returns 5 (free) or 20 (premium). Patched to return 999999.
 */
object ReplayDailyMinutesFingerprint : Fingerprint(
    definingClass = "LUh/b;",
    name = "Y",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "I",
    parameters = emptyList()
)

// ── Telemetry Kill ──────────────────────────────────────────────────────────

/**
 * Fingerprint for Ad.E.b() — Instabug initialization.
 *
 * Initializes Instabug SDK (bug reports, screenshots, session replay, APM).
 * Patched to no-op (return-void at index 0).
 */
object InstabugInitFingerprint : Fingerprint(
    definingClass = "LAd/E;",
    name = "b",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = emptyList()
)

/**
 * Fingerprint for Ad.C2071e.f(Application) — Adjust SDK initialization.
 *
 * Initializes Adjust for attribution/install tracking (reads GAID, IMEI, OAID).
 * Patched to no-op (return-void at index 0).
 */
object AdjustInitFingerprint : Fingerprint(
    definingClass = "Lad/e;",
    name = "f",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Landroid/app/Application;")
)

// ── History Cap ─────────────────────────────────────────────────────────────

/**
 * Fingerprint for cd.d.createQuery() — Room migration SQL queries.
 *
 * Case 3 returns "DELETE from history_docs where _id < (SELECT ... OFFSET 200)".
 * Patched: when this.a == 3, return "SELECT 1" (no-op) to remove the 200-item cap.
 */
object HistoryCapFingerprint : Fingerprint(
    definingClass = "Lcd/d;",
    name = "createQuery",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Ljava/lang/String;",
    parameters = emptyList()
)

// ── Notification Limits ─────────────────────────────────────────────────────

/**
 * Fingerprint for NotificationFrequencyManager.isUnderFreqLimit().
 *
 * Per-category daily push cap (default 25-50). Returns true when over limit.
 * Patched to always return false (never block pushes).
 */
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

/**
 * Fingerprint for HeadsUpPushMgr.shouldShowHeadsUpPush().
 *
 * Heads-up push daily limit (default 1). Patched to always return true.
 */
object HeadsUpPushFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/push/headsup/HeadsUpPushMgr;",
    name = "shouldShowHeadsUpPush",
    accessFlags = listOf(AccessFlags.PRIVATE, AccessFlags.FINAL),
    returnType = "Z",
    parameters = listOf("Lcom/particlemedia/data/PushData;")
)
