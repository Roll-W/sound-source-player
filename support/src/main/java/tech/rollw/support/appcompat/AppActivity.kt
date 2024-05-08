/*
 * Copyright (C) 2024 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.rollw.support.appcompat

import android.app.ActivityManager
import android.app.ActivityManager.AppTask
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import tech.rollw.support.Switch


/**
 * @author RollW
 */
abstract class AppActivity : AppCompatActivity() {
    private val localeDelegate: LocaleDelegate = LocaleDelegate()

    override fun onResume() {
        super.onResume()
        if (localeDelegate.isLocaleChanged) {
            Log.d("AppActivity", "Locale changed, recreate activity. new=${LocaleDelegate.defaultLocale}")

            ActivityCompat.recreate(this)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        newBase?.resources?.configuration?.let {
            localeDelegate.updateConfiguration(it)
        }

        super.attachBaseContext(newBase)
    }

    fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * @see AppCompatDelegate.setLocalNightMode
     */
    fun setLocalNightMode(state: Switch) {
        when (state) {
            Switch.ON -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            Switch.OFF -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            Switch.AUTO -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> {}
        }
    }

    /**
     * @see AppCompatDelegate.setDefaultNightMode
     */
    fun setDefaultNightMode(state: Switch) {
        when (state) {
            Switch.ON -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

            Switch.OFF -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )

            else -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        }
    }

    companion object {
        const val COLOR_DEFAULT = -1

        fun Context.getOrCreateActivityIntent(activity: Class<*>): Intent {
            val activityManager = getSystemService<ActivityManager>()
                ?: return Intent(this, activity)

            val appTasks = activityManager.appTasks
            for (appTask: AppTask in appTasks) {
                if (appTask.taskInfo.baseActivity == null) {
                    continue
                }
                if (appTask.taskInfo.baseActivity!!.className
                    == activity.canonicalName
                ) {
                    val resultIntent = Intent(
                        this,
                        Class.forName(appTask.taskInfo.topActivity!!.className)
                    )
                    resultIntent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    )
                    return resultIntent
                }
            }

            return Intent(this, activity)
        }
    }

    protected fun setNavigationBar(
        @ColorInt colorBackground: Int = COLOR_DEFAULT,
        fullScreen: Boolean = false,
        lightBar: Switch = Switch.AUTO
    ) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowCompat.setDecorFitsSystemWindows(window, fullScreen)

        if (colorBackground != COLOR_DEFAULT) {
            window.navigationBarColor = colorBackground
        }

        WindowCompat.getInsetsController(window, window.decorView).apply {
            setLightBars(colorBackground, lightBar)

            if (fullScreen) {
                hide(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }

    protected fun setStatusBar(
        @ColorInt colorBackground: Int = COLOR_DEFAULT,
        fullScreen: Boolean = false,
        lightBar: Switch = Switch.AUTO
    ) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowCompat.setDecorFitsSystemWindows(window, fullScreen)
        if (colorBackground != COLOR_DEFAULT) {
            window.statusBarColor = colorBackground
        }

        WindowCompat.getInsetsController(window, window.decorView).apply {
            setLightBars(colorBackground, lightBar)
            if (fullScreen) {
                hide(WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    private fun setLightBars(
        colorBackground: Int = COLOR_DEFAULT,
        lightStatusBar: Switch = Switch.NONE
    ) {
        val lightness = if (colorBackground == COLOR_DEFAULT) 0.0 else
            computeColorLightness(colorBackground)
        val lightBarState = when (lightStatusBar) {
            Switch.ON -> true
            Switch.NONE, Switch.OFF -> false
            Switch.AUTO -> lightness >= 0.5f
        }
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightBarState
            isAppearanceLightNavigationBars = lightBarState
        }
    }

    private fun computeColorLightness(@ColorInt color: Int): Double {
        if (color == Color.TRANSPARENT) {
            return 0.0
        }
        return ColorUtils.calculateLuminance(color)
    }

    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier(
            "status_bar_height", "dimen",
            "android"
        )
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    fun getNavigationBarHeight(): Int {
        val compat = WindowInsetsCompat.toWindowInsetsCompat(
            window.decorView.rootWindowInsets, window.decorView
        )
        val barInsets = compat.getInsets(
            WindowInsetsCompat.Type.navigationBars()
        )
        return barInsets.bottom - barInsets.top
    }

    private val activityResultLauncherMap: MutableMap<Class<out ActivityResultContract<*, *>?>,
            ActivityResultContractWrapper<*, *>> = hashMapOf()

    protected fun <I, O> registerActivityLauncher(contract: ActivityResultContract<I, O>) {
        val wrapper = ActivityResultContractWrapper(contract)
        activityResultLauncherMap[contract::class.java] =
            wrapper
    }

    fun <I, O> registerActivityResultCallback(
        clazz: Class<out ActivityResultContract<I, O>>,
        callback: ActivityResultCallback<O>,
        unregisterAfterCallback: Boolean = true
    ): ActivityResultLauncher<I> {
        val wrapper = activityResultLauncherMap[clazz] as ActivityResultContractWrapper<I, O>?
            ?: throw IllegalStateException("ActivityResultLauncher have not been registered.")
        wrapper.callbacks.registerOnActivityResultCallback(callback, unregisterAfterCallback)
        return wrapper.launcher
    }

    fun <I, O> unregisterActivityResultCallback(
        clazz: Class<out ActivityResultContract<I, O>>,
        callback: ActivityResultCallback<O>
    ) {
        val wrapper = activityResultLauncherMap[clazz] as ActivityResultContractWrapper<I, O>?
            ?: throw IllegalStateException("ActivityResultContract have not been registered.")
        wrapper.callbacks.unregisterOnActivityResultCallback(callback)
    }

    fun <I, O> getActivityResultLauncher(clazz: Class<out ActivityResultContract<I, O>?>): ActivityResultLauncher<I> {
        val wrapper = activityResultLauncherMap[clazz] as ActivityResultContractWrapper<I, O>?
            ?: throw IllegalStateException("ActivityResultContract have not been registered.")
        return wrapper.launcher
    }

    private class ActivityResultCallbacks<O> : ActivityResultCallback<O> {
        private val callbacks: MutableMap<ActivityResultCallback<O>, Boolean> = HashMap()

        fun registerOnActivityResultCallback(callback: ActivityResultCallback<O>?, unregisterAfterCallback: Boolean) {
            if (callback == null) {
                return
            }
            callbacks[callback] = unregisterAfterCallback
        }

        fun unregisterOnActivityResultCallback(callback: ActivityResultCallback<O>?) {
            if (callback == null) {
                return
            }
            callbacks.remove(callback)
        }

        override fun onActivityResult(result: O) {
            val it = callbacks.keys.iterator()
            while (it.hasNext()) {
                val callback = it.next()
                callback.onActivityResult(result)
                if (callbacks[callback] == true) {
                    it.remove()
                }
            }
        }
    }

    private inner class ActivityResultContractWrapper<I, O>(
        contract: ActivityResultContract<I, O>
    ) {
        val callbacks: ActivityResultCallbacks<O> = ActivityResultCallbacks()
        val launcher: ActivityResultLauncher<I> =
            this@AppActivity.registerForActivityResult(contract, callbacks)
    }
}