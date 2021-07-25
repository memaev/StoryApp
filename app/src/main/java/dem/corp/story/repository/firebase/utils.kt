package dem.corp.story.repository.firebase

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dem.corp.story.MainActivity
import dem.corp.story.application.App
import dem.corp.story.models.User
import kotlin.math.tan

fun registerUser(user: User, onSuccess: () -> Unit) {
    AUTH.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            USER = user.also { it.id = AUTH.currentUser!!.uid }
            addUserToDatabase(user, onSuccess)
        } else {
            task.exception?.printStackTrace()
        }
    }
}

fun addUserToDatabase(user: User, onSuccess: () -> Unit) {
    DATABASE_ROOT.child(NODE_USERS).child(USER.id).updateChildren(user.asHashMap()).addOnCompleteListener {
        if (it.isSuccessful){
            onSuccess()
        }else{
            it.exception?.printStackTrace()
        }
    }



//    FirebaseDatabase.getInstance().reference.child("Users").child(
//        FirebaseAuth.getInstance().currentUser!!.uid
//    )
//        .child("username")
//        .setValue(binding.usernameEdit.getText().toString())
//    FirebaseDatabase.getInstance().reference.child("Users").child(
//        FirebaseAuth.getInstance().currentUser!!.uid
//    )
//        .child("email").setValue(binding.emailEdit.getText().toString())
//    FirebaseDatabase.getInstance().reference.child("Users").child(
//        FirebaseAuth.getInstance().currentUser!!.uid
//    )
//        .child("password")
//        .setValue(binding.passwordEdit.getText().toString())
//    FirebaseDatabase.getInstance().reference.child("Users").child(
//        FirebaseAuth.getInstance().currentUser!!.uid
//    )
//        .child("myStories").setValue(0)
//    FirebaseDatabase.getInstance().reference.child("Users").child(
//        FirebaseAuth.getInstance().currentUser!!.uid
//    )
//        .child("bio").setValue(binding.bioEdit.text.toString())
}

