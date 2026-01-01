
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatup.data.ConversationList

class ConversationListViewModel : ViewModel() {

    val conversation = MutableLiveData<List<ConversationList>>()

}