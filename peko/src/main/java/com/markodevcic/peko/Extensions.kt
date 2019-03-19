package com.markodevcic.peko


import android.app.Activity
import com.markodevcic.peko.rationale.PermissionRationale

/**
 * Requests permissions asynchronously. The function suspends only if request contains permissions that are denied.
 * Should be called from a coroutine which has a UI (Main) Dispatcher as context.
 * If the parent job is cancelled with [ActivityRotatingException], ongoing request will be retained and can be resumed with [resumePermissionRequest] function.
 * @return [PermissionRequestResult]
 * @throws [IllegalStateException] if called while another request has not completed yet
 */
suspend fun Activity.requestPermissionsAsync(vararg permissions: String,
                                             rationale: PermissionRationale = PermissionRationale.none) =
        Peko.requestPermissionsAsync(this, *permissions, rationale = rationale)

/**
 * Checks if there is a request in progress.
 * If true is returned, resume the existing request by calling [resumePermissionRequest]
 */
fun Activity.isPermissionRequestInProgress(): Boolean = Peko.isRequestInProgress()

/**
 * Resumes a request that was previously canceled with [ActivityRotatingException]
 * @throws [IllegalStateException] if there is no request in progress
 */
suspend fun Activity.resumePermissionRequest(): PermissionRequestResult = Peko.resumeRequest()

/**
 * Requests permissions asynchronously. The function suspends only if request contains permissions that are denied.
 * Should be called from a coroutine which has a UI (Main) Dispatcher as context.
 * If the parent job is cancelled with [ActivityRotatingException], ongoing request will be retained and can be resumed with [resumePermissionRequest] function.
 * @return [PermissionRequestResult]
 * @throws [IllegalStateException] if called while another request has not completed yet
 */
suspend fun android.app.Fragment.requestPermissionsAsync(vararg permissions: String,
                                                         rationale: PermissionRationale = PermissionRationale.none) =
        Peko.requestPermissionsAsync(this.activity, *permissions, rationale = rationale)

/**
 * Checks if there is a request in progress.
 * If true is returned, resume the existing request by calling [resumePermissionRequest]
 */
fun android.app.Fragment.isPermissionRequestInProgress(): Boolean = Peko.isRequestInProgress()

/**
 * Resumes a request that was previously canceled with [ActivityRotatingException]
 * @throws [IllegalStateException] if there is no request in progress
 */
suspend fun android.app.Fragment.resumePermissionRequest(): PermissionRequestResult = Peko.resumeRequest()


/**
 * Requests permissions asynchronously. The function suspends only if request contains permissions that are denied.
 * Should be called from a coroutine which has a UI (Main) Dispatcher as context.
 * If the parent job is cancelled with [ActivityRotatingException], ongoing request will be retained and can be resumed with [resumePermissionRequest] function.
 * @return [PermissionRequestResult]
 * @throws [IllegalStateException] if called while another request has not completed yet
 */
suspend fun android.support.v4.app.Fragment.requestPermissionsAsync(vararg permissions: String,
                                                                    rationale: PermissionRationale = PermissionRationale.none) =
        Peko.requestPermissionsAsync(this.activity, *permissions, rationale = rationale)

/**
 * Checks if there is a request in progress.
 * If true is returned, resume the existing request by calling [resumePermissionRequest]
 */
fun android.support.v4.app.Fragment.isPermissionRequestInProgress(): Boolean = Peko.isRequestInProgress()

/**
 * Resumes a request that was previously canceled with [ActivityRotatingException]
 * @throws [IllegalStateException] if there is no request in progress
 */
suspend fun android.support.v4.app.Fragment.resumePermissionRequest(): PermissionRequestResult = Peko.resumeRequest()