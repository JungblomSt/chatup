
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConversationListViewModel : ViewModel() {

    val conversation = MutableLiveData<List<ConversationList>>()

}