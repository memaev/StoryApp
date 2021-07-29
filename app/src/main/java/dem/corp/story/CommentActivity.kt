package dem.corp.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import dem.corp.story.comment.CommentsAdapter
import dem.corp.story.models.Comment

class CommentActivity : AppCompatActivity() {
    private var adapter : CommentsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val options: FirebaseRecyclerOptions<Comment> = FirebaseRecyclerOptions.Builder<Comment>()
            .setQuery(
                FirebaseDatabase.getInstance().getReference(
                    "Stories/" + intent.getStringExtra("storyID") +  "/comments"
                ),
                Comment::class.java
            )
            .build()

        val storyTitle = intent.getStringExtra("storyTitle")
        val storyTitleText = findViewById<TextView>(R.id.name_txt)
        val backBtn = findViewById<ImageButton>(R.id.back_from_comments_btn)
        val recView = findViewById<RecyclerView>(R.id.comments_recycler)
        recView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        adapter = CommentsAdapter(options)
        recView.adapter = adapter

        storyTitleText.text = storyTitle

        backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}