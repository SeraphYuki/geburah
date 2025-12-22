package com.example.geburah
import android.Manifest
import android.widget.Button
import android.widget.TextView
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RadioButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.OnMapReadyCallback

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.lifecycle.lifecycleScope
import com.example.geburah.ui.theme.UbersilkTheme
import coil3.ImageLoader.Builder
import coil3.compose.ImagePainter
import coil3.request.ImageRequest
import coil3.compose.AsyncImage
import coil3.request.crossfade
import  androidx.compose.ui.unit.sp
import okhttp3.RequestBody
import okhttp3.OkHttpClient
import okhttp3.Callback
import okhttp3.Request
import okhttp3.*
import okhttp3.FormBody
import okio.IOException
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.StreetViewPanorama
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import java.security.MessageDigest

fun String.sha1() :String{
    val messageDigest = MessageDigest.getInstance("SHA-1")
    val input = this.toByteArray(Charsets.UTF_8)
    val digestBytes = messageDigest.digest(input)
    return digestBytes.joinToString (""){ "%02x".format(it) }
}
class MainActivity : ComponentActivity(), OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {
    val client = OkHttpClient()
    var loggedin = 0
    var username_loggedin = ""
    var password_loggedin = ""
    var accounttype_loggedin = ""
    var silker_latitude = 0.0
    var silker_longitude = 0.0
    var silker_location = ""
    var offer_requestfor = ""
    var offer_loggedin = 0.0
    var amount_loggedin = ""
    var silker_requestid = 0
    var silkerid_loggedin = 0
    var status_loggedin = ""
    var latitude_view : Double = 0.0
    var longitude_view : Double = 0.0
    var latitude_loggedin : Double = 0.0
    var longitude_loggedin : Double = 0.0
    var imgid_loggedin = 0
    var requesting_loggedin = false
    var location_loggedin = ""

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var street: StreetViewPanorama



