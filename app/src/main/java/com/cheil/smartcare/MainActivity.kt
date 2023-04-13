package com.cheil.smartcare

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cheil.smartcare.continuousSpeechRecognizer.interfaces.RecognitionCallback
import com.cheil.smartcare.continuousSpeechRecognizer.managers.ContinuousRecognitionManager
import com.cheil.smartcare.continuousSpeechRecognizer.models.RecognitionStatus
import com.cheil.smartcare.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.util.*


class MainActivity : AppCompatActivity(), RecognitionCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mDpm: DevicePolicyManager
    //private var PACKAGE_NAME: String? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var REQUEST_CODE = 1

    private var speechRecognizerIntent: Intent? = null
    var mDecorView: View? = null

    companion object {
        /**
         * Put any keyword that will trigger the speech recognition
         */
        private const val ACTIVATION_KEYWORD = "제일전기"
        private const val RECORD_AUDIO_REQUEST_CODE = 101
    }

    private val recognitionManager: ContinuousRecognitionManager by lazy {
        ContinuousRecognitionManager(this, activationKeyword = ACTIVATION_KEYWORD, callback = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        //PACKAGE_NAME = applicationContext.packageName

        // 상단 툴바 제거
        supportActionBar?.hide()
        //supportActionBar!!.hide()
        /*
        val componentName = ComponentName(this, KioskDeviceAdminReceiver::class.java)
        val policyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        if (!policyManager.isAdminActive(componentName)) {
            Toast.makeText(this, "not device admin", Toast.LENGTH_SHORT).show()
            //val nextIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            //nextIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
            //startActivity(nextIntent)
        }

        if (policyManager.isDeviceOwnerApp(packageName)) {
            policyManager.setLockTaskPackages(componentName, arrayOf(packageName))
        } else {
            Toast.makeText(this, "not device owner", Toast.LENGTH_SHORT).show()
        }
        if (policyManager.isLockTaskPermitted(packageName)) {
            startLockTask()
        }
        */
        // 안드로이드 6.0버전 이상인지 체크해서 퍼미션 체크
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
        }

        /*
        binding.appBarMain.fab?.setOnClickListener { view ->
            //Snackbar.make(view, "Voice Recognition", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            startSTT()
        }
        */
        recognitionManager.createRecognizer()
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController

        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_main_fragment, R.id.nav_settings
                ),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_main_fragment
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }
    }

    override fun onDestroy() {
        recognitionManager.destroyRecognizer()
        super.onDestroy()
    }

    override fun onPause() {
        stopRecognition()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecognition()
        }
    }

    private fun startRecognition() {
        //progressBar.isIndeterminate = false
        //progressBar.visibility = View.VISIBLE
        recognitionManager.startRecognition()
    }

    private fun stopRecognition() {
        //progressBar.isIndeterminate = true
        //progressBar.visibility = View.INVISIBLE
        recognitionManager.stopRecognition()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp

        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }

        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    fun hideSystemUI()
    {

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)

            val controller = window.insetsController
            if( controller != null){
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        else{
            mDecorView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

    }

    fun showSystemUI()
    {

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(true)

            val controller = window.insetsController
            if( controller != null){
                controller.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        else{
            mDecorView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

    }


    /***
     *  SpeechToText 설정 및 동작
     */
    private  fun startSTT() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener)
            startListening(speechRecognizerIntent)
        }
    }

    /***
     *  SpeechToText 기능 세팅
     */
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            // 말하기 시작할 준비가되면 호출
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
        }

        override fun onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
        }

        override fun onRmsChanged(rmsdB: Float) {
            // 입력받는 소리의 크기를 알려줌
        }

        override fun onBufferReceived(buffer: ByteArray) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        override fun onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        override fun onError(error: Int) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출
            val message: String
            message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            Toast.makeText(applicationContext, "에러 발생 : $message", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            Toast.makeText(this@MainActivity, results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0], Toast.LENGTH_LONG).show()
        }

        override fun onPartialResults(partialResults: Bundle) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    }

    private fun getErrorText(errorCode: Int): String = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
        SpeechRecognizer.ERROR_SERVER -> "Error from server"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
        else -> "Didn't understand, please try again."
    }

    override fun onBeginningOfSpeech() {
        Log.i("Recognition","onBeginningOfSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i("Recognition", "onBufferReceived: $buffer")
    }

    override fun onEndOfSpeech() {
        Log.i("Recognition","onEndOfSpeech")
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.i("Recognition","onError: $errorMessage")
        //textView.text = errorMessage
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.i("Recognition","onEvent")
    }

    override fun onReadyForSpeech(params: Bundle) {
        Log.i("Recognition","onReadyForSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        //progressBar.progress = rmsdB.toInt()
    }

    override fun onPrepared(status: RecognitionStatus) {
        when (status) {
            RecognitionStatus.SUCCESS -> {
                Log.i("Recognition","onPrepared: Success")
                //textView.text = "Recognition ready"
                Toast.makeText(applicationContext, "Recognition ready", Toast.LENGTH_SHORT).show()
            }
            RecognitionStatus.UNAVAILABLE -> {
                Log.i("Recognition", "onPrepared: Failure or unavailable")
                AlertDialog.Builder(this)
                    .setTitle("Speech Recognizer unavailable")
                    .setMessage("Your device does not support Speech Recognition. Sorry!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }

    override fun onKeywordDetected() {
        Log.i("Recognition","keyword detected !!!")
        //extView.text = "Keyword detected"
        Toast.makeText(applicationContext, "Keyword detected", Toast.LENGTH_SHORT).show()
    }

    override fun onPartialResults(results: List<String>) {}

    override fun onResults(results: List<String>, scores: FloatArray?) {
        val text = results.joinToString(separator = "\n")
        Log.i("Recognition","onResults : $text")
        //textView.text = text
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecognition()
                }
            }
        }
    }
}