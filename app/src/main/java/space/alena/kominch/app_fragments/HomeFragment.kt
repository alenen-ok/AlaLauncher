package space.alena.kominch.app_fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.room.Room
import kotlinx.android.synthetic.main.fragment_home.*

import space.alena.kominch.R
import space.alena.kominch.activities.ApplicationListActivity.Companion.adapterHome
import space.alena.kominch.adapters.ChangeHomeListener
import space.alena.kominch.adapters.ItemTouchHelperCallback
import space.alena.kominch.provider.AppDatabase

class HomeFragment: Fragment() {
    lateinit var contextActivity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            contextActivity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val callback = ItemTouchHelperCallback(adapterHome)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rv_name)
        rv_name.adapter = adapterHome
        rv_name.layoutManager = GridLayoutManager(activity, 4)
        fab_plus.setOnClickListener {
            (contextActivity as ChangeHomeListener).addWebsite()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}
/*class HomeFragment : Fragment(), DragDropPresenter,View.OnLongClickListener, View.OnClickListener {

    private lateinit var myDB: AppDatabase
    private lateinit var mDeleteZone: DeleteZone
    private lateinit var gridView: GridView
    private var mAppCount: Int = 0
    private var mLastNewCell: AppCell? = null
    private lateinit var adapter: AppCellAdapter
    private lateinit var mVibrator: Vibrator
    private val VIBRATE_DURATION = 35L
    lateinit var contextActivity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            contextActivity = context
        }
        myDB = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "kominch.db"
        ).build()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mVibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        ApplicationListActivity.mDragController = DragController(this)
        adapter = AppCellAdapter(activity!!, ApplicationListActivity.mDragController)
        gridView = activity!!.findViewById<GridView>(R.id.image_grid_view)
        gridView.adapter = adapter
        mDeleteZone = activity!!.findViewById(R.id.delete_zone_view)
        mDeleteZone.setOnDragListener(ApplicationListActivity.mDragController)
        btn_add_website.setOnClickListener(::onClickAddImage)
        super.onViewCreated(view, savedInstanceState)
    }

    fun onClickAddImage(view: View) {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.add_dialog)
        dialog.setTitle("Add website to workspace")
        dialog.setCancelable(true)
        dialog.show()
        val btn = dialog.findViewById<Button>(R.id.btn_add)
        btn.setOnClickListener{
            val name: String = dialog.findViewById<EditText>(R.id.editText).text.toString()
            addNewImageToScreen(name)
            dialog.cancel()
        }
    }
    fun addNewImageToScreen(name: String){
        if(mLastNewCell != null) mLastNewCell!!.visibility = View.GONE
        adapter.items[0] = AppCell(activity!!)
        adapter.notifyDataSetChanged()
        mAppCount++

    }

    override fun isDragDropEnabled(): Boolean = true

    override fun onDragStarted(source: DragSource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            mVibrator.vibrate(VIBRATE_DURATION)
        }
    }

    override fun onDropCompleted(target: DropTarget, success: Boolean) {

    }
    override fun onLongClick(v: View?): Boolean {
        Log.e("as", "qwwwwwwwwwwwwwwwwwwwwwwwwwwww")
        if(!v!!.isInTouchMode)
            return false
        return startDrag(v)
    }
    fun startDrag(v: View): Boolean {
        v.setOnDragListener(ApplicationListActivity.mDragController)
        ApplicationListActivity.mDragController.startDrag(v)
        return true
    }
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
*/