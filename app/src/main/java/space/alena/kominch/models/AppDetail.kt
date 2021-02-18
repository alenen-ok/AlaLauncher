package space.alena.kominch.models

import android.graphics.drawable.Drawable

data class AppDetail(
        var label: String,
        var name: String,
        val icon: Drawable?,
        val installationTime: Long) {
    override fun equals(other: Any?): Boolean {
        return when(other){
            is AppDetail -> this.label == other.label && this.name == other.name
            else -> false
        }
    }
}