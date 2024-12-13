package io.github.asephermann.plugins.mocklocationchecker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


const val TAG: String = "MockLocationChecker"

class MockLocationChecker {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var indicated = JSONArray()

    @SuppressLint("ObsoleteSdkInt")
    fun checkMock(activity: Activity, whiteList: List<String>): CheckMockResult {
        val listData: ArrayList<String>
        var msg = ""

        // check root
        if (isDeviceRooted()) {
            msg += "Device is rooted. Please unroot the device.\n"

            AlertDialog.Builder(activity)
                .setTitle("Security Alert")
                .setMessage("This device is rooted. The application will now exit.")
                .setPositiveButton("OK") { _, _ ->
                    activity.finish() // Close the application
                }
                .setCancelable(false)
                .show()
        }

        // check Developer Options
        if (isDeveloperOptionsEnabled(activity)) {
            msg += "Developer options are enabled. Please disable Developer Options.\n"

            AlertDialog.Builder(activity)
                .setTitle("Security Alert")
                .setMessage("Please disable Developer Options to proceed.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    activity.startActivity(intent)
                }
                .setCancelable(false)
                .show()
        }

        // check Mock Location
        val isMock: Boolean = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Settings.Secure.getString(
                    activity.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION
                ) != "0"
            ) {
                msg += "Please turn off Allow Mock locations option in developer options.\n"
                true
            } else {
                false
            }
        } else {
            listData = ArrayList(whiteList)
            if (checkForAllowMockLocationsApps(activity, listData)) {
                msg =
                    "We've detected that there are other apps in the device, which are using Mock Location access (Location Spoofing Apps). Please uninstall first."
                true
            } else {
                false
            }
        }
        Log.i(TAG, "isMock: $isMock")
        Log.i(TAG, "msg: $msg")
        Log.i(TAG, "indicated: $indicated")
        return CheckMockResult(isMock, msg, indicated)
    }

    private fun checkForAllowMockLocationsApps(
        activity: Activity,
        whiteList: ArrayList<String>
    ): Boolean {
        var count = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val hasPermission =
                activity.checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                activity.requestPermissions(arrayOf(Manifest.permission.QUERY_ALL_PACKAGES), 1)
            } else {
                val pm = activity.packageManager
                val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
                indicated = JSONArray()
                for (applicationInfo in packages) {
                    try {
                        val packageInfo = pm.getPackageInfo(
                            applicationInfo.packageName,
                            PackageManager.GET_PERMISSIONS
                        )

                        // Get Permissions
                        val requestedPermissions = packageInfo.requestedPermissions
                        if (requestedPermissions != null && requestedPermissions.contains("android.permission.ACCESS_MOCK_LOCATION")
                            && packageInfo.packageName != activity.packageName
                            && !whiteList.contains(packageInfo.packageName)
                        ) {
                            count++
                            indicated.put(packageInfo.packageName)
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        Log.e(TAG, "Got exception " + e.message)
                    }
                }
            }
        } else {
            val pm = activity.packageManager
            val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
            indicated = JSONArray()
            for (applicationInfo in packages) {
                try {
                    val packageInfo = pm.getPackageInfo(
                        applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS
                    )

                    val appInfo = pm.getApplicationInfo(applicationInfo.packageName, 0)

                    // Get Permissions
                    val requestedPermissions = packageInfo.requestedPermissions
                    if (requestedPermissions != null && requestedPermissions.contains("android.permission.ACCESS_MOCK_LOCATION")
                        && packageInfo.packageName != activity.packageName
                        && appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
                        && !whiteList.contains(packageInfo.packageName)
                    ) {
                        count++
                        indicated.put(packageInfo.packageName)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(TAG, "Got exception " + e.message)
                }
            }
        }

        return count > 0
    }

    @SuppressLint("ObsoleteSdkInt")
    fun isLocationFromMockProvider(activity: Activity): Boolean {
        var isFromMockProvider = false
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    try {
                        isFromMockProvider = if (Build.VERSION.SDK_INT <= 30) {
                            location.isFromMockProvider
                        } else if (Build.VERSION.SDK_INT >= 31) {
                            location.isMock
                        } else {
                            false
                        }
                    } catch (e: Exception) {
                        Log.e("MockLocationChecker", e.toString())
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 18) {
            if (isLocationEnabled(activity)) {
                if (ActivityCompat.checkSelfPermission(
                        activity.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity.applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Open app settings to allow the user to grant permissions
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                    return false
                }
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } else {
                Toast.makeText(
                    activity.applicationContext,
                    "Please turn on location",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivity(intent)
                return false
            }
            return isFromMockProvider
        } else {
            return Settings.Secure.getString(
                activity.applicationContext.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION
            ) != "0"
        }
    }

    private fun isLocationEnabled(activity: Activity): Boolean {
        val locationManager: LocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    suspend fun checkMockGeoLocation(activity: Activity): CheckMockResult =
        suspendCoroutine { continuation ->
            var msg = ""
            var isMock = false
            locationClient = DefaultLocationClient(
                activity,
                LocationServices.getFusedLocationProviderClient(activity)
            )
            val locationUpdatesJob = serviceScope.launch {
                locationClient
                    .getLocationUpdates(10000L)
                    .catch { e ->
                        e.printStackTrace()
                        continuation.resumeWith(
                            Result.success(
                                CheckMockResult(
                                    false,
                                    "Failed to get location",
                                    indicated
                                )
                            )
                        )
                    }
                    .onEach { location ->
                        val lat = location.latitude.toString()
                        val long = location.longitude.toString()
                        isMock = if (Build.VERSION.SDK_INT <= 30) {
                            location.isFromMockProvider
                        } else if (Build.VERSION.SDK_INT >= 31) {
                            location.isMock
                        } else {
                            false
                        }
                        msg = "Location: ($lat, $long); Is Mock: $isMock"
                    }
                    .collect {
                        continuation.resumeWith(
                            Result.success(
                                CheckMockResult(
                                    isMock,
                                    msg,
                                    indicated
                                )
                            )
                        )
                    }
            }

            serviceScope.launch {
                delay(30000L)
                locationUpdatesJob.cancel()
                continuation.resumeWith(
                    Result.success(
                        CheckMockResult(
                            false,
                            "Timeout",
                            indicated
                        )
                    )
                )
            }
        }

    private fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { path ->
            try {
                File(path).exists()
            } catch (e: Exception) {
                false
            }
        } || checkSuCommand()
    }

    private fun checkSuCommand(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            process.inputStream.bufferedReader().use { it.readLine() != null }
        } catch (e: Exception) {
            false
        }
    }

    private fun isDeveloperOptionsEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED
            ) == 1
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

}

data class CheckMockResult(
    var isMock: Boolean,
    var messages: String,
    var indicated: JSONArray
)