package space.alena.kominch.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.content.res.ResourcesCompat
import space.alena.kominch.R
import space.alena.kominch.app.MyApp
import kotlin.math.abs

abstract class GestureListener():GestureDetector.SimpleOnGestureListener() {
    companion object{
        private val SWIPE_MIN_DISTANCE = MyApp.appContext.resources.getDimension(R.dimen.swipe_distance)
        private const val SWIPE_THRESHOLD_VELOCITY = 400
    }
    abstract fun onSwipeUp(): Boolean
    abstract fun onSwipeDown(): Boolean
    abstract fun onSwipeRight(): Boolean
    abstract fun onSwipeLeft():Boolean

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || e2 == null)
            return false
        return if(e1.y - e2.y > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeUp() // снизу вверх
        } else if (e2.y - e1.y > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeDown()
        } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeRight()
        } else if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            onSwipeLeft()
        } else {
            false
        }
    }
}