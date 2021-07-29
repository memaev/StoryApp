package dem.corp.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast
import dem.corp.story.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            if (binding.emailEdit.text.toString().isEmpty() || binding.passwordEdit.text
                    .toString().isEmpty()
            ) {
                FancyToast.makeText(this,"Fields cannot be empty"
                    , FancyToast.LENGTH_LONG, FancyToast.ERROR,true);
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEdit.text.toString(), binding.passwordEdit.text.toString())
                    .addOnCompleteListener{
                        Log.d("login", "successfully logined")
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
            }
        }
    }
}