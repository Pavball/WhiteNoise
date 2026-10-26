package pavball.hr.whitenoise.platform

/**
 * Some phones stop "battery optimized" apps overnight even while they play
 * audio, so Settings lets the user exempt the app.
 */
interface BackgroundPlayback {
    /** True when the system won't restrict the app in the background. */
    fun isUnrestricted(): Boolean

    /** Opens the system screen where background use can be allowed. */
    fun openSettings()
}
