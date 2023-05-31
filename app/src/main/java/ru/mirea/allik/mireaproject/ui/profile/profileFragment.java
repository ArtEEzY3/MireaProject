package ru.mirea.allik.mireaproject.ui.profile;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mirea.allik.mireaproject.R;
import ru.mirea.allik.mireaproject.databinding.FragmentCameraBinding;
import ru.mirea.allik.mireaproject.databinding.FragmentProfileBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
    public profileFragment() {
        // Required empty public constructor
//        String email = sharedPref.getString("EMAIL", "no email");
//        String password = sharedPref.getString("PASSWORD", "no password");
    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment profileFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static profileFragment newInstance(String param1, String param2) {
//        profileFragment fragment = new profileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    private FragmentProfileBinding binding;
    private Context mContext;
    private SharedPreferences sharedPref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mContext = inflater.getContext();
        sharedPref = mContext.getSharedPreferences("settings-allik",	Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("EMAIL", String.valueOf(binding.emailEdit.getText()));
                editor.putString("PASSWORD", String.valueOf(binding.pasEdit.getText()));
                editor.apply();
//                Log.d(TAG, getEmailLog());
//                Log.d(TAG, getPasLog());
            }
        });
        Log.d(TAG, getEmailLog());
        Log.d(TAG, getPasLog());
        return binding.getRoot();
    }

    public String getEmailLog(){
        return sharedPref.getString("EMAIL", "null email");
    }
    public String getPasLog(){
        return sharedPref.getString("PASSWORD", "null password");
    }
}