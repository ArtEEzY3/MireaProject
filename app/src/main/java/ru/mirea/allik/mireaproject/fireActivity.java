package ru.mirea.allik.mireaproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ru.mirea.allik.mireaproject.databinding.ActivityFireBinding;
import ru.mirea.allik.mireaproject.ui.profile.profileFragment;

public class fireActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityFireBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFireBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        sharedPref = getSharedPreferences("settings-allik", Context.MODE_PRIVATE);

        binding.emailEdit.setText(sharedPref.getString("EMAIL", ""));
        binding.passwordEdit.setText(sharedPref.getString("PASSWORD", ""));

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(binding.emailEdit.getText());
                String password = String.valueOf(binding.passwordEdit.getText());
                signIn(email, password, view);
            }
        });



        binding.createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(binding.emailEdit.getText());
                String password = String.valueOf(binding.passwordEdit.getText());
                createAccount(email, password, view);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
//            binding.emailText.setText(getString(R.string.emailpassword_status_fmt,user.getEmail(), user.isEmailVerified()));
//            binding.firebaseIDText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//            binding.emailEdit.setVisibility(View.GONE);
//            binding.passwordEdit.setVisibility(View.GONE);
//            binding.signInBtn.setVisibility(View.GONE);
//            binding.createAccBtn.setVisibility(View.GONE);
//            binding.signOutBtn.setVisibility(View.VISIBLE);
//            binding.verifyBtn.setVisibility(View.VISIBLE);
//            binding.verifyBtn.setEnabled(!user.isEmailVerified());

        } else {
//            binding.emailText.setText(R.string.signed_out);
//            binding.firebaseIDText.setText(null);
            binding.emailEdit.setVisibility(View.VISIBLE);
            binding.passwordEdit.setVisibility(View.VISIBLE);
            binding.signInBtn.setVisibility(View.VISIBLE);
            binding.createAccBtn.setVisibility(View.VISIBLE);
            binding.verifyBtn.setVisibility(View.GONE);
        }
    }

    private void createAccount(String email, String password, View view) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Trigger SMS verification
//                            requestSMSVerification(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(fireActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password, View view) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //
                            binding.emailEdit.setVisibility(View.GONE);
                            binding.passwordEdit.setVisibility(View.GONE);
                            binding.createAccBtn.setVisibility(View.GONE);
                            binding.signInBtn.setVisibility(View.GONE);
                            binding.phoneEdit.setVisibility(View.VISIBLE);
                            binding.phoneBtn.setVisibility(View.VISIBLE);
                            binding.phoneBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String number = String.valueOf(binding.phoneEdit.getText());
                                    requestSMSVerification(number);
                                }
                            });
                            // Trigger SMS verification
