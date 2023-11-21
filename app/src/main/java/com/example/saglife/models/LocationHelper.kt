import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

class LocationHelper(private val context: Context) {

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    // Fonction pour démarrer la mise à jour de la localisation
    fun startLocationUpdates(callback: (Location) -> Unit) {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Vérifiez les autorisations ici si nécessaire

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Callback lorsque la localisation de l'utilisateur change
                callback(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        }

        // Demandez les mises à jour de localisation en fonction du fournisseur que vous souhaitez utiliser

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BETWEEN_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            locationListener!!
        )
    }

    // Fonction pour arrêter la mise à jour de la localisation lorsque vous n'en avez plus besoin
    fun stopLocationUpdates() {
        locationManager?.removeUpdates(locationListener!!)
    }

    companion object {
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000 // 1 seconde
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 mètres
    }
}
