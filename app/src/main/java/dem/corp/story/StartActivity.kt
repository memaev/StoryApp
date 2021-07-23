package dem.corp.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dem.corp.story.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInBtn.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }

        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
    }
}