//                            requestSMSVerification(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(fireActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void requestSMSVerification(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,  // Phone number to verify
                60,                             // Timeout duration
                TimeUnit.SECONDS,               // Unit of timeout
                this,                           // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Auto-retrieval of SMS completed, sign-in the user
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // Verification failed, display an error message
                        String error = "Verification failed: " + e.getMessage();
                        Toast.makeText(fireActivity.this, error,
                                Toast.LENGTH_SHORT).show();
                        System.out.println(error);
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        // Code sent successfully, save the verification ID and launch the verification activity
                        // You can store the verificationId and token to retry sending later if needed
                        // For now, let's assume you have another activity for entering the code
                        binding.phoneBtn.setVisibility(View.GONE);
                        binding.phoneEdit.setVisibility(View.GONE);
                        binding.codeEdit.setVisibility(View.VISIBLE);
                        binding.verifyBtn.setVisibility(View.VISIBLE);

                        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String verificationCode = binding.codeEdit.getText().toString();
                                if (!TextUtils.isEmpty(verificationCode)) {
                                    // Verify the code
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                                    signInWithPhoneAuthCredential(credential);
                                } else {
                                    // Handle the case where the verification code is empty
                                    Toast.makeText(fireActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
//                        Intent intent = new Intent(fireActivity.this, VerifyCodeActivity.class);
//                        intent.putExtra("verificationId", verificationId);
//                        intent.putExtra("userPhoneNumber", user.getPhoneNumber());
//                        startActivity(intent);
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                            loadMain();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(fireActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void loadMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private ActivityFireBinding binding;
//    private FirebaseAuth mAuth;
//    private SharedPreferences sharedPref;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityFireBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//// [START initialize_auth] Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//        sharedPref = getSharedPreferences("settings-allik",	Context.MODE_PRIVATE);
//        binding.emailEdit.setText(sharedPref.getString("EMAIL", "null email"));
//        binding.passwordEdit.setText(sharedPref.getString("PASSWORD", "null password"));
//        String idid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
////        binding.idText2.setText(idid);
//
//        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = String.valueOf(binding.emailEdit.getText());
//                String password = String.valueOf(binding.passwordEdit.getText());
//                sha256(password);
//                signIn(email, password, view);
//
//            }
//        });
//        binding.createAccBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = String.valueOf(binding.emailEdit.getText());
//                String password = String.valueOf(binding.passwordEdit.getText());
//                createAccount(email, password, view);
//            }
//        });
//// [END initialize_auth]
//    }
//    // [START on_start_check_user]
//    @Override
//    public void onStart() {
//        super.onStart();
//// Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
////        profileFragment prof = new profileFragment();
////        binding.emailEdit.setText(prof.getEmailLog());
////        binding.passwordEdit.setText(prof.getPasLog());
//    }
//    // [END on_start_check_user]
//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
////            binding.emailText.setText(getString(R.string.emailpassword_status_fmt,user.getEmail(), user.isEmailVerified()));
////            binding.firebaseIDText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
////            binding.emailEdit.setVisibility(View.GONE);
////            binding.passwordEdit.setVisibility(View.GONE);
////            binding.signInBtn.setVisibility(View.GONE);
////            binding.createAccBtn.setVisibility(View.GONE);
////            binding.signOutBtn.setVisibility(View.VISIBLE);
////            binding.verifyBtn.setVisibility(View.VISIBLE);
////            binding.verifyBtn.setEnabled(!user.isEmailVerified());
//
//        } else {
////            binding.emailText.setText(R.string.signed_out);
////            binding.firebaseIDText.setText(null);
//            binding.emailEdit.setVisibility(View.VISIBLE);
//            binding.passwordEdit.setVisibility(View.VISIBLE);
//            binding.signInBtn.setVisibility(View.VISIBLE);
//            binding.createAccBtn.setVisibility(View.VISIBLE);
//            binding.signOutBtn.setVisibility(View.GONE);
//            binding.verifyBtn.setVisibility(View.GONE);
//        }
//    }
//
//    private void createAccount(String email, String password, View view) {
//        Log.d(TAG, "createAccount:" + email);
////        if (!validateForm()) {
////            return;
////        }
//// [START create_user_with_email]
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//// Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                            loadMain(view);
//                        } else {
//// If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure",
//                                    task.getException());
//                            Toast.makeText(fireActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//// [END create_user_with_email]
//    }
//
//
//    private void signIn(String email, String password, View view) {
//        Log.d(TAG, "signIn:" + email);
//// [START sign_in_with_email]
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//// Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                            loadMain(view);
//                        } else {
//// If sign in fails, display a message to the user.
//
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//
//                            Toast.makeText(fireActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//// [START_EXCLUDE]
//
//                        if (!task.isSuccessful()) {
//
////                            binding.emailText.setText(R.string.auth_failed);
//                        }
//
//// [END_EXCLUDE]
//
//                    }
//                });
//// [END sign_in_with_email]
//    }
//
//    public void loadMain(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }

    private void sha256(String password){
        int[] s0, s1, ch, temp1, temp2, maj;
//        String s_h0 = String.format("%32s", Long.toBinaryString(0x6a09e667f3bcc908L)).replace(' ', '0');
//        String s_h1 = String.format("%32s", Long.toBinaryString(0xbb67ae8584caa73bL)).replace(' ', '0');
//        String s_h2 = String.format("%32s", Long.toBinaryString(0x3c6ef372fe94f82bL)).replace(' ', '0');
//        String s_h3 = String.format("%32s", Long.toBinaryString(0xa54ff53a5f1d36f1L)).replace(' ', '0');
//        String s_h4 = String.format("%32s", Long.toBinaryString(0x510e527fade682d1L)).replace(' ', '0');
//        String s_h5 = String.format("%32s", Long.toBinaryString(0x9b05688c2b3e6c1fL)).replace(' ', '0');
//        String s_h6 = String.format("%32s", Long.toBinaryString(0x1f83d9abfb41bd6bL)).replace(' ', '0');
//        String s_h7 = String.format("%32s", Long.toBinaryString(0x5be0cd19137e2179L)).replace(' ', '0');

        int[] h0 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0x6a09e667f3bcc908L)).replace(' ', '0'));
        int[] h1 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0xbb67ae8584caa73bL)).replace(' ', '0'));
        int[] h2 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0x3c6ef372fe94f82bL)).replace(' ', '0'));
        int[] h3 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0xa54ff53a5f1d36f1L)).replace(' ', '0'));
        int[] h4 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0x510e527fade682d1L)).replace(' ', '0'));
        int[] h5 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0x9b05688c2b3e6c1fL)).replace(' ', '0'));
        int[] h6 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0x1f83d9abfb41bd6bL)).replace(' ', '0'));
        int[] h7 = convertStringToBitsArray(String.format("%64s", Long.toBinaryString(0x5be0cd19137e2179L)).replace(' ', '0'));


        long[] k = {
                0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
                0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
                0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
                0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
                0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
                0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
                0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
                0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
                0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
                0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
                0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
                0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
                0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
                0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
                0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
                0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
                0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
                0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
                0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
                0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
        };

        int[] a = h0;
        int[] b = h1;
        int[] c = h2;
        int[] d = h3;
        int[] e = h4;
        int[] f = h5;
        int[] g = h6;
        int[] h = h7;

        StringBuilder binarySB = new StringBuilder();
        for (char character : password.toCharArray()) {
            binarySB.append(
                    String.format("%8s", Integer.toBinaryString(character)).replace(' ', '0')
            );
        }
        String binaryS;
        //Добавление 1-индикатора
        binarySB.append('1');
        int currentLength = binarySB.length();
        //Определение количества незначащих нулей
        int additionalZeros = 1024 - (currentLength % 1024) - 128;
        //Определение наполнения 128 бит в конце с информацией о длине сообщения
        String bitRepresentation = String.format("%128s", Integer.toBinaryString(currentLength - 1)).replace(' ', '0');
        //3 штуки ниже - заполнение незначащими нулями и 128 битами и внесение их в строку сообщения
        for (int i = 0; i < additionalZeros; i++) {
            binarySB.append('0');
        }
        binarySB.append(bitRepresentation);
        binaryS = binarySB.toString();

        //делаем очередь сообщений. делим исходные 1024 бита на слова по 64, пихаем их в массив, остальные слова заполняем нулями
        int tmpArrayLength = 16;

        int[][] bitArray = new int[80][];

        for (int i = 0; i < tmpArrayLength; i++) {
            bitArray[i] =  convertStringToBitsArray(binarySB.substring(i * 64, (i + 1) * 64));
        }
        for (int i = tmpArrayLength; i < bitArray.length; i++) {
            bitArray[i] = convertStringToBitsArray("0000000000000000000000000000000000000000000000000000000000000000");
        }
