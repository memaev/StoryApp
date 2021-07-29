package dem.corp.story.comment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dem.corp.story.R
import dem.corp.story.models.Comment

internal class CommentsAdapter(options: FirebaseRecyclerOptions<Comment>) :
    FirebaseRecyclerAdapter<Comment, CommentsAdapter.viewholder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsAdapter.viewholder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        Log.d("j", "Adapter started")
        return CommentsAdapter.viewholder(view)
    }

    override fun onBindViewHolder(
        holder: CommentsAdapter.viewholder,
        i: Int,
        model: Comment
    ) {
        holder.date.text = model.date
        holder.text.text = model.text
        holder.from.text = model.username
    }


    internal class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView
        var from: TextView
        var date : TextView

        init {
            text = itemView.findViewById(R.id.text2s)
            from = itemView.findViewById(R.id.username)
            date = itemView.findViewById(R.id.time_and_data)
        }
    }

}