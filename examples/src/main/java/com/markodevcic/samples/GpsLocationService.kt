package com.markodevcic.samples

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// https://developer.android.com/training/location/request-updates
// https://developer.android.com/training/location/change-location-settings
class GpsLocationService(
    private val activity: MainActivity,
    private val runTestOnWithinContext: Boolean = false,// Deadlock if not within context
    private val runTestOnMainDispatcher: Boolean = true, // Works regardless of main or not
    private val context: Context = activity.applicationContext
) : LifecycleObserver {
    private val dispatcher = if (runTestOnMainDispatcher) Dispatchers.Main else Dispatchers.IO
    private val permissionRequester = PermissionRequester.instance() // Creates new instance

    suspend fun register() {
        // This means we have not previously requested location permission so its first startup, so request it now. If we have the permission it just continues and registers for location updates
        requestPermissionAndEnableServicesSuspending()
    }

    private suspend fun requestPermissionAndEnableServicesSuspending() {
        // We could check, but not necessary as user clicks gps themselves
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { app.activity.shouldShowRequestPermissionRationale(locationPermission) }
        requestPermissionInner { result ->
            Toast.makeText(context, "result: $result", Toast.LENGTH_LONG).apply { show() }
        }
    }

    private suspend fun requestPermissionInner(block: suspend (PermissionResult) -> Unit) {
        // Switching to IO for the permission request removed the deadlock
        toAddContextOrNotToAddContext {
            permissionRequester.request(locationPermission)
                .collect { result ->
                    toAddContextOrNotToAddContext {
                        block(result)
                    }
                }
        }
    }

    // This is a no-operation if runTestOnWithinContext is false
    private suspend fun toAddContextOrNotToAddContext(block: suspend () -> Unit) {
        if (runTestOnWithinContext) {
            withContext(dispatcher) {
                block()
            }
        } else {
            block()
        }
    }

    @Suppress("SimplifyBooleanWithConstants")
    companion object {
        private const val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
        private const val TAG = "GPS"
    }
}