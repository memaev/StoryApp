package dem.corp.story.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yarolegovich.discretescrollview.DiscreteScrollView
import dem.corp.story.R
import dem.corp.story.databinding.FragmentHomeBinding
import dem.corp.story.models.Comment
import dem.corp.story.models.Story
import dem.corp.story.repository.firebase.DATABASE_ROOT
import dem.corp.story.repository.firebase.NODE_USERS
import dem.corp.story.repository.firebase.UID
import dem.corp.story.repository.firebase.createStory
import dem.corp.story.story.StoryAdapter
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private var binding: FragmentHomeBinding? = null
    private lateinit var adapter: StoryAdapter
    private lateinit var title: EditText
    private lateinit var text: EditText
    private lateinit var createStoryBtn2: Button
    private lateinit var close_btn: ImageView
    private lateinit var btmSh: RoundedBottomSheetDialog
    private lateinit var storyImage: ImageView
    private var imageUri: Uri? = null

    private lateinit var recView: DiscreteScrollView
    private lateinit var refreshHome: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recView = root.findViewById<DiscreteScrollView>(R.id.storyRecView)
        refreshHome = root.findViewById<SwipeRefreshLayout>(R.id.refresh_home)
        val createStoryBtn = root.findViewById<RelativeLayout>(R.id.create_event_btn)
        btmSh = RoundedBottomSheetDialog(root.context)

        val comments: ArrayList<Comment> = ArrayList<Comment>()
        val stories: ArrayList<Story> = ArrayList<Story>()
        var e = true

        initializeRecView()

        refreshHome.setOnRefreshListener {
            initializeRecView()
        }

        createStoryBtn.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.create_story_bottom_sheet)

            storyImage = dialog.findViewById(R.id.story_image)!!
            title = dialog.findViewById(R.id.edit_txt_title_story)!!
            text = dialog.findViewById(R.id.edit_txt_text_story)!!
            createStoryBtn2 = dialog.findViewById(R.id.create_story)!!
            close_btn = dialog.findViewById(R.id.close_dialog)!!

            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.show()

            storyImage.setOnClickListener {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 8080)
            }

            //по клику на кнопку создания истории добавляем её в БД
            createStoryBtn2.setOnClickListener {
                if (!text.text.toString().isEmpty() && !text.text.toString().isEmpty()) {
                    createStory()
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            close_btn.setOnClickListener {
                dialog.dismiss()
            }
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        initializeRecView()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            8080 -> if (resultCode == RESULT_OK) {
                imageUri = data?.data
                if (imageUri != null) {
                    storyImage?.setImageURI(imageUri)
                    storyImage?.setBackgroundResource(0)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createStory() {

        var e = true
        DATABASE_ROOT.child(NODE_USERS).child(UID).child("storiesCount").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (e){
                    var res = snapshot.value.toString()
                    var storiesCount = res.toInt()
                    storiesCount+=1
                    DATABASE_ROOT.child(NODE_USERS).child(UID).child("storiesCount").setValue(storiesCount.toString())
                    e = false
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val date = SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date())
        Log.d("Get date", "createStory: $date")
        val story = Story(text = text.text.toString(), title = title.text.toString(), date = date)
        if (imageUri==null){
            val uri = Uri.parse("android.resource://dem.corp.story/drawable/wall")
            createStory(story, uri)
        }else{
            createStory(story, imageUri)
        }


        btmSh.dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun showBottomSheet() {

    }

    fun initializeRecView(){

        val stories: ArrayList<Story> = ArrayList<Story>()
        val comments: ArrayList<Comment> = ArrayList<Comment>()
        var e = true
        DATABASE_ROOT.child("Stories").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (e) {
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

                    Log.d("storiesCount", stories.size.toString())
                    adapter = StoryAdapter(context, stories, false)
                    recView?.adapter = adapter
                    refreshHome.isRefreshing = false
                    e = false
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}