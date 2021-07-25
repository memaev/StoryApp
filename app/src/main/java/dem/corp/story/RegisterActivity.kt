package dem.corp.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import dem.corp.story.databinding.ActivityRegisterBinding
import dem.corp.story.databinding.ActivityStartBinding

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.registerBtn.setOnClickListener(View.OnClickListener {
            if (binding.emailEdit.text.toString().isEmpty() || binding.passwordEdit.text
                    .toString().isEmpty() || binding.usernameEdit.text.toString().isEmpty()
                || binding.bioEdit.text.toString().isEmpty()
            ) {
                FancyToast.makeText(this,"Fields cannot be empty"
                    ,FancyToast.LENGTH_LONG,FancyToast.ERROR,true);
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailEdit.getText().toString(),
                    binding.passwordEdit.getText().toString()
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .child("username")
                                .setValue(binding.usernameEdit.getText().toString())
                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .child("email").setValue(binding.emailEdit.getText().toString())
                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .child("password")
                                .setValue(binding.passwordEdit.getText().toString())
                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .child("myStories").setValue(0)
                            FirebaseDatabase.getInstance().reference.child("Users").child(
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                                .child("bio").setValue(binding.bioEdit.text.toString())
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        }
                    }
            }
        })
    }
}