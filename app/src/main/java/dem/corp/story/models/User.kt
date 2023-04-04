package dem.corp.story.models

import dem.corp.story.repository.firebase.*

data class User(
                var id: String = "",
                var username: String,
                val email: String,
                var bio: String,
                val password: String,
                var likes: Long = 0,
                var storiesCount: Long = 0,
                var myStories: ArrayList<Story> = ArrayList()
){
    fun asHashMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()

        map[CHILD_EMAIL] = this.email
        map[CHILD_USERNAME] = this.username
        map[CHILD_BIO] = this.bio
        map[CHILD_PASSWORD] = this.password
        map[CHILD_LIKES] = this.likes
        map[CHILD_STORIES_COUNT] = this.storiesCount
        map[CHILD_MY_STORIES] = this.storiesToHashMap()

        return map
    }

    private fun storiesToHashMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in this.myStories){
            map[i.id] = i.asHashMap()
        }
        return map
    }
}
