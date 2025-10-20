package pavball.hr.whitenoise.ui.components

import AvPlayerView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import isAudioFile

@Composable
actual fun MediaPlayerComponent(modifier: Modifier, url: String, start: Boolean, pause: Boolean, stop: Boolean){

    if(isAudioFile(url)){
        AvPlayerView(
            modifier = modifier.fillMaxWidth(),
            url = "C:\\Users\\mmatijevic5\\Desktop\\Projekti\\Pomocni\\Udemy Multiplatform App\\WhiteNoise\\composeApp\\src\\commonMain\\composeResources\\files\\youtube_yCykHHqk_z0_audio.mp3",
            autoPlay = false,
            showControls = true
        )
    }

}