package com.fdhasna21.fond

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.fdhasna21.fond.base.BaseActivity
import com.fdhasna21.fond.databinding.ActivityLauncherBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

class LauncherActivity : BaseActivity<ActivityLauncherBinding>(ActivityLauncherBinding::inflate){

    private var isSplashVisible = true

    override fun setupData() {
        //add permissions
    }

    override fun setupUI() {
        lifecycleScope.launch {
            lifecycleScope.launch {
                delay(3000)
                openMainActivity()
            }
        }
    }

    private fun openMainActivity() {
        val mainIntent = Intent(this@LauncherActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
        isSplashVisible = false
    }
}