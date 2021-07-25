package dem.corp.story.application

import android.app.Application

class App: Application() {

    companion object{
        private lateinit var app: App
        fun getInstance(): App {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}