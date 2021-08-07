package dem.corp.story.repository.firebase

import android.net.Uri
import dem.corp.story.models.Story
import dem.corp.story.models.User
import kotlinx.coroutines.tasks.await
import java.lang.Exception


fun registerUser(user: User, onSuccess: () -> Unit) {
    AUTH.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            UID = AUTH.currentUser!!.uid
            USER = user.also { it.id = UID }
            addUserToDatabase(user, onSuccess)
        } else {
            task.exception?.printStackTrace()
        }
    }
}


private fun addUserToDatabase(user: User, onSuccess: () -> Unit) {
    DATABASE_ROOT.child(NODE_USERS).child(USER.id).updateChildren(user.asHashMap())
        .addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                it.exception?.printStackTrace()
            }
        }
}


fun createStory(story: Story, uri: Uri?) {


    DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                story.id = DATABASE_ROOT.child(NODE_STORIES).push().key!!
                story.from = task.result!!.value.toString()
                addStory(story, uri)
            } else {
                task.exception?.printStackTrace()
            }
        }
}


private fun addStory(story: Story, uri: Uri?) {
    val add = {
        DATABASE_ROOT.child(NODE_STORIES).child(story.id)
            .updateChildren(story.asHashMap() as Map<String, Any>).addOnCompleteListener {
                DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_MY_STORIES).child(story.id)
                    .updateChildren(story.asHashMap() as Map<String, Any>).addOnCompleteListener {

                    }
            }
    }

    if (uri != null) {
        val path = STORAGE_ROOT.child(CHILD_STORY_IMAGE).child(story.id)
        path.putFile(uri!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                path.downloadUrl.addOnCompleteListener { url ->
                    story.imageUrl = url.result.toString()
                    add()
                }
            }
        }
    } else {
        add()
    }
}

fun putLikeToStory(story: Story, onSuccess: () -> Unit = {}) {
    DATABASE_ROOT.child(NODE_USERS).child(story.from).child(CHILD_MY_STORIES).child(story.id)
        .child(CHILD_LIKES).child(AUTH.uid!!).setValue("")
    DATABASE_ROOT.child(NODE_STORIES).child(story.id).child(CHILD_LIKES).child(AUTH.uid!!)
        .setValue("").addOnSuccessListener {
            val likeRef = DATABASE_ROOT.child(NODE_USERS).child(story.from).child(CHILD_LIKES)
            likeRef.get().addOnSuccessListener {
                val likeCount = (try {
                    it.value.toString().toInt()
                } catch (e: Exception) {
                    0
                } + 1)
                likeRef.setValue(likeCount).addOnSuccessListener {
                    onSuccess()
                }
            }
        }
}

fun removeLikeFromStory(story: Story, onSuccess: () -> Unit = {}) {
    DATABASE_ROOT.child(NODE_USERS).child(story.from).child(CHILD_MY_STORIES).child(story.id)
        .child(CHILD_LIKES).child(AUTH.uid!!).removeValue()
    DATABASE_ROOT.child(NODE_STORIES).child(story.id).child(CHILD_LIKES).child(AUTH.uid!!)
        .removeValue().addOnSuccessListener {
            val likeRef = DATABASE_ROOT.child(NODE_USERS).child(story.from).child(CHILD_LIKES)
            likeRef.get().addOnSuccessListener {
                val likeCount = (try {
                    it.value.toString().toInt()
                } catch (e: Exception) {
                    e.printStackTrace(); 0
                } - 1)
                likeRef.setValue(likeCount).addOnSuccessListener {
                    onSuccess()
                }
            }
        }
}