    override fun onMapReady(map: GoogleMap){
        googleMap = map
        val ny = LatLng(latitude_view, longitude_view)

        googleMap.addMarker(
            MarkerOptions()
                .position(ny)
                .title("location")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny,16.0f))

    }

    override fun onStreetViewPanoramaReady(googleStreet: StreetViewPanorama) {
        street = googleStreet
//        val ny = LatLng(40.712, -74.006)
        val ny = LatLng(latitude_view, longitude_view)

        street.setPosition(ny)

    }

    fun login(savedInstanceState: Bundle?,username :String, password:String){

        var httpdata = ""
        val formBody = FormBody.Builder()
            .add("username",username)
            .add("password",password)
            .add("mobile_session","1")
            .build()
//      .post(RequestBody.create("json")))


        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/login.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")

                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()
                val jsondata = JSONObject(httpdata)
                val success = jsondata.getBoolean("success")

                if(success){
                    loggedin = 1
                    username_loggedin = username
                    password_loggedin = password
                    accounttype_loggedin = jsondata.getString("status")
                    getRequest(savedInstanceState)
                    requestView(savedInstanceState)
                } else {
                    loginView(savedInstanceState)
                }
            }
        }
    }
    fun plugView(savedInstanceState: Bundle?, jsonObject : JSONObject){
        setContentView(R.layout.silker_plug)

        longitude_view= jsonObject.getDouble("longitude").toDouble()
        latitude_view=jsonObject.getDouble("latitude").toDouble()
        var info = findViewById<TextView>(R.id.info)

        info.text = "status: " + jsonObject.getString("status") + " offer: " + jsonObject.getString("offer").toString() + " amount: " + jsonObject.getString("amount").toString() +
                " requesting: " + jsonObject.getString("requestfor").toString() + " location: " + jsonObject.getString("location").toString()


        var cancel : Button = findViewById<Button>(R.id.cancelrequest)
        cancel.setOnClickListener {
            cancelPlug(savedInstanceState)
        }

        mapView = findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        var streetView = findViewById<StreetViewPanoramaView>(R.id.streetView)
        streetView.onCreate(savedInstanceState)
        streetView.getStreetViewPanoramaAsync(this)

        var finish : Button = findViewById<Button>(R.id.finishrequest)
        finish.setOnClickListener {
            silker_location = findViewById<TextView>(R.id.location).text.toString()
            silker_latitude = findViewById<TextView>(R.id.latitude).text.toString().toDouble()
            silker_longitude = findViewById<TextView>(R.id.longitude).text.toString().toDouble()
            finishPlug(savedInstanceState)
        }
    }
    fun  createaccount(savedInstanceState: Bundle?,username :String, password:String,passwordrepeat : String) {
        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("repeat_password", passwordrepeat)
            .add("mobile_session","1")
            .build()
//      .post(RequestBody.create("json")))
        val errorText : TextView = findViewById<TextView>(R.id.errorMessage)
        var httpdata = ""
        lifecycleScope.launch {

            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/create_account.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")
                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()
                val jsondata = JSONObject(httpdata)
                val success = jsondata.getBoolean("success")
                if(success == true){
                    loginView(savedInstanceState)
                } else {
                    errorText.text = jsondata.getString("message")

                }

            }
        }
    }

    fun setplug(savedInstanceState: Bundle?, jsonObject : JSONObject){

        var requestId = silker_requestid.toString()

        var httpdata = ""
        val formBody = FormBody.Builder()
            .add("username",username_loggedin)
            .add("password",password_loggedin)
            .add("requestid",requestId)
            .add("mobile_session","1")
            .build()
//      .post(RequestBody.create("json")))

        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/silker_assign.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")

                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()

                    plugView(savedInstanceState, jsonObject)
            }
        }

    }

    fun finishPlug(savedInstanceState: Bundle?){

        var requestId = silker_requestid.toString()
        var httpdata = ""
        val formBody = FormBody.Builder()
            .add("username",username_loggedin)
            .add("password",password_loggedin)
            .add("latitude",silker_latitude.toString())
            .add("longitude",silker_longitude.toString())
            .add("location",silker_location)
            .add("requestid",requestId)
            .add("mobile_session","1")
            .build()
//      .post(RequestBody.create("json")))

        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/silker_finish.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")

                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                requestView(savedInstanceState)
            }
        }

    }


    fun cancelPlug(savedInstanceState: Bundle?){

        var requestId = silker_requestid.toString()
        var httpdata = ""
        val formBody = FormBody.Builder()
            .add("username",username_loggedin)
            .add("password",password_loggedin)
            .add("requestid",requestId)
            .add("mobile_session","1")
            .build()
//      .post(RequestBody.create("json")))

        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/silker_cancel.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")

                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                    requestView(savedInstanceState)
            }
        }

    }



    fun silkerScreen(savedInstanceState: Bundle?, jsonString : String){
        var jsonobjcet = JSONObject(jsonString)

        if(jsonobjcet.has("silker")){
            silker_requestid = jsonobjcet.getJSONObject("silker").getInt("requestid")
            plugView(savedInstanceState, jsonobjcet.getJSONObject("silker"))

            return
        }
        setContentView(R.layout.silker)
        if(jsonobjcet.has("requests")) {

            var requests = jsonobjcet.getJSONArray("requests")

            //
            var info = findViewById<LinearLayout>(R.id.linearlayout)
            for (i in 0 until requests.length()) {
                var x = i
                var jsondata = requests.getJSONObject(x)
                var amount = jsondata.getString("amount")
                var offer = jsondata.getString("offer")
                var requestid = jsondata.getInt("requestid")
                var requestfor = jsondata.getString("requestfor")
                var location = jsondata.getString("location")
                val str =
                    "offer: " + offer.toString() + "\namount: " + amount.toString() + "\nrequesting: " + requestfor.toString() + "\nlocation: " + location.toString()
                var te = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                te.setText(str)
                te.setTextSize(28f)
                te.setTextColor(android.graphics.Color.BLACK)
                te.setBackgroundColor(android.graphics.Color.WHITE)
                info.addView(te)
                var button = Button(this).apply {
                    text = "plug"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                button.setOnClickListener {
                    jsondata = requests.getJSONObject(x)
                    silker_requestid = requestid

                    setplug(savedInstanceState, jsondata)
                }
                info.addView(button)

            }
        }
        val request : Button = findViewById<Button>(R.id.makerequest)
        request.setOnClickListener {
            requestView(savedInstanceState)
        }
    }
    fun getSilker(savedInstanceState :Bundle?){
        val formBody = FormBody.Builder()
            .add("username", username_loggedin)
            .add("password", password_loggedin)
            .add("mobile_session","1")
            .build()
        var httpdata = ""
        lifecycleScope.launch {

            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/silker_view_requests.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")
                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()
                silkerScreen(savedInstanceState, httpdata)
            }
        }
    }
    fun getRequest(savedInstanceState :Bundle?){
        val formBody = FormBody.Builder()
            .add("username", username_loggedin)
            .add("password", password_loggedin)
            .add("mobile_session","1")
            .build()
        var httpdata = ""
        lifecycleScope.launch {

            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/get_request.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")
                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()
                val jsondata = JSONObject(httpdata)
                val success = jsondata.getBoolean("success")

                if(success == true){
                    status_loggedin = jsondata.getString("status")
                    offer_loggedin  = jsondata.getDouble("offer")
                    amount_loggedin = jsondata.getString("amount")
                    location_loggedin = jsondata.getString("location")
                    silkerid_loggedin = jsondata.getInt("silkerid")
                    offer_requestfor = jsondata.getString("requestfor")
                    imgid_loggedin = jsondata.getInt("imgid")
                    longitude_loggedin = jsondata.getDouble("longitude")
                    latitude_loggedin = jsondata.getDouble("latitude")
                    requesting_loggedin = true

                    mapViewScreen(savedInstanceState)
                }
            }
        }
    }

    fun endRequest(savedInstanceState :Bundle?){
        val formBody = FormBody.Builder()
            .add("username", username_loggedin)
            .add("password", password_loggedin)
            .add("mobile_session","1")
            .build()

        var httpdata = ""
        lifecycleScope.launch {

            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/end_request.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")
                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()

                val jsondata = JSONObject(httpdata)
                val success = jsondata.getBoolean("success")
                if(success == true){
                    requesting_loggedin = false
                    requestView(savedInstanceState)
                }
            }
        }
    }
    fun submitRequest(savedInstanceState :Bundle?, amount : String, offer : String, forrequest : String, location : String,latitude : String, longitude : String){
        val formBody = FormBody.Builder()
            .add("username", username_loggedin)
            .add("password", password_loggedin)
            .add("amount", amount)
            .add("offer", offer)
            .add("requestfor", forrequest)
            .add("location", location)
            .add("longitude", longitude)
            .add("latitude", latitude)
            .add("mobile_session","1")
            .build()

        var httpdata = ""
        lifecycleScope.launch {

            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://geburah.vip/submit_request.php")
                    .post(formBody)
                    .header("Content-Type", "text/html")
                    .build()

                client.newCall(request).execute()
            }
            if(response.isSuccessful) {
                httpdata = response.body!!.string()

                val jsondata = JSONObject(httpdata)
                val success = jsondata.getBoolean("success")
                if(success == true){
                    getRequest(savedInstanceState)
                }
            }
        }
    }
    fun requestView(savedInstanceState: Bundle?){
        setContentView(R.layout.requests)


        if(accounttype_loggedin == "silker"){

            val viewsilker : Button = findViewById<Button>(R.id.silkerview)

            viewsilker.setOnClickListener {
                getSilker(savedInstanceState)

            }
        }



        val request : Button = findViewById<Button>(R.id.request)
        val amount : EditText = findViewById<EditText>(R.id.amount)
        val offer : EditText = findViewById<EditText>(R.id.offer)
        val requestfor : RadioGroup = findViewById<RadioGroup>(R.id.requestfor)
        val location : EditText = findViewById<EditText>(R.id.location)
        val longitude : EditText = findViewById<EditText>(R.id.longitude)
        val latitude : EditText = findViewById<EditText>(R.id.latitude)
        request.setOnClickListener {
            val selected = requestfor.checkedRadioButtonId
            val requestforChecked : RadioButton = findViewById<RadioButton>(selected)
            submitRequest(savedInstanceState,amount.text.toString(), offer.text.toString(), requestforChecked.text.toString(), location.text.toString(), latitude.text.toString(),longitude.text.toString())
        }


    }
    fun mapViewScreen(savedInstanceState: Bundle?){

        setContentView(R.layout.pickup)
        var info = findViewById<TextView>(R.id.info)
        info.text = "status: " + status_loggedin + " offer: " + offer_loggedin.toString() + " amount: " + offer_loggedin.toString() +
                " requesting: " + offer_requestfor.toString() + " location: " + location_loggedin.toString()


        longitude_view= longitude_loggedin
        latitude_view= latitude_loggedin
        var cancel : Button = findViewById<Button>(R.id.cancelrequest)
        cancel.setOnClickListener {
            endRequest(savedInstanceState)
        }

        var refresh : Button = findViewById<Button>(R.id.refresh)
        refresh.setOnClickListener {
            getRequest(savedInstanceState)
        }
        if(status_loggedin == "complete"){
            mapView = findViewById<MapView>(R.id.mapView)
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this)
            var streetView = findViewById<StreetViewPanoramaView>(R.id.streetView)
            streetView.onCreate(savedInstanceState)
            streetView.getStreetViewPanoramaAsync(this)
        }


    }
    fun createAccView(savedInstanceState: Bundle?){

        setContentView(R.layout.create_account)


        val create : Button = findViewById<Button>(R.id.create)
        val back : Button = findViewById<Button>(R.id.backbutton)
        val username : EditText = findViewById<EditText>(R.id.username)
        val password : EditText = findViewById<EditText>(R.id.password)
        val passwordRepeat : EditText = findViewById<EditText>(R.id.passwordrepeat)
        create.setOnClickListener {
             createaccount(savedInstanceState,username.text.toString(), password.text.toString(),passwordRepeat.text.toString())
        }
        back.setOnClickListener {
            loginView(savedInstanceState)
        }

    }
    fun loginView(savedInstanceState: Bundle?){
        setContentView(R.layout.main_layout)

        val login : Button = findViewById<Button>(R.id.login)
        val newaccount : Button = findViewById<Button>(R.id.newaccount)
        val username : EditText = findViewById<EditText>(R.id.username)
        val password : EditText = findViewById<EditText>(R.id.password)
        login.setOnClickListener {
            login(savedInstanceState,username.text.toString(), password.text.toString())
        }
        newaccount.setOnClickListener {
            createAccView(savedInstanceState)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginView(savedInstanceState)
    }
}


@Preview
@Composable
fun UbersilkApp(name :String ) {
    val url = "https://angelicthrone.com/wp-content/uploads/2025/04/angelic-tarot-vehuiah.jpg"

    ShowImage(url,modifier = Modifier.fillMaxSize())
    Greeting(name)

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

        Text(
            text = name,
            style = TextStyle(fontSize = 30.sp),
            color = Color.White,
        )

}
@Composable
fun ShowImage(url : String, modifier : Modifier = Modifier){

    AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = "null",
            modifier = Modifier.fillMaxSize()
        )
}