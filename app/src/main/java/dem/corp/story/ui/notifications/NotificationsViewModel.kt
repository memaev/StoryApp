package dem.corp.story.ui.notifications

import android.provider.Contacts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dem.corp.story.comment.CommentsAdapter
import dem.corp.story.models.Comment
import dem.corp.story.models.Notification
import dem.corp.story.repository.firebase.DATABASE_ROOT
import dem.corp.story.repository.firebase.UID

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}