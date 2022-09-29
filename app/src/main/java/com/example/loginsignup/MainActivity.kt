package com.example.loginsignup


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // this stores the phone number of the user
    var phoneNumber :String = ""

    // create instance of firebase auth
     lateinit var auth: FirebaseAuth

    // we will use this to match the sent otp from firebase
   lateinit var otpVerification : String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        
        auth = FirebaseAuth.getInstance()
        
        
        val requestOTP = findViewById<Button>(R.id.btnSubmit)
        
        requestOTP.setOnClickListener{
                login()
            }


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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
                Log.w(TAG, "onVerificationFailed", e)




                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                otpVerification = verificationId
                resendToken = token

                val intent = Intent(applicationContext,EnterOTP::class.java)
                intent.putExtra("OTP",otpVerification)
                intent.putExtra("resendToken",resendToken)
                intent.putExtra("phoneNumber", phoneNumber)

                startActivity(intent)

            }
        }

    }

   private fun login() {
       val number = findViewById<EditText>(R.id.etPhoneNum)

       phoneNumber = number.text.trim().toString()

       if (phoneNumber.isNullOrEmpty()){
           Toast.makeText(this,"Please enter your number", Toast.LENGTH_LONG).show()
       } else{
           phoneNumber = "+91$phoneNumber"

           sendVerificationCode(phoneNumber)

       }
   }

    private fun sendVerificationCode(number: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    override fun onStart(){
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this,DashBoard::class.java))
        }

    }

}

