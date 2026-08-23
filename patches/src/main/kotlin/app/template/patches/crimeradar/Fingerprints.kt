package app.template.patches.crimeradar

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Fingerprint for PremiumEntitlementHelper.isPremiumActive().
 *
 * This is the entitlement layer choke point:
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

/**
 * Fingerprint for SubscriptionAccountHelper.shouldSuppressPremiumPromotions().
 *
 * This is the promotion/paywall layer choke point. It reads SubscriptionManager.currentState()
 * directly (bypassing PremiumEntitlementHelper) and returns true if the user is premium.
 * All premium UI flows check this:
 * - shouldShowPremiumEntry() = isPremiumFeatureEnabled() && !shouldSuppressPremiumPromotions()
 * - shouldShowDetailPremiumEntry() = same pattern
 * - RadarMapSubscriptionGateway.hasPaidEver() delegates to this method
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
 * Reads SubscriptionManager.currentState().isActive() directly.
 */
object IsActiveNowFingerprint : Fingerprint(
    definingClass = "Lcom/particlemedia/feature/subscription/RadarMapSubscriptionGateway;",
    name = "isActiveNow",
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    parameters = emptyList()
)
