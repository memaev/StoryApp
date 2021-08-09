package dem.corp.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import dem.corp.story.repository.firebase.AUTH

class SplashScreen : AppCompatActivity() {
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({

            if (AUTH.currentUser== null){
                val intent = Intent(this,StartActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }


        } , 3000)
    }
}