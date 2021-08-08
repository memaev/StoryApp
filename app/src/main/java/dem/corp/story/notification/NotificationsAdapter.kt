 package dem.corp.story.notification

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dem.corp.story.CommentActivity
import dem.corp.story.R
import dem.corp.story.models.Notification
import dem.corp.story.repository.firebase.DATABASE_ROOT
import dem.corp.story.repository.firebase.NODE_USERS
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


 class NotificationsAdapter internal constructor(context: Context?, notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private val notifications: List<Notification>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Notification = notifications[position]
        if (model.type.equals("like")){
            var e = true
            DATABASE_ROOT.child("Stories").child(model.storyID.toString()).child("title")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot2: DataSnapshot) {
                        if (e){
                            var storyTitle = snapshot2.value.toString()
                            var username = model.username
                            holder.text.text = "$username liked your story: $storyTitle"
                            holder.date.text = model.date.toString()
                            e = false
                            holder.comment.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            holder.itemView.setOnClickListener{
                //create an alert builder for dialog
                val builder = AlertDialog.Builder(holder.itemView.context)

                //set the custom dialog layout
                val customLayout: View =
                    inflater.inflate(R.layout.story_item_notifications, null)
                customLayout.setBackgroundResource(R.drawable.story_item_shape)
                builder.setView(customLayout)

                val dialog = Dialog(holder.itemView.context)
                dialog.setContentView(customLayout)
                dialog.window!!.setBackgroundDrawableResource(R.drawable.story_item_shape)

                val storyTitle = dialog.findViewById<TextView>(R.id.story_title)
                val storyDate = dialog.findViewById<TextView>(R.id.tv_story_date)
                val storyComment = dialog.findViewById<ImageButton>(R.id.comment_btn)
                val storyRead = dialog.findViewById<ImageButton>(R.id.read_more)

                var k = true
                DATABASE_ROOT.child("Stories").child(model.storyID.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot2: DataSnapshot) {
                            if (k){
                                val date = snapshot2.child("date").value.toString()
                                val title = snapshot2.child("title").value.toString()

                                storyDate!!.text = if (date != "") {
                                    val firstFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")
                                    var period = Period.between(
                                        LocalDate.parse(date, firstFormatter),
                                        LocalDate.parse(
                                            SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date()),
                                            firstFormatter
                                        )
                                    )
                                    if (period.months != 0) period.months.toString() + " months ago"
                                    else if (period.days != 0) period.days.toString() + " days ago"
                                    else "today"
                                } else "today"

                                storyTitle!!.text = title
                                dialog.show()

                                k = false
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                storyRead.setOnClickListener {
                    val dialog = Dialog(it.context)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.read_story_bottom_sheet)

                    val storyText = dialog.findViewById<TextView>(R.id.story_txt)
                    val storyTitle = dialog.findViewById<TextView>(R.id.story_title)
                    val storyDate = dialog.findViewById<TextView>(R.id.tv_btm_shed_date)
                    val storyFrom = dialog.findViewById<TextView>(R.id.story_from)
                    val storyClose = dialog.findViewById<ImageButton>(R.id.dialog_close)

                    dialog.show()
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                    dialog.window?.setGravity(Gravity.BOTTOM)

                    storyClose.setOnClickListener {
                        dialog.dismiss()
                    }

                    val key = model.storyID.toString()
                    var e = true
                    FirebaseDatabase.getInstance().getReference("Stories").child(key)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (e) {
                                    val text = snapshot.child("text").value.toString()
                                    val title = snapshot.child("title").value.toString()
                                    val date = snapshot.child("date").value.toString()
                                    val from = snapshot.child("from").value.toString()
                                    DATABASE_ROOT.child(NODE_USERS).child(from).child("username").addValueEventListener(object: ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            storyFrom!!.text = snapshot.value.toString()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
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

                storyComment.setOnClickListener {
                    var l = true
                    DATABASE_ROOT.child("Stories").child(model.storyID.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if (l){
                                    val title = snapshot2.child("title").value.toString()
                                    val intent = Intent(it.context, CommentActivity::class.java)
                                    intent.putExtra("storyTitle", title)
                                    intent.putExtra("storyID", model.storyID)
                                    intent.putExtra("fromNot", 1)
                                    intent.putExtra("storyFrom", model.from)
                                    it.context.startActivity(intent)
                                    Log.d("comment story log", "comment btn clicked")
                                    l = false
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }
        }else{
            var e = true
            DATABASE_ROOT.child("Stories").child(model.storyID.toString()).child("title")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot2: DataSnapshot) {
                        if (e){
                            var storyTitle = snapshot2.value.toString()
                            var username = model.username
                            holder.text.text = "$username commented your story: $storyTitle"
                            holder.date.text = model.date.toString()
                            e = false
                            holder.comment.visibility = View.VISIBLE
                            holder.comment.text = "See comment"

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            holder.itemView.setOnClickListener{
                //create an alert builder for dialog
                val builder = AlertDialog.Builder(holder.itemView.context)

                //set the custom dialog layout
                val customLayout: View =
                    inflater.inflate(R.layout.story_item_notifications, null)
                customLayout.setBackgroundResource(R.drawable.story_item_shape)
                builder.setView(customLayout)

                val dialog = Dialog(holder.itemView.context)
                dialog.setContentView(customLayout)
                dialog.window!!.setBackgroundDrawableResource(R.drawable.story_item_shape)

                val storyTitle = dialog.findViewById<TextView>(R.id.story_title)
                val storyDate = dialog.findViewById<TextView>(R.id.tv_story_date)
                val storyComment = dialog.findViewById<ImageButton>(R.id.comment_btn)
                val storyRead = dialog.findViewById<ImageButton>(R.id.read_more)

                var k = true
                DATABASE_ROOT.child("Stories").child(model.storyID.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot2: DataSnapshot) {
                            if (k){
                                val date = snapshot2.child("date").value.toString()
                                val title = snapshot2.child("title").value.toString()

                                storyDate!!.text = if (date != "") {
                                    val firstFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")
                                    var period = Period.between(
                                        LocalDate.parse(date, firstFormatter),
                                        LocalDate.parse(
                                            SimpleDateFormat("dd.MM.yyyy hh:mm").format(Date()),
                                            firstFormatter
                                        )
                                    )
                                    if (period.months != 0) period.months.toString() + " months ago"
                                    else if (period.days != 0) period.days.toString() + " days ago"
                                    else "today"
                                } else "today"

                                storyTitle!!.text = title
                                dialog.show()

                                k = false
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                storyRead.setOnClickListener {
                    Log.d("comment story log", "read story btn clicked")
                }

                storyComment.setOnClickListener {
                    var l = true
                    DATABASE_ROOT.child("Stories").child(model.storyID.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if (l){
                                    val title = snapshot2.child("title").value.toString()
                                    val intent = Intent(it.context, CommentActivity::class.java)
                                    intent.putExtra("storyTitle", title)
                                    intent.putExtra("storyID", model.storyID)
                                    intent.putExtra("fromNot", 1)
                                    it.context.startActivity(intent)
                                    Log.d("comment story log", "comment btn clicked")
                                    l = false
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            holder.comment.setOnClickListener {
                // create an alert builder

                // create an alert builder
                val builder = AlertDialog.Builder(holder.itemView.context)

                // set the custom dialog layout

                // set the custom dialog layout
                val customLayout: View =
                    inflater.inflate(R.layout.item_comment, null)
                customLayout.setBackgroundResource(R.drawable.create_story_dialog_shape)
                builder.setView(customLayout)

                val dialog = Dialog(holder.itemView.context)

                dialog.setContentView(customLayout)
                dialog.window!!.setBackgroundDrawableResource(R.drawable.create_story_dialog_shape)

                val username = customLayout.findViewById<TextView>(R.id.username)
                val date = customLayout.findViewById<TextView>(R.id.time_and_data)
                val text = customLayout.findViewById<TextView>(R.id.text2s)

                username.text = model.username
                date.text = model.date
                text.text = model.text

                dialog.show()
            }
        }
//        holder.type.text = model.type
//        holder.date.text = model.date
//        holder.from.text = model.username
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView
        var date: TextView
        var comment: TextView

        init {
            text = view.findViewById(R.id.notificationText)
            date = view.findViewById(R.id.notificationDate)
            comment = view.findViewById(R.id.notificationCommentName)
        }
    }

    init {
        this.notifications = notifications
        inflater = LayoutInflater.from(context)
    }
}