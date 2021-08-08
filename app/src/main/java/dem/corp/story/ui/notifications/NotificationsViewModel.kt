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

//    val notificationsList =  MutableLiveData<List<Notification>>().apply {
//        var notificationList2: ArrayList<Notification> = ArrayList<Notification>()
//        var e = true
//        DATABASE_ROOT.child("Users").child(UID).child("notifications").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (e) {
//                    for (snapshot1:DataSnapshot in snapshot.children){
//                        val date = snapshot1.child("date").getValue().toString()
//                        val username = snapshot1.child("username").getValue().toString()
//                        val from = snapshot1.child("from").getValue().toString()
//                        val type = snapshot1.child("type").getValue().toString()
//                        val storyID = snapshot1.child("storyID").getValue().toString()
//
//                        notificationList2.add(Notification(type, from, username, storyID, date))
//
//                    }
//
//                    e = false
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}