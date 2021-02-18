package space.alena.kominch.app_fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.yandex.metrica.YandexMetrica

class DialogFragment(val s: Int) : AppCompatDialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        YandexMetrica.reportEvent("Show frequency dialog")
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Частота")
        builder.setMessage("Количество запусков этого приложения: $s")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int -> }
        return builder.create()
    }
}