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
import com.google.firebase.database.FirebaseDatabase
import dem.corp.story.StartActivity
import dem.corp.story.databinding.FragmentProfileBinding
import dem.corp.story.models.Story
import dem.corp.story.repository.firebase.AUTH
import dem.corp.story.story.StoryAdapter

class ProfileFragment : Fragment() {
    private var notificationsViewModel: ProfileViewModel? = null
    private var binding: FragmentProfileBinding? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: StoryAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        recyclerView = binding!!.profileRv

        //get username
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("username").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val name = task.result!!.value.toString()
                binding!!.profileUsername.text = name
            }
        }

        //logout from account
        binding?.profileLogout?.setOnClickListener{
            AUTH.signOut()
            startActivity(Intent(context, StartActivity::class.java))
        }

        //get bio
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("bio").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val bio = task.result!!.value.toString()
                binding!!.profileBio.text = bio
            }
        }

        //get likes count
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("likes").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val likesCount = task.result!!.value.toString()
                binding!!.profileLikesCount.text = likesCount
            }
        }

        //set query for FirebaseRecyclerAdapter
        val options: FirebaseRecyclerOptions<Story> = FirebaseRecyclerOptions.Builder<Story>()
            .setQuery(
                FirebaseDatabase.getInstance().getReference(
                    "Users/" + FirebaseAuth.getInstance().currentUser!!
                        .uid + "/myStories"
                ),
                Story::class.java
            )
            .build()
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        Log.d("j", "Starting adapter...")
        recyclerView!!.layoutManager = linearLayoutManager
        adapter = StoryAdapter(options)
        if (adapter == null) {
            Log.d("j", "Adapter is null")
        }
        recyclerView!!.adapter = adapter

        //get stories count
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("myStories").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val stories = task.result!!.value.toString()
                binding?.profileStoriesCount?.text = stories
            }
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}