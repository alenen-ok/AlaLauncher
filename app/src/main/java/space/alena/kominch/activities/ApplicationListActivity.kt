package space.alena.kominch.activities

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_application_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.alena.kominch.R
import space.alena.kominch.adapters.ChangeActivityManager
import space.alena.kominch.adapters.ChangeHomeListener
import space.alena.kominch.adapters.HomeScreenAdapter
import space.alena.kominch.adapters.MyListAdapter
import space.alena.kominch.app_fragments.DialogFragment
import space.alena.kominch.models.AppDetail
import space.alena.kominch.utils.Pref
import space.alena.kominch.provider.App
import space.alena.kominch.provider.AppDatabase
import space.alena.kominch.provider.HomeApp
import space.alena.kominch.service.Background
import space.alena.kominch.service.BackgroundLoaderService
import space.alena.kominch.utils.GestureListener
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ApplicationListActivity : AppCompatActivity(), ChangeActivityManager, ChangeHomeListener {



    companion object {
        val originListApps : ArrayList<Pair<AppDetail, Int>> = ArrayList()
        val adapter = MyListAdapter()
        lateinit var adapterHome: HomeScreenAdapter
    }
    private val CODE_REQUEST_INTERNET = 1000
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var mMyReceiver: BroadcastReceiver
    private lateinit var myDB: AppDatabase
    private lateinit var prefs: SharedPreferences
    private lateinit var swipeListener: GestureListener
    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            YandexMetrica.reportEvent("Landscape mode", HashMap())
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (prefs.getBoolean(getString(R.string.id_openmode_pref), true)) {
            prefs.edit().putBoolean(getString(R.string.id_openmode_pref), false).apply()
            val intent = Intent(this, WelcomePageActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(prefs.getBoolean(getString(R.string.id_theme_pref), false))
            setTheme(R.style.AppDarkThemes)
        updateWallpaper()
        setContentView(R.layout.activity_application_list)
        myDB = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "kominch.db"
        ).build()
        if(savedInstanceState == null) {
            firstStart()
            adapterHome = HomeScreenAdapter(this)
        }
        adapterHome.onItemClick=::onHomeItemClick
        adapter.onItemClick=:: onItemClick
        adapter.onItemLongClick=::onLongItemClick
        adapterHome.onDeleteClick=::deleteClick
        /*Thread {
            logRecords(myDB.appDao().getAll())
        }.start()*/
        if(savedInstanceState == null) {
            val t = Thread{
                loadData(myDB.homeDao().loadAllApps())
            }
            t.start()
            t.join()
        }
        swipeListener = makeSwipeListener()
        gestureDetector = GestureDetectorCompat(this, swipeListener)
        mMyReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent!!.action){
                    Intent.ACTION_PACKAGE_ADDED -> {
                        val packageName = intent.data.schemeSpecificPart
                        originListApps.add(AppDetail(packageManager.getApplicationLabel(ApplicationInfo(packageManager.getApplicationInfo(packageName, 0))) as String,
                                packageName, packageManager.getApplicationIcon(packageName), packageManager.getPackageInfo(packageName, 0).firstInstallTime) to 0)
                        adapter.submitList(originListApps)
                        applySortForAdapter()
                    }
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        val pack = intent.data.encodedSchemeSpecificPart
                        removeElemByName(pack)
                        val pos = findHomePositionByName(pack)
                        if(pos != -1)
                            deleteApp(pos)
                        adapterHome.notifyDataSetChanged()
                        applySortForAdapter()
                    }
                    BackgroundLoaderService.BROADCAST_ACTION_UPDATE_WALLPAPERS -> updateWallpaper()
                }
            }
        }
        registerMyReceiver()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.gridFragment, R.id.linearFragment, R.id.homeFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        createNotificationChannel ()
        val btn_send = findViewById<Button>(R.id.send_stand_push)
        btn_send.setOnClickListener {
            with (NotificationManagerCompat.from (this)) {
                val intent: Intent = Intent(this@ApplicationListActivity, SplashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity( this@ApplicationListActivity, 0, intent, 0)

                val builder = NotificationCompat.Builder (this@ApplicationListActivity, "MyChannelId")
                        .setSmallIcon(R.drawable.small_icon)
                        .setColorized(true)
                        .setContentTitle ("Это обычный пуш")
                        .setContentText ("Открывай меня скорее;)")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                notify (1111, builder.build ())
            }
        }
        val btn_send_custom = findViewById<Button>(R.id.send_custom_push)
        btn_send_custom.setOnClickListener {
            with (NotificationManagerCompat.from (this)) {
                val intent: Intent = Intent(this@ApplicationListActivity, SplashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity( this@ApplicationListActivity, 0, intent, 0)
                val builder = NotificationCompat.Builder (this@ApplicationListActivity, "MyChannelId")
                        .setSmallIcon (R.drawable.small_icon)
                        .setColor(ContextCompat.getColor(this@ApplicationListActivity, R.color.colorPrimaryDark))
                        .setColorized(true)
                        .setLargeIcon(BitmapFactory.decodeResource(resources,
                                R.drawable.main_photo))
                        .setContentTitle ("Это кастомный пуш")
                        .setContentText ("Можешь открыть меня;)")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                notify (1112, builder.build ())
            }
        }
    }
    private fun createNotificationChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyChannel"
            val descriptionText = "DescriptionMyChannel"
            val priority = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel ("MyChannelId", name, priority) .apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                    getSystemService (Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (gestureDetector.onTouchEvent(ev)) true
        else super.dispatchTouchEvent(ev)
    }

    private fun firstStart(): Boolean {

        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this)

        if (prefs.getBoolean("first_start_service", true)) {
            val interval = android.preference.PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(Pref.WALLPAPER_UPDATE_FREQUENCY, Pref.WallpaperUpdateFrequency.frequency1)
            Background.addWallpaperWork(interval.toInt())
            prefs.edit().putBoolean("first_start_service", false).apply()
        }
        return false
    }

    private fun updateWallpaper() {
        CoroutineScope(Dispatchers.Default).launch {
            val source = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    .getString(Pref.WALLPAPER_SOURCE, Pref.WallpapperSource.sourse1)
            val temp = Background.getWallpaper(source)
            withContext(Dispatchers.Main) {
                drawer_layout.background = temp
            }
        }
    }

    private fun makeSwipeListener(): GestureListener {
        return object : GestureListener() {
            override fun onSwipeUp() = false
            override fun onSwipeDown() = false
            override fun onSwipeRight(): Boolean {
                val navController = findNavController(R.id.nav_host_fragment)
                return when ( navController.currentDestination?.id) {
                    R.id.gridFragment -> {
                        navController.navigate(R.id.action_gridFragment_to_homeFragment)
                        true
                    }
                    R.id.linearFragment -> {
                        navController.navigate(R.id.action_linearFragment_to_gridFragment)
                        true
                    }
                    else -> false
                }
            }

            override fun onSwipeLeft(): Boolean {
                val navController = findNavController(R.id.nav_host_fragment)
                return when(navController.currentDestination?.id) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.action_homeFragment_to_gridFragment2)
                        true
                    }
                    R.id.gridFragment -> {
                        navController.navigate(R.id.action_gridFragment_to_linearFragment)
                        true
                    }
                    else -> false
                }
            }

        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun removeElemByName(s: String){
        var i = 0
        for(e in originListApps){
            if(e.first.name == s){
                originListApps.removeAt(i)
                break
            }
            i++
        }
    }

    private fun findHomePositionByName(s: String):Int{
        var i = 0
        for(r in adapterHome.currentList){
            if(r.name == s)
                break;
            i++
        }
        return if(i == 17) -1 else i
    }
    private fun findElemByName(s: String, s2: String) : AppDetail{
        for(r in originListApps) {
            if(r.first.label == s && r.first.name == s2)
                return r.first
        }
        return originListApps[0].first //никогда не будет(?)
    }
    fun onProfileClick(view: View) {
        val navController = findNavController(R.id.nav_host_fragment)
        val builder: NavOptions.Builder = NavOptions.Builder()
        val navOptions = builder.setPopUpTo(R.id.gridFragment, false).build()
        navController.navigate(R.id.profileFragment, null, navOptions)
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    private fun onLongItemClick(view: View?, i: Int):Boolean{
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.context_menu, popupMenu.menu)
        val appInfo = this.packageManager.getApplicationInfo(adapter.currentList[i].first.name, 0)
        val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        if((appInfo.flags and mask) != 0)
            popupMenu.menu.findItem(R.id.delete_item).isVisible = false
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.delete_item ->{
                    YandexMetrica.reportEvent("Delete app")
                    val packageUri = Uri.parse("package:${adapter.currentList[i].first.name}")
                    val deleteApp = Intent(Intent.ACTION_DELETE, packageUri)
                    startActivity(deleteApp)
                }
                R.id.app_frequency -> {
                    val dialog = DialogFragment(adapter.currentList[i].second)
                    this?.supportFragmentManager?.let { it1 -> dialog.show(it1, "1") }
                }
                R.id.app_info -> {
                    YandexMetrica.reportEvent("Watched app info")
                    val packageUri = Uri.parse("package:${adapter.currentList[i].first.name}")
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri)
                    startActivity(intent)
                }
                R.id.add_to_home -> {
                    addItem(adapter.currentList[i].first)
                }
            }
            true
        }
        popupMenu.show()
        return true
    }
    private fun deleteClick(i: Int){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.setTitle("Удаление")
        dialog.setCancelable(true)
        val btn = dialog.findViewById<Button>(R.id.btn_delete)
        val txt = dialog.findViewById<TextView>(R.id.name_app)
        val btn_ext = dialog.findViewById<Button>(R.id.btn_cancel)
        txt.text = adapterHome.currentList[i].label
        btn.setOnClickListener{
            deleteApp(i)
            dialog.cancel()
        }
        btn_ext.setOnClickListener {
            dialog.cancel()
        }
        dialog.show()
    }

    private fun deleteApp(i: Int) {
        val items : ArrayList<AppDetail> = ArrayList()
        items.addAll(adapterHome.currentList)
        val t =  Thread {
            myDB.homeDao().deleteAppByPosition(i)
        }
        t.start()
        t.join()
        items[i] = AppDetail("","",null,0)
        adapterHome.submitList(items)
    }
    private fun appsCount(): Int {
        var k = 0
        for(r in adapterHome.currentList)
            if(r.label!="" || r.label == "Delete")
                k++
        return k
    }
    private fun addItem(appDetail: AppDetail){
        if(appsCount() >= 16) {
            Log.i("QWE", "замнога уже")
            return
        }
        for(c in adapterHome.currentList){
            Log.e("INDEX_CHECK", c.label)
        }
        if(adapterHome.currentList.contains(appDetail)){
            Log.i("QWE", "такой уже есть")
            return
        }
        Log.e("INDEX_ADD", appDetail.label)
        var k = 0
        for (r in adapterHome.currentList){
            if(r.label!=""){
                k++
                continue
            }
            break
        }
        val items : ArrayList<AppDetail> = ArrayList()
        items.addAll(adapterHome.currentList)
        items[k] = appDetail
        adapterHome.submitList(items)
        val t =  Thread {
            myDB.homeDao().insertApp(HomeApp.fromContentValues(ContentValues().apply {
                put(HomeApp.COLUMN_LABEL, appDetail.label)
                put(HomeApp.COLUMN_NAME, appDetail.name)
                put(HomeApp.COLUMN_INDEX, k)
            }))
        }
        t.start()
        t.join()
    }
    private fun onItemClick(view: View?, i: Int) {
        val items : ArrayList<Pair<AppDetail, Int>> = ArrayList()
        items.addAll(adapter.currentList)
        val item = adapter.currentList[i]
        val eventParameters: HashMap<String, Any> = HashMap()
        eventParameters["name"] = item.first.label
        YandexMetrica.reportEvent("Start app from launcher", eventParameters)
        val k = item.second + 1
        val t = Thread {
            myDB.appDao().updateApp(k, item.first.label, item.first.name, System.currentTimeMillis())
        }
        t.start()
        t.join()
        originListApps[originListApps.indexOf(item)] =
                originListApps[originListApps.indexOf(item)].copy(second = k)
        items[i] = items[i].copy(second = k)
        adapter.submitList(items)
        applySortForAdapter()
        val intent: Intent = this!!.packageManager.getLaunchIntentForPackage(item.first.name)!!
        startActivity(intent)
    }

    private fun onHomeItemClick(view: View?, i: Int) {
        val item = adapterHome.currentList[i]
        when(item.name){
            "" -> {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www."+item.label))
                startActivity(webIntent)
            }
            else -> {
                val intent: Intent = this!!.packageManager.getLaunchIntentForPackage(item.name)!!
                startActivity(intent)
            }
        }
    }
    private fun loadData(@Nullable records: List<HomeApp>){
        val items : ArrayList<AppDetail> = ArrayList()
        for (i in 1..16)
            items.add(AppDetail("","",null,0))
        for(f in records){
            if(f.name != "")
                items[f.index_rv] = AppDetail(f.label, f.name, packageManager.getApplicationIcon(f.name), 0)
            else
                items[f.index_rv] = AppDetail(f.label, "", getIcon(f.label), 0)
        }
        items.add(AppDetail("Delete","", ResourcesCompat.getDrawable(resources, R.drawable.trashcan_hover, null),0))
        runOnUiThread{adapterHome.submitList(items)}

        originListApps.clear()
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val availableActivities: List<ResolveInfo> = packageManager.queryIntentActivities(i, 0)
            var app: AppDetail
            var temp: App?
            var loadLabel: String
            var packageName: String
            for (info in availableActivities) {
                packageName = info.activityInfo.packageName
                if(packageName == "space.alena.kominch") continue
                loadLabel = info.loadLabel(packageManager) as String
                app = AppDetail(loadLabel, packageName, info.activityInfo.loadIcon(packageManager),
                        packageManager.getPackageInfo(packageName, 0).firstInstallTime )
                temp = myDB.appDao().findByLabelAndName(loadLabel, packageName)
                if (temp != null)
                    originListApps.add(app to temp.count)
                else {
                    originListApps.add(app to 0)
                    myDB.appDao().insertAll(App.fromContentValues(ContentValues().apply {
                        put(App.COLUMN_LABEL, loadLabel)
                        put(App.COLUMN_NAME, packageName)
                        put(App.COLUMN_COUNT, 0)
                        put(App.COLUMN_START, 0)
                    }))
                }
            }
            runOnUiThread {
                adapter.submitList(originListApps)
                applySortForAdapter()
            }
    }

    private fun logRecords(@Nullable records: List<App>){
        for(f in records){
            Log.i("qwe", String.format(
                    "record: id = %d; label = %s; count = %s;start = %d",
                    f.id,
                    f.label,
                    f.name,
                    f.start
            ))
        }
    }

    override fun recreateWithPrefs() {
        recreate()
    }

    override fun applySortForAdapter() {
        val temp: String = prefs.getString(getString(R.string.id_type_of_sort), "")!!
        if(temp.contains(getString(R.string.wout_sort))){
            adapter.submitList(originListApps)
            return
        }
        val items : ArrayList<Pair<AppDetail, Int>> = ArrayList()
        items.addAll(originListApps)
        if(temp.contains(getString(R.string.sort_by_name)))
            items.sortBy { it.first.label}
        else if (temp.contains(getString(R.string.sort_by_name_reverse))) {
            items.sortBy { it.first.label }
            items.reverse()
        }
        else if (temp.contains(getString(R.string.sort_by_frequency))) {
            items.sortBy { it.second }
            items.reverse()
        }
        else if (temp.contains(getString(R.string.sort_by_date))) {
            items.sortBy { it.first.installationTime}
        }
        adapter.submitList(items)
    }

    private fun registerMyReceiver(){
        val intentFilter =
                IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_REMOVED)
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addDataScheme("package")
                }

        val wallpaperFilter =
                IntentFilter().apply {
                    addAction(BackgroundLoaderService.BROADCAST_ACTION_UPDATE_WALLPAPERS)
                }

        registerReceiver(mMyReceiver, intentFilter)
        registerReceiver(mMyReceiver, wallpaperFilter)
    }
    override fun onDestroy() {
        unregisterReceiver(mMyReceiver)
        super.onDestroy()
    }

    override fun refreshDataBase(appDetail: AppDetail, i: Int) {
        val t = Thread {
            myDB.homeDao().updateHomeApp(i, appDetail.label, appDetail.name)
        }
        t.start()
        t.join()
    }

    override fun addWebsite() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_dialog)
        dialog.setTitle("Add website to workspace")
        dialog.setCancelable(true)
        dialog.show()
        val btn = dialog.findViewById<Button>(R.id.btn_add)
        val btn_ext = dialog.findViewById<Button>(R.id.btn_cancel)
        btn.setOnClickListener{
            val name: String = dialog.findViewById<EditText>(R.id.editText).text.toString()
            if(name!=""){
                var app: AppDetail? = null
                val t = Thread {
                    app = AppDetail(name,"",getIcon(name),0)
                }
                t.start()
                t.join()
                addItem(app!!)
            }
            dialog.cancel()
        }
        btn_ext.setOnClickListener {
            dialog.cancel()
        }
    }
    private fun getIcon(address: String): Drawable{
        if (checkPermissions()) {
            return onPermissoinsGranted(address)
        }
        return ContextCompat.getDrawable(this, R.drawable.temp_icon)!!
    }
    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), CODE_REQUEST_INTERNET)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CODE_REQUEST_INTERNET -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //onPermissoinsGranted("")
                } else {
                    // пачему же пачиму???((
                }
            }
        }
    }
    private fun onPermissoinsGranted(s: String): Drawable{
        val imgUrl = "https://i.olsh.me/icon?size=48&url=$s"
        val url: URL = URL(imgUrl)
        var inputStream : InputStream? = null
        val t = Thread {
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            inputStream = connection.inputStream
        }
        t.start()
        t.join()
        val img: Bitmap = BitmapFactory.decodeStream(inputStream)
        if(img == null)
            return ContextCompat.getDrawable(this, R.drawable.temp_icon)!!
        return BitmapDrawable(resources, img)
    }
}
