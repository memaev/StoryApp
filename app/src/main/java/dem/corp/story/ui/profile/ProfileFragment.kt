package dem.corp.story.ui.profile

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
import dem.corp.story.R
import dem.corp.story.databinding.FragmentProfileBinding
import dem.corp.story.story.Story
import dem.corp.story.story.StoryAdapter

class ProfileFragment : Fragment() {
    private var notificationsViewModel: ProfileViewModel? = null
    private var binding: FragmentProfileBinding? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: StoryAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root2: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val root: View = binding!!.root
        recyclerView = binding!!.profileRv
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("username").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val name = task.result!!.value.toString()
                binding!!.profileUsername.text = name
            }
        }
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("bio").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val bio = task.result!!.value.toString()
                binding!!.profileBio.text = bio
            }
        }
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("likes").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val likesCount = task.result!!.value.toString()
                binding!!.profileLikesCount.text = likesCount
                Log.d("j", "likes counted")
            }
        }
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
        FirebaseDatabase.getInstance().getReference("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        ).child("myStories").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val invites = task.result!!.value.toString()
                if (!invites.isEmpty()) {
                    val count = invites.split(",").toTypedArray().size - 1
                    binding!!.profileStoriesCount.text = Integer.toString(count)
                } else {
                    binding!!.profileStoriesCount.text = "0"
                }
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