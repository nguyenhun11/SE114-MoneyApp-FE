package com.example.moneyapp.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.data.repository.AuthRepository;
import com.example.moneyapp.utils.PreferenceManager;

public class ProfileViewModel extends AndroidViewModel {
    private final Context context;
    private final AuthRepository authRepository;
    public final MutableLiveData<User> currentUser = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        authRepository = new AuthRepository(application);
    }

    public void fetchUserData(){
        String userID = PreferenceManager.getInstance(context).getUserID();
        authRepository.getUserByID(userID, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.postValue(user);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
