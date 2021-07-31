package dem.corp.story.story

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import dem.corp.story.repository.firebase.*
import java.lang.Exception

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

        holder.commentBtn.setOnClickListener { v ->
            val intent = Intent(v.context, CommentActivity::class.java)
            intent.putExtra("storyTitle", post.title)
            intent.putExtra("storyID", getRef(finalI).key)
            v.context.startActivity(intent)
        }

        holder.updateFields()

        holder.read.setOnClickListener { v ->
            val bottomSheetDialog = RoundedBottomSheetDialog(v.context)
            bottomSheetDialog.setContentView(R.layout.read_story_bottom_sheet)
            bottomSheetDialog.show()
            val story_text = bottomSheetDialog.findViewById<TextView>(R.id.story_txt)
            val story_title = bottomSheetDialog.findViewById<TextView>(R.id.story_title)
            val key = getRef(finalI).key
            var e = true
            FirebaseDatabase.getInstance().getReference("Stories").child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (e){
                            val text = snapshot.child("text").value.toString()
                            val title = snapshot.child("title").value.toString()
                            story_text!!.text = text
                            story_title!!.text = title
                            e = false
                        }
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
        return viewholder(view)
    }

    internal inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.story_title)
        val read: ImageButton = itemView.findViewById(R.id.read_more)
        val commentBtn: ImageButton = itemView.findViewById(R.id.comment_btn)
        val likeBtn: ImageButton = itemView.findViewById(R.id.like_story)
        val likesCount: TextView = itemView.findViewById(R.id.likes_count)

        init {
            initOnClick()
            updateFields()
        }

        fun updateFields(){
            updateLikeImage()
            updateLikesCountText()
        }

        private fun updateLikeImage() {
            try {
                if (getItem(layoutPosition).getLikesList().contains(AUTH.uid!!)) {
                    setLikeIsClicked()
                } else {
                    setLikeIsNotClicked()
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        private fun updateLikesCountText() {
            try { likesCount.text = getItem(position).likes.keys.size.toString() } catch (e: Exception){ e.printStackTrace() }
        }

        private fun setLikeIsClicked() = likeBtn.setBackgroundResource(R.drawable.ic_like_clicked)

        private fun setLikeIsNotClicked() = likeBtn.setBackgroundResource(R.drawable.ic_like)

        private fun sendNotification(position: Int, key:String){
            var e = true
            DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot1: DataSnapshot) {
                        if (e){
                            val username = snapshot1.getValue().toString()
                            var a = true
                            DATABASE_ROOT.child("Stories").child(key).child("from")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot2: DataSnapshot) {
                                        if (a){
                                            val from = snapshot2.getValue().toString()
                                            val map = HashMap<String, String> ()
                                            map.put("from", from)
                                            map.put("username", username)
                                            map.put("storyID", key)
                                            map.put("type", "like")
                                            DATABASE_ROOT.child("Users").child(UID).child("notifications").child(key).setValue(map)
                                            a = false
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            e = false
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
//            DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).addValueEventListener(ValueEventListener)
//            DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).get().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val username = task.result.toString()
//                    DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).get().addOnCompleteListener { task2 ->
//                        if (task2.isSuccessful){
//                            val from = task2.result.toString()
//                            val map = HashMap<String, String> ()
//                            map.put("from", from)
//                            map.put("username", username)
//                            map.put("storyID", key)
//                            map.put("type", "like")
//                            DATABASE_ROOT.child("Users").child(UID).child("notifications").child(key).setValue(map)
//                        }
//                    }
//                } else {
//                    task.exception?.printStackTrace()
//                }
//            }
        }

        private fun deleteNotification(position: Int, key:String){
            DATABASE_ROOT.child("Users").child(UID).child("notifications").child(key).setValue(null)
        }

        private fun initOnClick() {
            likeBtn.setOnClickListener {
                val story = getItem(position)
                val key = getRef(itemCount-1-position).key.toString()
                if(story.getLikesList().contains(AUTH.uid!!)){
                    removeLikeFromStory(story) {
                        story.likes.remove(AUTH.uid!!)
                        setLikeIsNotClicked()
                        deleteNotification(position, key)
                        likesCount.text = story.likes.size.toString()
                    }
                }else{
                    putLikeToStory(story) {
                        story.likes[AUTH.uid!!] = ""
                        setLikeIsClicked()
                        sendNotification(position, key)
                        likesCount.text = story.likes.size.toString()
                    }
                }
            }
        }

    }
}