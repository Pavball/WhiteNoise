package pavball.hr.whitenoise.ui.viewmodels

internal sealed class MainScreenViewState {
    data object Initial : MainScreenViewState()
}

internal abstract class MainScreenViewModel : BaseViewModel<MainScreenViewState>() {

}

internal class MainScreenViewModelImpl() : MainScreenViewModel() {


}