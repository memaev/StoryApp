package dem.corp.story.repository.firebase

import dem.corp.story.models.Story
import dem.corp.story.models.User
import kotlinx.coroutines.tasks.await


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
    DATABASE_ROOT.child(NODE_USERS).child(USER.id).updateChildren(user.asHashMap()).addOnCompleteListener {
        if (it.isSuccessful){
            onSuccess()
        }else{
            it.exception?.printStackTrace()
        }
    }
}


fun createStory(story: Story){
    DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            story.id = DATABASE_ROOT.child(NODE_STORIES).push().key!!
            story.from = task.result!!.value.toString()
            addStory(story)
        } else {
            task.exception?.printStackTrace()
        }
    }
}


private fun addStory(story: Story) {
    DATABASE_ROOT.child(NODE_STORIES).child(story.id)
      .updateChildren(story.asHashMap() as Map<String, Any>).addOnCompleteListener {
        DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_MY_STORIES).child(story.id)
          .updateChildren(story.asHashMap() as Map<String, Any>).addOnCompleteListener {

            }
      }
}



