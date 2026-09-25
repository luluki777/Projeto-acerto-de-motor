package com.exemplo.motortuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.exemplo.motortuner.ui.AppMotorTuner
import com.exemplo.motortuner.ui.theme.MotorTunerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotorTunerTheme {
                AppMotorTuner()
            }
        }
    }
}
