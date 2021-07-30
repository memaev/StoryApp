package dem.corp.story.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dem.corp.story.R
import dem.corp.story.models.Comment

class CommentsAdapter internal constructor(context: Context?, comments: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private val comments: List<Comment>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Comment = comments[position]
        holder.text.text = model.text
        holder.data.text = model.date
        holder.from.text = model.username
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView
        var data: TextView
        var from: TextView

        init {
            text = view.findViewById(R.id.text2s)
            data = view.findViewById(R.id.time_and_data)
            from = view.findViewById(R.id.username)
        }
    }

    init {
        this.comments = comments
        inflater = LayoutInflater.from(context)
    }
}
