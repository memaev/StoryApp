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
import com.google.firebase.database.FirebaseDatabase
import com.yarolegovich.discretescrollview.DiscreteScrollView
import dem.corp.story.R
import dem.corp.story.databinding.FragmentHomeBinding
import dem.corp.story.models.Story
import dem.corp.story.repository.firebase.createStory
import dem.corp.story.story.StoryAdapter

class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: StoryAdapter
    private lateinit var title: EditText
    private lateinit var text: EditText
    private lateinit var createStoryBtn2: Button
    private lateinit var close_btn: ImageView
    private lateinit var btmSh: RoundedBottomSheetDialog
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
        recView.adapter = adapter
        createStoryBtn.setOnClickListener {
            showBottomSheet()
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
            //по клику на кнопку создания истории добавляем её в БД
            createStoryBtn2.setOnClickListener {
                if (!text.text.toString().isEmpty() && !text.text.toString().isEmpty()) {
                    createStory()
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            close_btn.setOnClickListener {
                btmSh.dismiss()
            }
        }
        return root
    }

    private fun createStory() {

        val story = Story(text = text.text.toString(), title = title.text.toString())
        createStory(story)

        btmSh.dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    fun showBottomSheet() {
        btmSh.setContentView(R.layout.create_story_bottom_sheet)
        title = btmSh.findViewById(R.id.edit_txt_title_story)!!
        text = btmSh.findViewById(R.id.edit_txt_text_story)!!
        createStoryBtn2 = btmSh.findViewById(R.id.create_story)!!
        close_btn = btmSh.findViewById(R.id.close_dialog)!!
        btmSh.show()
    }
}