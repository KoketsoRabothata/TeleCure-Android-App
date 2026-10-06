package com.telecure.app.ui.components

/** Fixed "virtual" chat contacts that reuse the existing doctor-chat infrastructure
 *  (ChatThread/ChatMessage) without needing a real doctor account behind them. */
const val PHARMACIST_UID = "telecure_pharmacist"
const val PHARMACIST_NAME = "TeleCure Pharmacist"
const val SUPPORT_UID = "telecure_support"
const val SUPPORT_NAME = "TeleCure Support"

val FIXED_CONTACT_UIDS = setOf(PHARMACIST_UID, SUPPORT_UID)
