package pavball.hr.whitenoise.domain.usecase.mix

import kotlinx.coroutines.flow.Flow
import pavball.hr.whitenoise.domain.model.SoundMix
import pavball.hr.whitenoise.repositories.SoundMixRepository

internal interface GetSoundMixesUseCase {
    operator fun invoke(): Flow<List<SoundMix>>
}

internal class GetSoundMixes(private val repository: SoundMixRepository) : GetSoundMixesUseCase {
    override fun invoke(): Flow<List<SoundMix>> = repository.getSoundMixes()
}
