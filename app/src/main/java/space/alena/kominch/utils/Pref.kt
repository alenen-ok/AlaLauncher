package space.alena.kominch.utils

abstract class Pref {
    companion object {
        const val WALLPAPER_SOURCE = "wallpaperSource"
        const val WALLPAPER_UPDATE_FREQUENCY = "wallpaperUpdateFrequency"
    }

    abstract class WallpapperSource {
        companion object {
            const val sourse1 = "https://picsum.photos/800/1600"
            const val source2 = "https://loremflickr.com/800/1600"
            const val source3 = "https://placeimg.com/800/1600/any"
            const val source4 = "https://source.unsplash.com/random/800x1600"
        }
    }

    abstract class WallpaperUpdateFrequency {
        companion object {
            const val frequency1 = "15"
            const val frequency2 = "60"
            const val frequency3 = "480"
            const val frequency4 = "1440"
        }
    }
}
