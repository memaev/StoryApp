package dem.corp.story.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dem.corp.story.models.User


val AUTH = FirebaseAuth.getInstance()
val DATABASE_ROOT = FirebaseDatabase.getInstance().reference
var UID = AUTH.currentUser?.uid?:""

lateinit var USER: User

const val NODE_USERS = "Users"
const val NODE_STORIES = "Stories"

const val CHILD_FROM = "from"
const val CHILD_TEXT = "text"
const val CHILD_TITLE = "title"
const val CHILD_ID = "id"

const val CHILD_BIO = "bio"
const val CHILD_EMAIL = "email"
const val CHILD_MY_STORIES = "myStories"
const val CHILD_PASSWORD = "password"
const val CHILD_USERNAME = "username"
const val CHILD_LIKES = "likes"

fun initFirebase(){

}