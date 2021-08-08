package dem.corp.story.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dem.corp.story.databinding.FragmentNotificationsBinding
import dem.corp.story.models.Notification
import dem.corp.story.notification.NotificationsAdapter
import dem.corp.story.repository.firebase.DATABASE_ROOT
import dem.corp.story.repository.firebase.UID
import java.util.*
import kotlin.collections.ArrayList


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var adapter: NotificationsAdapter? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initializeRV()

        binding.refreshNotifications.setOnRefreshListener(OnRefreshListener {

            binding.refreshNotifications.isRefreshing = true
            initializeRV()
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initializeRV(){
        var notificationList2: ArrayList<Notification> = ArrayList<Notification>()
        var e = true
        DATABASE_ROOT.child("Users").child(UID).child("notifications").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (e) {
                    for (snapshot1: DataSnapshot in snapshot.children){
                        val date = snapshot1.child("date").value.toString()
                        val username = snapshot1.child("username").value.toString()
                        val from = snapshot1.child("from").value.toString()
                        val type = snapshot1.child("type").value.toString()
                        val storyID = snapshot1.child("storyID").value.toString()
                        val text = snapshot1.child("text").value.toString()

                        notificationList2.add(Notification(type, from, username, storyID, date, text))
                    }

                    binding.notificationsRV.layoutManager = LinearLayoutManager(context)
                    val adapter2 = NotificationsAdapter(context, notificationList2)
                    binding.notificationsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = adapter2
                    binding.notificationsRV.addItemDecoration(
                        DividerItemDecoration(
                            binding.notificationsRV.context,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    binding.notificationsRV.adapter = adapter
                    binding.refreshNotifications.isRefreshing = false
                    e = false
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}