package dem.corp.story.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yarolegovich.discretescrollview.DiscreteScrollView
import dem.corp.story.R
import dem.corp.story.databinding.FragmentHomeBinding
import dem.corp.story.story.Story
import dem.corp.story.story.StoryAdapter

class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private var binding: FragmentHomeBinding? = null
    private var adapter: StoryAdapter? = null
    private var title: EditText? = null
    private var text: EditText? = null
    private var createStory: Button? = null
    private var close_btn: ImageView? = null
    private var btmSh: RoundedBottomSheetDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val recView = root.findViewById<DiscreteScrollView>(R.id.storyRecView)
        val createStoryBtn = root.findViewById<RelativeLayout>(R.id.create_event_btn)
        val options: FirebaseRecyclerOptions<Story> = FirebaseRecyclerOptions.Builder<Story>()
            .setQuery(
                FirebaseDatabase.getInstance().getReference(
                    "Stories"
                ),
                Story::class.java
            )
            .build()
        btmSh = RoundedBottomSheetDialog(requireContext())
        adapter = StoryAdapter(options)
        recView.setAdapter(adapter)
        createStoryBtn.setOnClickListener(View.OnClickListener {
            showBottomSheet()
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
            createStory!!.setOnClickListener {
                if (!text!!.text.toString().isEmpty() && !text!!.text.toString().isEmpty()) {
                    val key = FirebaseDatabase.getInstance().getReference("Stories")
                        .push().key //генерируем ключ истории
                    FirebaseDatabase.getInstance().getReference("Stories").child(key!!)
                        .child("text").setValue(
                            text!!.text.toString()
                        )
                    FirebaseDatabase.getInstance().getReference("Stories").child(key)
                        .child("title").setValue(
                            title!!.text.toString()
                        )
                    FirebaseDatabase.getInstance().getReference("Users").child(
                        FirebaseAuth.getInstance().currentUser!!.uid
                    ).child("username")
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val name = task.result!!.value.toString()
                                FirebaseDatabase.getInstance().getReference("Stories")
                                    .child(
                                        key
                                    ).child("from").setValue(name)
                                FirebaseDatabase.getInstance().getReference("Users").child(
                                    FirebaseAuth.getInstance().currentUser!!.uid
                                )
                                    .child("myStories").child(key).child("text")
                                    .setValue(
                                        text!!.text.toString()
                                    )
                                FirebaseDatabase.getInstance().getReference("Users").child(
                                    FirebaseAuth.getInstance().currentUser!!.uid
                                )
                                    .child("myStories").child(key).child("title")
                                    .setValue(
                                        title!!.text.toString()
                                    )
                                FirebaseDatabase.getInstance().getReference("Users").child(
                                    FirebaseAuth.getInstance().currentUser!!.uid
                                )
                                    .child("myStories").child(key).child("from")
                                    .setValue(name)
                            }
                        }
                    btmSh!!.dismiss()
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            close_btn!!.setOnClickListener { btmSh!!.dismiss() }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    fun showBottomSheet() {
        btmSh?.setContentView(R.layout.create_story_bottom_sheet)
        title = btmSh!!.findViewById(R.id.edit_txt_title_story)
        text = btmSh!!.findViewById(R.id.edit_txt_text_story)
        createStory = btmSh!!.findViewById(R.id.create_story)
        close_btn = btmSh!!.findViewById(R.id.close_dialog)
        btmSh!!.show()
    }
}