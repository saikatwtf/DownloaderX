package com.saikat.downloaderx.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.saikat.downloaderx.MainActivity
import com.saikat.downloaderx.R

class SplashActivity : AppCompatActivity() {
    
    private lateinit var lottieAnimation: LottieAnimationView
    private lateinit var spiderView: ImageView
    private lateinit var batView: ImageView
    private lateinit var scorpionView: ImageView
    private lateinit var compassView: ImageView
    private lateinit var magnifierView: ImageView
    private var mediaPlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        initViews()
        startAnimations()
        
        // Navigate to main activity after 4 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }
    
    private fun initViews() {
        lottieAnimation = findViewById(R.id.lottie_animation)
        spiderView = findViewById(R.id.spider_view)
        batView = findViewById(R.id.bat_view)
        scorpionView = findViewById(R.id.scorpion_view)
        compassView = findViewById(R.id.compass_view)
        magnifierView = findViewById(R.id.magnifier_view)
        
        // Play ambient horror sound
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.horror_ambient)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun startAnimations() {
        // Start Lottie animation
        lottieAnimation.playAnimation()
        
        // Animate spider crawling
        val spiderAnimator = ObjectAnimator.ofFloat(spiderView, "translationX", -200f, 200f)
        spiderAnimator.duration = 2000
        spiderAnimator.interpolator = AccelerateDecelerateInterpolator()
        
        // Animate bat flying
        val batAnimatorX = ObjectAnimator.ofFloat(batView, "translationX", -300f, 300f)
        val batAnimatorY = ObjectAnimator.ofFloat(batView, "translationY", -50f, 50f)
        val batSet = AnimatorSet()
        batSet.playTogether(batAnimatorX, batAnimatorY)
        batSet.duration = 3000
        
        // Animate scorpion tail creeping
        val scorpionAnimator = ObjectAnimator.ofFloat(scorpionView, "translationY", 100f, -50f)
        scorpionAnimator.duration = 1500
        scorpionAnimator.startDelay = 500
        
        // Animate compass rotating
        val compassAnimator = ObjectAnimator.ofFloat(compassView, "rotation", 0f, 360f)
        compassAnimator.duration = 4000
        compassAnimator.repeatCount = ObjectAnimator.INFINITE
        
        // Animate magnifier inspecting
        val magnifierAnimator = ObjectAnimator.ofFloat(magnifierView, "scaleX", 1f, 1.5f, 1f)
        val magnifierAnimatorY = ObjectAnimator.ofFloat(magnifierView, "scaleY", 1f, 1.5f, 1f)
        val magnifierSet = AnimatorSet()
        magnifierSet.playTogether(magnifierAnimator, magnifierAnimatorY)
        magnifierSet.duration = 2000
        magnifierSet.repeatCount = 1
        
        // Start all animations
        spiderAnimator.start()
        batSet.start()
        scorpionAnimator.start()
        compassAnimator.start()
        magnifierSet.start()
        
        // Fade in mysterious symbols
        val symbolsView = findViewById<View>(R.id.symbols_view)
        val symbolsAnimator = ObjectAnimator.ofFloat(symbolsView, "alpha", 0f, 0.3f, 0f)
        symbolsAnimator.duration = 3000
        symbolsAnimator.startDelay = 1000
        symbolsAnimator.start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}