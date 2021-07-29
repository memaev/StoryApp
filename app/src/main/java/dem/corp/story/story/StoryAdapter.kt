package dem.corp.story.story

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dem.corp.story.CommentActivity
import dem.corp.story.R
import dem.corp.story.models.Comment
import dem.corp.story.models.Story

internal class StoryAdapter(options: FirebaseRecyclerOptions<Story>) :
    FirebaseRecyclerAdapter<Story, dem.corp.story.story.StoryAdapter.viewholder>(options) {
    override fun getItem(position: Int): Story {
        return super.getItem(itemCount - 1 - position)
    }

    override fun onBindViewHolder(
        holder: StoryAdapter.viewholder,
        i: Int,
        post: Story
    ) {
        var i = i
        i = itemCount - 1 - i
        holder.title.text = post.title
        val finalI = i

        holder.commentBtn.setOnClickListener(View.OnClickListener { v ->
            val intent = Intent (v.context, CommentActivity::class.java)
            intent.putExtra("storyTitle", "story")
            intent.putExtra("storyID", getRef(finalI).key)
            v.context.startActivity(intent)
        })


        holder.read.setOnClickListener { v ->
            val bottomSheetDialog = RoundedBottomSheetDialog(v.context)
            bottomSheetDialog.setContentView(R.layout.read_story_bottom_sheet)
            bottomSheetDialog.show()
            val story_text = bottomSheetDialog.findViewById<TextView>(R.id.story_txt)
            val story_title = bottomSheetDialog.findViewById<TextView>(R.id.story_title)
            val key = getRef(finalI).key
            FirebaseDatabase.getInstance().getReference("Stories").child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val text = snapshot.child("text").value.toString()
                        val title = snapshot.child("title").value.toString()
                        story_text!!.text = text
                        story_title!!.text = title
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryAdapter.viewholder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.story_item, parent, false)
        Log.d("j", "Adapter started")
        return StoryAdapter.viewholder(view)
    }

    internal class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var read: ImageButton
        var commentBtn: ImageButton

        init {
            title = itemView.findViewById(R.id.story_title)
            read = itemView.findViewById(R.id.read_more)
            commentBtn = itemView.findViewById(R.id.comment_btn)
        }
    }
}