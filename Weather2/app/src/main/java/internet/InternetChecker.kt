package internet

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class InternetChecker {
    private var connectivity : ConnectivityManager? = null
    private var info : NetworkInfo? = null

    fun isOnline(context : Context) : Boolean {
        connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo

            if (info != null) {
                if (info!!.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false

    }
}