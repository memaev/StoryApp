package dem.corp.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import dem.corp.story.databinding.ActivityRegisterBinding
import dem.corp.story.models.User
import dem.corp.story.repository.firebase.AUTH
import dem.corp.story.repository.firebase.registerUser

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initOnClicks()
    }

    private fun initOnClicks() {
        binding.registerBtn.setOnClickListener {
            if (binding.emailEdit.text.toString().isEmpty() || binding.passwordEdit.text
                    .toString().isEmpty() || binding.usernameEdit.text.toString().isEmpty()
                || binding.bioEdit.text.toString().isEmpty()
            ) {
                FancyToast
                    .makeText(this, "Fields cannot be empty", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show()
            } else {
                registerUser()
            }
        }
    }

    private fun registerUser(){
        val user = User(
            username = binding.usernameEdit.text.toString(),
            email = binding.emailEdit.text.toString(),
            bio = binding.bioEdit.text.toString(),
            password = binding.passwordEdit.text.toString()
        )
        registerUser(user) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}