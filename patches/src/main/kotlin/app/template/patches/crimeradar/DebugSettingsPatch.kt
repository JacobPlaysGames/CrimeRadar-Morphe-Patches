package app.template.patches.crimeradar

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_CRIMERADAR

/**
 * Adds a "CrimeRadar+ Patches" entry to the Settings screen that shows a
 * debug AlertDialog with runtime info:
 *   - App version & package name
 *   - Live premium status (isPremiumActive)
 *   - List of all active patches
 *
 * Implementation:
 *   1. Injects into SettingAdapter.initSettingItems() to append a new SettingItem
 *      using the unused SettingId.Favorite enum value.
 *   2. Injects into SettingAdapter.onClick() to intercept clicks on that entry
 *      and show an AlertDialog instead of falling through to the original switch.
 */
@Suppress("unused")
val debugSettingsPatch = bytecodePatch(
    name = "Debug Settings",
    description = "Adds a CrimeRadar+ debug info panel to the settings screen.",
    default = true
) {
    compatibleWith(COMPATIBILITY_CRIMERADAR)

    execute {
        // ── 1. Append "CrimeRadar+ Patches" entry at end of settings list ──
        // initSettingItems() clears items then adds them; we append AFTER everything.
        // Register budget: v0-v6 only (7 locals) — conservative for small .locals counts.
        val initMethod = SettingInitItemsFingerprint.method
        val initCount = initMethod.implementation?.instructions?.size
            ?: error("initSettingItems has no implementation")

        initMethod.addInstructions(
            initCount,
            """
                # new SettingItem(Favorite, Option_New, 0, 0, 0, null)
                new-instance             v0, Lcom/particlemedia/feature/settings/SettingItem;
                sget-object              v1, Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingId;->Favorite:Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingId;
                sget-object              v2, Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingType;->Option_New:Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingType;
                const/4                  v3, 0x0
                const/4                  v4, 0x0
                const/4                  v5, 0x0
                const/4                  v6, 0x0
                invoke-direct/range      {v0 .. v6}, Lcom/particlemedia/feature/settings/SettingItem;-><init>(Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingId;Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingType;IIILjava/lang/String;)V

                # item.nameString = "CrimeRadar+ Patches"  (reuse v1, v7 not needed)
                const-string             v1, "CrimeRadar+ Patches"
                iput-object              v1, v0, Lcom/particlemedia/feature/settings/SettingItem;->nameString:Ljava/lang/String;

                # item.descStr = "Tap for debug info"
                const-string             v1, "Tap for debug info"
                iput-object              v1, v0, Lcom/particlemedia/feature/settings/SettingItem;->descStr:Ljava/lang/String;

                # this.items.add(item)
                iget-object              v1, p0, Lcom/particlemedia/feature/settings/SettingAdapter;->items:Ljava/util/ArrayList;
                invoke-virtual           {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
            """
        )

        // ── 2. Intercept onClick → show debug AlertDialog ──────────────────
        // NOTE: SettingItem.id field is the REAL DEX name. jadx renames it to
        // "f38808id" due to root-package collision — do NOT use that name.
        SettingOnClickFingerprint.method.addInstructions(
            0,
            """
                # View.getTag() → check if this is our debug entry
                invoke-virtual           {p1}, Landroid/view/View;->getTag()Ljava/lang/Object;
                move-result-object       v0
                if-eqz                   v0, :original
                instance-of              v1, v0, Lcom/particlemedia/feature/settings/SettingItem;
                if-eqz                   v1, :original
                check-cast               v0, Lcom/particlemedia/feature/settings/SettingItem;
                iget-object              v0, v0, Lcom/particlemedia/feature/settings/SettingItem;->id:Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingId;
                sget-object              v1, Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingId;->Favorite:Lcom/particlemedia/feature/settings/SettingItem${'$'}SettingId;
                if-ne                    v0, v1, :original

                # ── It's our entry — build debug dialog ──
                invoke-virtual           {p1}, Landroid/view/View;->getContext()Landroid/content/Context;
                move-result-object       v0

                # Get package name
                invoke-virtual           {v0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;
                move-result-object       v1

                # Get version name via PackageManager.getPackageInfo(pkg, 0)
                invoke-virtual           {v0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;
                move-result-object       v2
                const/4                  v3, 0x0
                invoke-virtual           {v2, v1, v3}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
                move-result-object       v2
                iget-object              v2, v2, Landroid/content/pm/PackageInfo;->versionName:Ljava/lang/String;

                # Get live premium status
                invoke-static            {}, Lcom/particlemedia/feature/subscription/PremiumEntitlementHelper;->isPremiumActive()Z
                move-result              v3

                # ── Build message via StringBuilder ──
                new-instance             v4, Ljava/lang/StringBuilder;
                invoke-direct            {v4}, Ljava/lang/StringBuilder;-><init>()V

                # "CrimeRadar v" + version
                const-string             v5, "CrimeRadar v"
                invoke-virtual           {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
                invoke-virtual           {v4, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

                # "\nPackage: " + packageName
                const-string             v5, "\nPackage: "
                invoke-virtual           {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
                invoke-virtual           {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

                # "\nPremium: " + (ACTIVE | INACTIVE)
                const-string             v5, "\n\nPremium: "
                invoke-virtual           {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
                if-eqz                   v3, :inactive
                const-string             v5, "ACTIVE"
                goto                     :status_done
                :inactive
                const-string             v5, "INACTIVE"
                :status_done
                invoke-virtual           {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

                # Patch list
                const-string             v5, "\n\nApplied patches:\n\n* Premium Bypass\n* Package Rename\n* Replay Unlimited\n* Telemetry Block\n* History Cap Removed\n* Notifications Enhanced\n* CrimeRadar+ Branding\n* Debug Settings"
                invoke-virtual           {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

                invoke-virtual           {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
                move-result-object       v5

                # ── Show AlertDialog ──
                new-instance             v4, Landroid/app/AlertDialog${'$'}Builder;
                invoke-direct            {v4, v0}, Landroid/app/AlertDialog${'$'}Builder;-><init>(Landroid/content/Context;)V

                const-string             v6, "CrimeRadar+ Debug"
                invoke-virtual           {v4, v6}, Landroid/app/AlertDialog${'$'}Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog${'$'}Builder;

                invoke-virtual           {v4, v5}, Landroid/app/AlertDialog${'$'}Builder;->setMessage(Ljava/lang/CharSequence;)Landroid/app/AlertDialog${'$'}Builder;

                const/4                  v5, 0x0
                const-string             v6, "OK"
                invoke-virtual           {v4, v6, v5}, Landroid/app/AlertDialog${'$'}Builder;->setPositiveButton(Ljava/lang/CharSequence;Landroid/content/DialogInterface${'$'}OnClickListener;)Landroid/app/AlertDialog${'$'}Builder;

                invoke-virtual           {v4}, Landroid/app/AlertDialog${'$'}Builder;->show()Landroid/app/AlertDialog;

                return-void

                :original
            """
        )
    }
}
