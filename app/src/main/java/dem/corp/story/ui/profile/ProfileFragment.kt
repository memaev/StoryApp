package dem.corp.story.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarolegovich.discretescrollview.DiscreteScrollView
import dem.corp.story.R
import dem.corp.story.StartActivity
import dem.corp.story.databinding.FragmentProfileBinding
import dem.corp.story.models.Story
import dem.corp.story.repository.firebase.AUTH
import dem.corp.story.repository.firebase.DATABASE_ROOT
import dem.corp.story.repository.firebase.UID
import dem.corp.story.story.StoryAdapter
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    private var notificationsViewModel: ProfileViewModel? = null
    private var binding: FragmentProfileBinding? = null
    private var recyclerView: DiscreteScrollView? = null
    private var adapter: StoryAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        recyclerView = binding!!.profileRv

        //logout from account
        binding?.profileLogout?.setOnClickListener{
            AUTH.signOut()
            startActivity(Intent(context, StartActivity::class.java))
        }

        binding?.refreshProfile?.setOnRefreshListener {
            refreshProfile()
        }

        refreshProfile()

        initializeRecView()

        val stories: ArrayList<Story> = ArrayList<Story>()
        var e = true
        DATABASE_ROOT.child("Users").child(UID).child("myStories").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot1: DataSnapshot in snapshot.children){
                    val date = snapshot1.child("date").value.toString()
                    val title = snapshot1.child("title").value.toString()
                    val from = snapshot1.child("from").value.toString()
                    val text = snapshot1.child("text").value.toString()
                    val id = snapshot1.child("id").value.toString()
                    val imageUrl = snapshot1.child("imageUrl").value.toString()
                    val likes: HashMap<String, Any> = HashMap<String, Any> ()

                    for (snapshot2: DataSnapshot in snapshot1.child("likes").children){
                        likes.put(snapshot2.key.toString(), "")
                    }

                    stories.add(Story(likes, from, title, text, date, id, imageUrl))

                }

                adapter = StoryAdapter(context, stories, true)
                recyclerView?.adapter = adapter
                if (e) {

                    e = false
                    Log.d("storiesCount", "ajsc: ${stories.size}")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun refreshProfile(){
        DATABASE_ROOT.child("Users").child(UID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("username").value.toString()
                    val bio = snapshot.child("bio").value.toString()
                    val likes = snapshot.child("likes").value.toString()
                    val storiesCount = snapshot.child("storiesCount").value.toString()

                    binding!!.profileUsername.text = username
                    binding!!.profileBio.text = bio
                    binding!!.profileLikesCount.text = likes
                    binding!!.profileStoriesCount.text = storiesCount
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        initializeRecView()
    }

    fun initializeRecView(){

        val stories: ArrayList<Story> = ArrayList<Story>()

        DATABASE_ROOT.child("Users").child(UID).child("myStories").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot1: DataSnapshot in snapshot.children){
                    val date = snapshot1.child("date").value.toString()
                    val title = snapshot1.child("title").value.toString()
                    val from = snapshot1.child("from").value.toString()
                    val text = snapshot1.child("text").value.toString()
                    val id = snapshot1.child("id").value.toString()
                    val imageUrl = snapshot1.child("imageUrl").value.toString()
                    val likes: HashMap<String, Any> = HashMap<String, Any> ()
                    for (snapshot2: DataSnapshot in snapshot1.child("likes").children){
                        likes.put(snapshot2.key.toString(), "")
                    }

                    stories.add(Story(likes, from, title, text, date, id, imageUrl))
                }

                adapter = StoryAdapter(context, stories, true)
                recyclerView?.adapter = adapter
                binding?.refreshProfile?.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}