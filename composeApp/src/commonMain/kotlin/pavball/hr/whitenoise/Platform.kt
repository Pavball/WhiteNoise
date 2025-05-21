package pavball.hr.whitenoise

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform