package com.markodevcic.samples

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.markodevcic.peko.allGranted
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

	private lateinit var viewModel: MainViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		PermissionRequester.initialize(applicationContext)

		viewModel = ViewModelProvider(
				this@MainActivity,
				MainViewModelFactory(PermissionRequester.instance())
		)[MainViewModel::class.java]

		CoroutineScope(Dispatchers.Main).launch {
			PermissionRequester.instance().request(Manifest.permission.CALL_PHONE)
				.collect {
					Log.d("PEKO", "RESULT: $it")
				}
		}

		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

//		viewModel.liveData.observe(this) {
//			setResult(it)
//		}

//		lifecycleScope.launchWhenStarted {
//			viewModel.permissionsFlow
//					.collect { setResult(it) }
//		}

		btnContacts.setOnClickListener {
			requestPermission(Manifest.permission.READ_CONTACTS)
		}
		btnFineLocation.setOnClickListener {
			requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
		}
		btnFile.setOnClickListener {
			requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
		}
		btnCamera.setOnClickListener {
			requestPermission(Manifest.permission.CAMERA)
		}
		btnAll.setOnClickListener {
			viewModel.requestPermissions(
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.CAMERA,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.READ_CONTACTS
			)
		}

//	        permissionRequestThatWorks() // ok
        permissionRequestThatFails() // Back button is 'stuck', and notice that PermissionRequester:76 is never reached
//        permissionRequestThatAlsoWorks()
//        simpleRequestThatFails() // ok
    }

    private fun simpleRequestThatFails() {
        val context = applicationContext
        val permissionRequester = PermissionRequester.instance() // Creates new instance
        val activityLifecycleScope: LifecycleCoroutineScope = lifecycleScope
        activityLifecycleScope.launchWhenResumed { // Fails
            permissionRequester.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .collect { result ->
                    Toast.makeText(context, "result: $result", Toast.LENGTH_LONG).apply { show() }
                }
        }
    }

    private fun permissionRequestThatFails() {
        val activityLifecycleScope: LifecycleCoroutineScope = lifecycleScope
        val service = GpsLocationService(
            this,
            runTestOnWithinContext = false,
            runTestOnMainDispatcher = true
        )
        activityLifecycleScope.launchWhenResumed { // Fails
            service.register()
        }
    }

    private fun permissionRequestThatAlsoWorks() {
        val activityLifecycleScope: LifecycleCoroutineScope = lifecycleScope
        val service = GpsLocationService(
            this,
            runTestOnWithinContext = true,
            runTestOnMainDispatcher = true
        )
        activityLifecycleScope.launchWhenResumed { // Fails
            service.register()
        }
    }

    private fun permissionRequestThatWorks() {
        val service = GpsLocationService(
            this,
            runTestOnWithinContext = false,
            runTestOnMainDispatcher = true
        )
        CoroutineScope(Dispatchers.Main).launch { // Works
            service.register()
        }
    }

	private fun checkAllGranted(vararg permissions: String) {
		lifecycleScope.launch {
			val allGranted = viewModel.flowPermissions(*permissions).allGranted()
		}
	}

	private fun requestPermission(vararg permissions: String) {
		viewModel.requestPermissions(*permissions)
	}

	private fun setResult(result: PermissionResult) {
		if (result is PermissionResult.Granted) {

			val granted = "GRANTED"
			if (Manifest.permission.ACCESS_COARSE_LOCATION == result.permission) {
				textLocationResult.text = granted
				textLocationResult.setTextColor(Color.GREEN)
			}
			if (Manifest.permission.WRITE_EXTERNAL_STORAGE == result.permission) {
				textFileResult.text = granted
				textFileResult.setTextColor(Color.GREEN)
			}
			if (Manifest.permission.CAMERA == result.permission) {
				textCameraResult.text = granted
				textCameraResult.setTextColor(Color.GREEN)
			}
			if (Manifest.permission.READ_CONTACTS == result.permission) {
				textContactsResult.text = granted
				textContactsResult.setTextColor(Color.GREEN)
			}
		} else if (result is PermissionResult.Denied) {
			if (Manifest.permission.ACCESS_COARSE_LOCATION == result.permission) {
				textLocationResult.text = deniedReasonText(result)
				textLocationResult.setTextColor(Color.RED)
			}
			if (Manifest.permission.WRITE_EXTERNAL_STORAGE == result.permission) {
				textFileResult.text = deniedReasonText(result)
				textFileResult.setTextColor(Color.RED)
			}
			if (Manifest.permission.CAMERA == result.permission) {
				textCameraResult.text = deniedReasonText(result)
				textCameraResult.setTextColor(Color.RED)
			}
			if (Manifest.permission.READ_CONTACTS == result.permission) {
				textContactsResult.text = deniedReasonText(result)
				textContactsResult.setTextColor(Color.RED)
			}
		} else if (result is PermissionResult.Cancelled) {
			textLocationResult.text = "CANCELLED"
			textLocationResult.setTextColor(Color.RED)
			textFileResult.text = "CANCELLED"
			textFileResult.setTextColor(Color.RED)
			textCameraResult.text = "CANCELLED"
			textCameraResult.setTextColor(Color.RED)
			textContactsResult.text = "CANCELLED"
			textContactsResult.setTextColor(Color.RED)
		}
	}

	private fun deniedReasonText(result: PermissionResult): String {
		return when (result) {
			is PermissionResult.Denied.NeedsRationale -> "NEEDS RATIONALE"
			is PermissionResult.Denied.DeniedPermanently -> "DENIED PERMANENTLY"
			else -> ""
		}
	}


	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}
