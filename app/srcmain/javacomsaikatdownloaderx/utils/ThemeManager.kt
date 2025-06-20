package com.saikat.downloaderx.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.saikat.downloaderx.R

object ThemeManager {
    
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_AMOLED = "amoled"
    const val THEME_RETRO_GREEN = "retro_green"
    const val THEME_HACKER_NEON = "hacker_neon"
    
    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val theme = prefs.getString("selected_theme", THEME_DARK) ?: THEME_DARK
        
        when (theme) {
            THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                context.setTheme(R.style.Theme_DownloaderX_Light)
            }
            THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                context.setTheme(R.style.Theme_DownloaderX_Dark)
            }
            THEME_AMOLED -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                context.setTheme(R.style.Theme_DownloaderX_AMOLED)
            }
            THEME_RETRO_GREEN -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                context.setTheme(R.style.Theme_DownloaderX_RetroGreen)
            }
            THEME_HACKER_NEON -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                context.setTheme(R.style.Theme_DownloaderX_HackerNeon)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                context.setTheme(R.style.Theme_DownloaderX)
            }
        }
    }
    
    fun setTheme(context: Context, themeName: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_theme", themeName).apply()
        applyTheme(context)
    }
    
    fun getThemeResourceId(theme: String): Int {
        return when (theme) {
            THEME_LIGHT -> R.style.Theme_DownloaderX_Light
            THEME_DARK -> R.style.Theme_DownloaderX_Dark
            THEME_AMOLED -> R.style.Theme_DownloaderX_AMOLED
            THEME_RETRO_GREEN -> R.style.Theme_DownloaderX_RetroGreen
            THEME_HACKER_NEON -> R.style.Theme_DownloaderX_HackerNeon
            else -> R.style.Theme_DownloaderX
        }
    }
}