package dem.corp.story

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import dem.corp.story.comment.CommentsAdapter
import dem.corp.story.databinding.ActivityCommentBinding
import dem.corp.story.models.Comment
import dem.corp.story.repository.firebase.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CommentActivity : AppCompatActivity() {
    private var adapter : CommentsAdapter? = null

    lateinit var binding: ActivityCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyID = intent.getStringExtra("storyID")

        initializeRecView()

        val storyTitle = intent.getStringExtra("storyTitle")

        binding.nameTxt.text = storyTitle

        binding.backFromCommentsBtn.setOnClickListener {
            startActivity(Intent (applicationContext, MainActivity::class.java))
        }

        binding.refreshComments.setOnRefreshListener {
            initializeRecView()
            binding.refreshComments.isRefreshing = false
        }

        binding.commentBtnSend.setOnClickListener {
            if (binding.commentEdit.text.toString().isEmpty()){
                FancyToast.makeText(applicationContext, "Field cannot be empty", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            }else{
                val commentText = binding.commentEdit.text.toString()
                val commentID =  DATABASE_ROOT.child(NODE_STORIES).push().key!!
                if (storyID != null) {
                    val map = HashMap<String, String>()
                    val date = Date()
                    val formatForDate = SimpleDateFormat("dd.MM.yyyy hh:mm")
                    DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val username = task.result!!.value.toString()
                            map.put("username", username)
                            map.put("from", UID)
                            map.put("text", commentText)
                            map.put("date", formatForDate.format(date))
                            DATABASE_ROOT.child("Stories").child(storyID).child("comments").child(commentID).setValue(map)
                            initializeRecView()
                        } else {
                            task.exception?.printStackTrace()
                        }
                    }

                    binding.commentEdit.setText("")

//                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun initializeRecView(){

        val comments: ArrayList<Comment> = ArrayList<Comment>()
        val storyID = intent.getStringExtra("storyID")
        var e = true
        if (storyID != null) {
            DATABASE_ROOT.child("Stories").child(storyID).child("comments").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (e) {
                        for (snapshot1:DataSnapshot in snapshot.children){
                            val date = snapshot1.child("date").getValue().toString()
                            val username = snapshot1.child("username").getValue().toString()
                            val from = snapshot1.child("from").getValue().toString()
                            val text = snapshot1.child("text").getValue().toString()

                            comments.add(Comment(text, username, date, from))

                        }

                        binding.commentsRecycler.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                        adapter = CommentsAdapter(applicationContext, comments)
                        binding.commentsRecycler.adapter = adapter
                        e = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}