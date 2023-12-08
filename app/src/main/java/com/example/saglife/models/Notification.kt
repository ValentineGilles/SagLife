import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Notification(private val title: String, private val body: String, private val topic: String) {

    fun send(context: Context) {
        val url = "https://fcm.googleapis.com/fcm/send"

        val requestQueue = Volley.newRequestQueue(context)

        val jsonObject = JSONObject().apply {
            put("to", "/topics/$topic")
            put(
                "notification",
                JSONObject().apply {
                    put("title", title)
                    put("body", body)
                    put("sound", "default")
                }
            )
        }

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                Log.d("Notification", "Message sent successfully: $response")
            },
            Response.ErrorListener { error ->
                Log.e("Notification", "Error sending message: $error")
            }) {
            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "key=AAAAtbl3SOY:APA91bGF0c0P3tWlIcYgiShd3Q9TuYrLZOdTXUd_xy3ZAXkECh_q6Rw5P9o8NjTaolmgpGLc_2sVvSrE5xxZd4t_JkZMqZUOIkXfw-YmNsOPaI8Wb2KkPSed4tkAEG02OxGqSk3FQi2n"
                )
            }
        }

        requestQueue.add(jsonObjectRequest)
    }
}
