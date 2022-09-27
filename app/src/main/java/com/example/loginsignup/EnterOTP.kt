package com.example.loginsignup

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class EnterOTP : AppCompatActivity() {

    private lateinit var etOTP: EditText
    private lateinit var etOTP2 : EditText
    private lateinit var etOTP3 : EditText
    private lateinit var etOTP4 : EditText
    private lateinit var etOTP5 : EditText
    private lateinit var etOTP6 : EditText

    private lateinit var btnVerifyOTP: Button
    private lateinit var btnResendOTP : TextView



    lateinit var auth: FirebaseAuth
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var phoneNumber : String
    private lateinit var storedOTP : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_otp)

       init()
        addTextChangeListener()
        resendOTPVisibility()

        btnResendOTP.setOnClickListener{
            resendVerificationCode()
            resendOTPVisibility()
        }  //

        btnVerifyOTP.setOnClickListener {
            val typedOtp = etOTP.text.toString() + etOTP2.text.toString() + etOTP3.text.toString() + etOTP4.text.toString() + etOTP5.text.toString() + etOTP6.text.toString()

                    if (typedOtp.isNotEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedOTP, typedOtp
                )
                singInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_LONG).show()
            }
        }





    }

    fun resendOTPVisibility(){
        etOTP.setText("")
        etOTP2.setText("")
        etOTP3.setText("")
        etOTP4.setText("")
        etOTP5.setText("")
        etOTP6.setText("")
        btnResendOTP.visibility = View.INVISIBLE
        btnResendOTP.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            btnResendOTP.visibility = View.VISIBLE
            btnResendOTP.isEnabled = true
        }, 60000)
    }



    private fun singInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, DashBoard::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }

            }
    }

    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

   private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            startActivity(Intent(applicationContext,DashBoard::class.java))
            finish()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(ContentValues.TAG, "onVerificationFailed", e)




            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(ContentValues.TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedOTP = verificationId
            resendToken = token


        }
    }

    private fun addTextChangeListener(){

        etOTP.addTextChangedListener(EditTextWatcher(etOTP))
        etOTP2.addTextChangedListener(EditTextWatcher(etOTP2))
        etOTP3.addTextChangedListener(EditTextWatcher(etOTP3))
        etOTP4.addTextChangedListener(EditTextWatcher(etOTP4))
        etOTP5.addTextChangedListener(EditTextWatcher(etOTP5))
        etOTP6.addTextChangedListener(EditTextWatcher(etOTP6))
    }

    fun init(){
        auth = FirebaseAuth.getInstance()

        storedOTP = intent.getStringExtra("OTP").toString()

        resendToken = intent.getParcelableExtra("resendToken")!!

        phoneNumber = intent.getStringExtra("phoneNumber")!!

        btnVerifyOTP = findViewById(R.id.btnVerifyOTP)
        btnResendOTP  = findViewById(R.id.btnResendOTP)

        etOTP = findViewById(R.id.etOTP1)
        etOTP2 = findViewById(R.id.etOTP2)
        etOTP3 = findViewById(R.id.etOTP3)
        etOTP4 = findViewById(R.id.etOTP4)
        etOTP5 = findViewById(R.id.etOTP5)
        etOTP6 = findViewById(R.id.etOTP6)

    }

    inner class EditTextWatcher(private  val view : View) : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()

            when(view.id){
                R.id.etOTP1 -> if (text.length == 1) etOTP2.requestFocus()
                R.id.etOTP2 -> if (text.length == 1) etOTP3.requestFocus() else if (text.isEmpty()) etOTP.requestFocus()
                R.id.etOTP3 -> if (text.length == 1) etOTP4.requestFocus() else if (text.isEmpty()) etOTP2.requestFocus()
                R.id.etOTP4 -> if (text.length == 1) etOTP5.requestFocus() else if (text.isEmpty()) etOTP3.requestFocus()
                R.id.etOTP5 -> if (text.length == 1) etOTP6.requestFocus() else if (text.isEmpty()) etOTP4.requestFocus()
                R.id.etOTP6 -> if (text.isEmpty()) etOTP5.requestFocus()

            }
        }

    }
}