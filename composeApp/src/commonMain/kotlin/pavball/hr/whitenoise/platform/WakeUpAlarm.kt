package pavball.hr.whitenoise.platform

/** Schedules the wake-up alarm with the platform's own clock/alarm app. */
interface WakeUpAlarm {
    /** Returns false when no alarm app could take the alarm. */
    fun schedule(hour: Int, minute: Int, label: String): Boolean
}
