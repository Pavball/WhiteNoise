package pavball.hr.whitenoise.playback

import androidx.annotation.OptIn
import androidx.media3.common.ForwardingSimpleBasePlayer
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * Wraps the ExoPlayer that the media session exposes so that:
 * - next/previous wrap around the sound list (the player itself repeats the
 *   current sound, which would otherwise disable "next" on the last sound and
 *   make "previous" restart the sound instead of switching);
 * - the notification subtitle can show the sleep timer via [subtitle];
 * - the notification shows the app's sound names, not the audio files' own tags.
 */
@OptIn(UnstableApi::class)
internal class DozzzePlayer(player: Player) : ForwardingSimpleBasePlayer(player) {

    /** Replaces the artist line of the current sound, e.g. "Sleep timer · 25 min left". */
    var subtitle: String? = null
        set(value) {
            if (field != value) {
                field = value
                invalidateState()
            }
        }

    override fun getState(): State {
        val state = super.getState()
        val builder = state.buildUpon()

        if (player.mediaItemCount > 1) {
            builder.setAvailableCommands(
                state.availableCommands.buildUpon()
                    .addAll(
                        COMMAND_SEEK_TO_NEXT,
                        COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
                        COMMAND_SEEK_TO_PREVIOUS,
                        COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
                    )
                    .build()
            )
        }

        val currentItem = player.currentMediaItem
        if (currentItem != null && player.isCommandAvailable(COMMAND_GET_TIMELINE)) {
            // Use only the metadata the app sets on the MediaItem: the bundled mp3s carry
            // their own ID3 titles and cover art, which would otherwise show in the notification.
            val metadata = currentItem.mediaMetadata.buildUpon()
                .apply { subtitle?.let(::setArtist) }
                .build()
            builder.setPlaylist(
                player.currentTimeline,
                if (player.isCommandAvailable(COMMAND_GET_TRACKS)) player.currentTracks
                else Tracks.EMPTY,
                metadata
            )
        }
        return builder.build()
    }

    override fun handleSeek(
        mediaItemIndex: Int,
        positionMs: Long,
        seekCommand: Int
    ): ListenableFuture<*> {
        val count = player.mediaItemCount
        val step = when (seekCommand) {
            COMMAND_SEEK_TO_NEXT, COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> 1
            COMMAND_SEEK_TO_PREVIOUS, COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> -1
            else -> return super.handleSeek(mediaItemIndex, positionMs, seekCommand)
        }
        if (count > 0) {
            val target = (player.currentMediaItemIndex + step + count) % count
            player.seekToDefaultPosition(target)
        }
        return Futures.immediateVoidFuture()
    }
}