//        System.out.println(bitArray.length);
        //заполнение новых слов в блоки
        for (int i = tmpArrayLength; i < bitArray.length; i++) {
            s0 = xorThreeArray(rightRotate(bitArray[i-15],  1), rightRotate(bitArray[i-15], 8), rightShift(bitArray[i-15],  7));
            s1 = xorThreeArray(rightRotate(bitArray[i-2], 19), rightRotate(bitArray[i-2], 61), rightShift(bitArray[i-2], 6));
            bitArray[i] = addMod64(bitArray[i-16], s0, bitArray[i-7], s1);
        }

        //Сжатие
        for (int i = 0; i < bitArray.length; i++) {
            s1 = xorThreeArray(rightRotate(e, 14), rightRotate(e, 18), rightRotate(e, 41));
            ch = xorTwoArray(andBitStrings(e, f), andBitStrings(notBitString(e), g));
            temp1 = addMod64(h, s1, ch, convertStringToBitsArray(String.format("%64s", Long.toBinaryString(k[i])).replace(' ', '0')), bitArray[i]);
            s0 = xorThreeArray(rightRotate(a, 28), rightRotate(a, 34), rightRotate(a, 39));
            maj = xorThreeArray(andBitStrings(a, b), andBitStrings(a, c), andBitStrings(b, c));
            temp2 = addMod64(s0, maj);

            h = g;
            g = f;
            f = e;
            e = addMod64(d, temp1);
            d = c;
            c = b;
            b = a;
            a = addMod64(temp1, temp2);
        }

        String s_h0 = binaryToHex(addMod64(h0, a));
        String s_h1 = binaryToHex(addMod64(h1, b));
        String s_h2 = binaryToHex(addMod64(h2, c));
        String s_h3 = binaryToHex(addMod64(h3, d));
        String s_h4 = binaryToHex(addMod64(h4, e));
        String s_h5 = binaryToHex(addMod64(h5, f));
        String s_h6 = binaryToHex(addMod64(h6, g));
        String s_h7 = binaryToHex(addMod64(h7, h));

        String hash = s_h0 + s_h1 + s_h2 + s_h3 + s_h4 + s_h5 + s_h6 + s_h7;
        Log.w(TAG, "Hash: " + hash);
    }


    public static int[] convertStringToBitsArray(String s) {
        int[] bitsArray = new int[s.length()];

        for (int i = 0; i < s.length(); i++) {
            // Преобразуем символ в число и помещаем его в массив
            bitsArray[i] = Character.getNumericValue(s.charAt(i));
        }

        return bitsArray;
    }



    private static int[] rightShift(int[] array, int positions) {
        int length = array.length;
        int[] shiftedArray = new int[length];

        for (int i = 0; i < length; i++) {
            int newPosition = (i + positions);
            if (newPosition < length){
                shiftedArray[newPosition] = array[i];
            }
            if(i<positions){
                shiftedArray[i] = 0;
            }
        }

        return shiftedArray;
    }

    public static int[] xorThreeArray(int[] array1, int[] array2, int[] array3) {
        int length = Math.min(Math.min(array1.length, array2.length), array3.length);
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = array1[i] ^ array2[i] ^ array3[i];
        }

        return result;
    }

    public static int[] xorTwoArray(int[] array1, int[] array2) {
        int length = Math.min(array1.length, array2.length);
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = array1[i] ^ array2[i];
        }

        return result;
    }

    public static int[] rightRotate(int[] array, int positions) {
        int length = array.length;
        int[] rotatedArray = new int[length];

        for (int i = 0; i < length; i++) {
            int newPosition = (i + positions) % length;
            rotatedArray[newPosition] = array[i];
        }

        return rotatedArray;
    }

