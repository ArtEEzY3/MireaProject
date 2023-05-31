package ru.mirea.allik.mireaproject.ui.filework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ru.mirea.allik.mireaproject.databinding.ActivityEncryptionBinding;

public class encryptionActivity extends AppCompatActivity {
    private ActivityEncryptionBinding binding;
    private String fileName = "mirea.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEncryptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String line = (String) getIntent().getSerializableExtra("line");
        SecretKey key = generateKey();
//        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        byte[] shiper = encryptMsg(line,key);
        String str = new String(shiper,StandardCharsets.UTF_8);
        binding.nameEditText.setText(fileName);
        binding.quoteEditText.setText(str);
//        binding.keyEdit.setText(encodedKey);

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExternalStorageWritable()) {
                    writeFileToExternalStorage();
                }
            }
        });
        binding.loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExternalStorageReadable()) readFileFromExternalStorage();
            }
        });
//        binding.decryptBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                byte[] chars = String.valueOf(binding.quoteEditText.getText()).getBytes();
//                String decLine = decryptMsg(chars,key);
//                binding.quoteEditText.setText(decLine);
//            }
//        });
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    /* Проверяем внешнее хранилище на доступность чтения */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void writeFileToExternalStorage()	{
        File path =	Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file =	new	File(path,	binding.nameEditText.getText() + ".txt");
        try	{
            FileOutputStream fileOutputStream =	new	FileOutputStream(file.getAbsoluteFile());
            OutputStreamWriter output =	new	OutputStreamWriter(fileOutputStream);
            //	Запись строки в файл
            output.write(String.valueOf(binding.quoteEditText.getText()));
            //	Закрытие потока записи
            output.close();

        }	catch	(IOException e)	{
            Log.w("ExternalStorage",	"Error	writing	"	+	file,	e);
        }
    }

    public void	readFileFromExternalStorage() {
        File path =	Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file =	new	File(path,binding.nameEditText.getText() + ".txt");
        try	{
            FileInputStream fileInputStream	= new	FileInputStream(file.getAbsoluteFile());
            InputStreamReader inputStreamReader	= new	InputStreamReader(fileInputStream,	StandardCharsets.UTF_8);
//            List<String> lines = new ArrayList<String>();
            BufferedReader reader =	new BufferedReader(inputStreamReader);
            String line	= reader.readLine();
//            while (line != null) {
//                lines.add(line);
//                line = reader.readLine();
//            }
            Log.w("ExternalStorage", String.format("Read from file %s successful",	line));
            binding.quoteEditText.setText(line);
        }	catch (Exception	e) {
            Log.w("ExternalStorage", String.format("Read from file %s failed",	e.getMessage()));
        }
    }

    public	static SecretKey generateKey(){
        try	{
            SecureRandom sr	= SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any	data	used	as	random	seed".getBytes());
            KeyGenerator kg	= KeyGenerator.getInstance("AES");
            kg.init(256,	sr);
            return	new SecretKeySpec((kg.generateKey()).getEncoded(),	"AES");
        }	catch	(NoSuchAlgorithmException e)	{
            throw	new	RuntimeException(e);
        }
    }

    public	static	byte[]	encryptMsg(String	message,	SecretKey	secret)	{
        Cipher cipher	=	null;
        try	{
            cipher	=	Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,	secret);
            return	cipher.doFinal(message.getBytes());
        }	catch	(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                       BadPaddingException | IllegalBlockSizeException e)	{
            throw	new	RuntimeException(e);
        }
    }
//    public static String decryptMsg(byte[] cipherText, SecretKey secret){
//        /* Decrypt the message */
//        try {
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.DECRYPT_MODE, secret);
//            return new String(cipher.doFinal(cipherText));
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
//                 | BadPaddingException | InvalidKeyException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    byte[] cryptText = args.getByteArray(ARG_WORD);
//    byte[] key = args.getByteArray("key");
//    SecretKey originalKey	=	new SecretKeySpec(key,	0,	key.length,	"AES");
//    firstName = decryptMsg(cryptText, originalKey);
}