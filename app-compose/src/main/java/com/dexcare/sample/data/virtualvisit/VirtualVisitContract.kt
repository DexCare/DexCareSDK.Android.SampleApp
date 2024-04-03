package com.dexcare.sample.data.virtualvisit

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class VirtualVisitContract {

    class LaunchVisit: ActivityResultContract<Intent,Int>(){
        override fun createIntent(context: Context, input: Intent): Intent {
            return input
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
           return resultCode
        }

    }
}
