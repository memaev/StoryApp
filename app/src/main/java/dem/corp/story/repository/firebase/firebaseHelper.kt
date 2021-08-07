package dem.corp.story.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dem.corp.story.models.User


val DATABASE_ROOT = FirebaseDatabase.getInstance().reference
val STORAGE_ROOT = FirebaseStorage.getInstance().reference
val AUTH = FirebaseAuth.getInstance()
var UID = AUTH.currentUser?.uid ?: ""


lateinit var USER: User

const val NODE_STORIES = "Stories"
const val NODE_USERS = "Users"

const val CHILD_TITLE = "title"
const val CHILD_FROM = "from"
const val CHILD_TEXT = "text"
const val CHILD_DATE = "date"
const val CHILD_ID = "id"

const val CHILD_STORY_IMAGE = "imageUrl"

const val CHILD_MY_STORIES = "myStories"
const val CHILD_USERNAME = "username"
const val CHILD_PASSWORD = "password"
const val CHILD_EMAIL = "email"
const val CHILD_LIKES = "likes"
const val CHILD_BIO = "bio"

fun initFirebase() {

}