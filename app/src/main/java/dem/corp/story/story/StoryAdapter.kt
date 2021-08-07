package dem.corp.story.story

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dem.corp.story.CommentActivity
import dem.corp.story.R
import dem.corp.story.models.Story
import dem.corp.story.repository.firebase.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

internal class StoryAdapter(options: FirebaseRecyclerOptions<Story>) :
    FirebaseRecyclerAdapter<Story, dem.corp.story.story.StoryAdapter.viewholder>(options) {

    override fun getItem(position: Int): Story {
        return super.getItem(itemCount - 1 - position)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(
        holder: StoryAdapter.viewholder,
        i: Int,
        post: Story
    ) {
        var i = i
        i = itemCount - 1 - i

        holder.title.text = post.title
        holder.date.text = if (post.date != "") {
            val firstFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")
            var period = Period.between(
                LocalDate.parse(post.date, firstFormatter),
                LocalDate.parse(
                    SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date()),
                    firstFormatter
                )
            )
            if (period.months != 0) period.months.toString() + " months ago"
            else if (period.days != 0) period.days.toString() + " days ago"
            else "today"
        } else "today"

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
            val storyText = bottomSheetDialog.findViewById<TextView>(R.id.story_txt)
            val storyTitle = bottomSheetDialog.findViewById<TextView>(R.id.story_title)
            var storyDate = bottomSheetDialog.findViewById<TextView>(R.id.tv_btm_shed_date)
            val key = getRef(finalI).key
            var e = true
            FirebaseDatabase.getInstance().getReference("Stories").child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (e) {
                            val text = snapshot.child("text").value.toString()
                            val title = snapshot.child("title").value.toString()
                            val date = snapshot.child("date").value.toString()
                            storyText!!.text = text
                            storyTitle!!.text = title
                            Log.d("TAG", date)
                            storyDate!!.text = if (post.date != "") {
                                val firstFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")
                                var period = Period.between(
                                    LocalDate.parse(post.date, firstFormatter),
                                    LocalDate.parse(
                                        SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date()),
                                        firstFormatter
                                    )
                                )
                                if (period.months != 0) period.months.toString() + " months ago"
                                else if (period.days != 0) period.days.toString() + " days ago"
                                else "today"
                            } else "today"
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
        val date: TextView = itemView.findViewById(R.id.tv_story_date)
        val read: ImageButton = itemView.findViewById(R.id.read_more)
        val commentBtn: ImageButton = itemView.findViewById(R.id.comment_btn)
        val likeBtn: ImageButton = itemView.findViewById(R.id.like_story)
        val likesCount: TextView = itemView.findViewById(R.id.likes_count)
        val storyImage: ImageView = itemView.findViewById(R.id.story_image)

        init {
            initOnClick()
            updateFields()
        }

        fun updateFields() {
            updateLikeImage()
            updateLikesCountText()
            updateStoryImage()
        }

        private fun updateStoryImage() {
            try {
                val url = getItem(layoutPosition).imageUrl
                if (url != "")
                    Glide.with(itemView).load(url).into(storyImage)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        private fun updateLikeImage() {
            try {
                if (getItem(layoutPosition).getLikesList().contains(AUTH.uid!!)) {
                    setLikeIsClicked()
                } else {
                    setLikeIsNotClicked()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun updateLikesCountText() {
            try {
                likesCount.text = getItem(position).likes.keys.size.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun setLikeIsClicked() = likeBtn.setBackgroundResource(R.drawable.ic_like_clicked)

        private fun setLikeIsNotClicked() = likeBtn.setBackgroundResource(R.drawable.ic_like)

        private fun sendNotification(position: Int, key: String) {
            var e = true
            DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot1: DataSnapshot) {
                        if (e) {
                            val username = snapshot1.getValue().toString()
                            var a = true
                            DATABASE_ROOT.child("Stories").child(key).child("from")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot2: DataSnapshot) {
                                        if (a) {
                                            val from = snapshot2.getValue().toString()
                                            val map = HashMap<String, String>()
                                            map.put("from", from)
                                            map.put("username", username)
                                            map.put("storyID", key)
                                            map.put("type", "like")
                                            DATABASE_ROOT.child("Users").child(UID)
                                                .child("notifications").child(key).setValue(map)
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

        private fun deleteNotification(position: Int, key: String) {
            DATABASE_ROOT.child("Users").child(UID).child("notifications").child(key).setValue(null)
        }

        private fun initOnClick() {
            likeBtn.setOnClickListener {
                val story = getItem(position)
                val key = getRef(itemCount - 1 - position).key.toString()
                if (story.getLikesList().contains(AUTH.uid!!)) {
                    removeLikeFromStory(story) {
                        story.likes.remove(AUTH.uid!!)
                        setLikeIsNotClicked()
                        deleteNotification(position, key)
                        likesCount.text = story.likes.size.toString()
                    }
                } else {
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