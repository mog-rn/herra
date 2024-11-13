package com.herra_org.heraclient.domain.model.cycles

enum class CyclePhase {
    MENSTRUAL,
    FOLLICULAR,
    OVULATION,
    LUTEAL;

    fun toDisplayName(): String {
        return when (this) {
            MENSTRUAL -> "Menstrual Phase"
            FOLLICULAR -> "Follicular Phase"
            OVULATION -> "Ovulation Phase"
            LUTEAL -> "Luteal Phase"
        }
    }
}