//    private static Long rightRotate(String str, int amount) {
//        if (str == null || str.length() == 0 || amount < 0) {
//            return str;
//        }
//        String end = str.substring(str.length() - amount);
//        String start = str.substring(0, str.length() - amount);
//
//        return end + start;
//    }

//    private static String xorBS(String str1, String str2) {
//        if (str1 == null || str2 == null || str1.length() != str2.length()) {
//            throw new IllegalArgumentException("Strings must be non-null and of equal length");
//        }
//
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < str1.length(); i++) {
//            char bit1 = str1.charAt(i);
//            char bit2 = str2.charAt(i);
//            char xor = (bit1 == bit2) ? '0' : '1';
//            result.append(xor);
//        }
//
//        return result.toString();
//    }

    public static int[] addMod64(int[]... arrays) {
        BigInteger sum = BigInteger.ZERO;
        BigInteger modulo = BigInteger.ONE.shiftLeft(64);

        for (int[] array : arrays) {
            BigInteger bigInteger = new BigInteger(arrayToBinaryString(array), 2);
            sum = sum.add(bigInteger);
        }

        String resultBinaryString = String.format("%64s", sum.mod(modulo).toString(2)).replace(' ', '0');
        return convertStringToBitsArray(resultBinaryString);
    }

    public static String arrayToBinaryString(int[] array) {
        StringBuilder binaryString = new StringBuilder();
        for (int value : array) {
            binaryString.append(value);
        }
        return binaryString.toString();
    }


    public static int[] andBitStrings(int[] array1, int[] array2) {
        int length = Math.min(array1.length, array2.length);
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = array1[i] & array2[i];
        }

        return result;
    }
//    private static String andBitStrings(String str1, String str2) {
//        if (str1 == null || str2 == null || str1.length() != 32 || str2.length() != 32) {
//            throw new IllegalArgumentException("Strings must be non-null and 32 bits in length");
//        }
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < 32; i++) {
//            char bit1 = str1.charAt(i);
//            char bit2 = str2.charAt(i);
//            char and = (bit1 == '1' && bit2 == '1') ? '1' : '0';
//            result.append(and);
//        }
//
//        return result.toString();
//    }

    public static int[] notBitString(int[] array) {
        int length = array.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = (array[i] == 1) ? 0 : 1;
        }

        return result;
    }
//    private static String notBitString(String str) {
//        if (str == null || str.length() != 32) {
//            throw new IllegalArgumentException("String must be non-null and 32 bits in length");
//        }
//
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < 32; i++) {
//            char bit = str.charAt(i);
//
//            // Perform NOT operation
//            char notBit = (bit == '1') ? '0' : '1';
//            result.append(notBit);
//        }
//
//        return result.toString();
//    }


    public static String binaryToHex(int[] array) {
        if (array.length != 64) {
            throw new IllegalArgumentException("Array must be 64 bits in length");
        }

        StringBuilder binaryString = new StringBuilder();
        for (int value : array) {
            binaryString.append(value);
        }

        BigInteger number = new BigInteger(binaryString.toString(), 2);
        return String.format("%016X", number);
    }
//    private static String binaryToHex(String binaryString) {
//        if (binaryString == null || binaryString.length() != 64) {
//            throw new IllegalArgumentException("String must be non-null and 32 bits in length");
//        }
//        long number = Long.parseLong(binaryString, 2);
//        return String.format("%08X", number);
//    }
}

