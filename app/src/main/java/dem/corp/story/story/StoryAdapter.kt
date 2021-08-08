package dem.corp.story.story

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.provider.Contacts
import android.renderscript.Sampler
import android.util.Log
import android.view.*
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
import dem.corp.story.models.Comment
import dem.corp.story.models.Story
import dem.corp.story.repository.firebase.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class StoryAdapter internal constructor(context: Context?, stories: List<Story>, isProfileFr: Boolean) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private val stories: List<Story>
    private val isProfileFr: Boolean
//    private val isProfileFragment: Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View? = null
        if (isProfileFr){
            view = inflater.inflate(R.layout.story_item_profile, parent, false)

        }
        else {
            view = inflater.inflate(R.layout.story_item, parent, false)
        }
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var i2 = itemCount - 1 - position
        val model: Story = stories[i2]
        holder.title.text = model.title
        holder.date.text = if (model.date != "" && model.date != null) {
            val firstFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")
            var period = Period.between(
                LocalDate.parse(model.date, firstFormatter),
                LocalDate.parse(
                    SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date()),
                    firstFormatter
                )
            )
            if (period.months != 0) period.months.toString() + " months ago"
            else if (period.days != 0) period.days.toString() + " days ago"
            else "today"
        } else "today"

        holder.commentBtn.setOnClickListener { v ->
            val intent = Intent(v.context, CommentActivity::class.java)
            intent.putExtra("storyTitle", model.title)
            intent.putExtra("storyID", model.id)
            intent.putExtra("storyFrom", model.from)
            v.context.startActivity(intent)
        }

        holder.updateFields()

        holder.read.setOnClickListener { v ->
            val dialog = Dialog(v.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.read_story_bottom_sheet)

            val storyText = dialog.findViewById<TextView>(R.id.story_txt)
            val storyTitle = dialog.findViewById<TextView>(R.id.story_title)
            val storyDate = dialog.findViewById<TextView>(R.id.tv_btm_shed_date)
            val storyFrom = dialog.findViewById<TextView>(R.id.story_from)
            val storyClose = dialog.findViewById<ImageButton>(R.id.dialog_close)
            val storyImage = dialog.findViewById<ImageView>(R.id.story_image)

            dialog.show()
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog.window?.setGravity(Gravity.BOTTOM)

            storyClose.setOnClickListener {
                dialog.dismiss()
            }



            Glide.with(holder.itemView).load(model.imageUrl).into(storyImage)

            val key = model.id.toString()
            var e = true
            FirebaseDatabase.getInstance().getReference("Stories").child(key!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (e) {
                            val text = snapshot.child("text").value.toString()
                            val title = snapshot.child("title").value.toString()
                            val date = snapshot.child("date").value.toString()
                            val from = snapshot.child("from").value.toString()

                            var t = true
                            DATABASE_ROOT.child("Users").child(from).child("username").addValueEventListener(object: ValueEventListener{
                                override fun onDataChange(snapshot2: DataSnapshot) {
                                    if (t){
                                        val username = snapshot2.value.toString()
                                        storyFrom!!.text = username
                                        t = false
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                            storyText!!.text = text
                            storyTitle!!.text = title
                            Log.d("TAG", date)
                            storyDate!!.text = if (model.date != "") {
                                val firstFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")
                                var period = Period.between(
                                    LocalDate.parse(model.date, firstFormatter),
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

    override fun getItemCount(): Int {
        return stories.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                val url = stories[itemCount-1-layoutPosition].imageUrl
                if (url != "")
                    Glide.with(itemView).load(url).into(storyImage)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        private fun updateLikeImage() {
            try {
                if (stories[itemCount-1-position].getLikesList().contains(AUTH.uid!!)) {
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
                likesCount.text = stories[itemCount-1-position].likes.keys.size.toString()
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
                                            val date = Date()
                                            val formatForDate = SimpleDateFormat("dd.MM.yyyy hh:mm")
                                            val from = snapshot2.getValue().toString()
                                            val map = HashMap<String, String>()
                                            map.put("from", from)
                                            map.put("username", username)
                                            map.put("storyID", key)
                                            map.put("type", "like")
                                            map.put("date", formatForDate.format(date))
                                            map.put("text", "like")

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
        }

        private fun deleteNotification(position: Int, key: String) {
            DATABASE_ROOT.child("Users").child(UID).child("notifications").child(key).setValue(null)
        }

        private fun initOnClick() {
            likeBtn.setOnClickListener {
                val story = stories[itemCount - 1-position]
                val key = stories[itemCount-1-position].id.toString()
                if (story.getLikesList().contains(AUTH.uid!!)) {
                    removeLikeFromStory(story) {
                        story.likes.remove(AUTH.uid!!)
                        setLikeIsNotClicked()
                        if (!story.from.equals(UID))
                            deleteNotification(position, key)
                        likesCount.text = story.likes.size.toString()
                    }
                } else {
                    putLikeToStory(story) {
                        story.likes[AUTH.uid!!] = ""
                        setLikeIsClicked()
                        if (!story.from.equals(UID))
                            sendNotification(position, key)
                        likesCount.text = story.likes.size.toString()
                    }
                }
            }
        }
    }

    init {
        this.stories = stories
        this.isProfileFr = isProfileFr
        inflater = LayoutInflater.from(context)
    }
}