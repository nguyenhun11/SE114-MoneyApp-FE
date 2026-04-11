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

    public void updatePassword(String oldPass, String newPass, String confirmPass) {
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            errorMessage.setValue("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        if (newPass.length() < 6) {
            errorMessage.setValue("Mật khẩu mới phải từ 6 ký tự");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            errorMessage.setValue("Mật khẩu xác nhận không khớp");
            return;
        }

        String userID = PreferenceManager.getInstance(context).getUserID();
        authRepository.updatePassword(userID, oldPass, newPass, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                errorMessage.postValue("SUCCESS");
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void updateUserName(String newName) {
        User user = currentUser.getValue();
        if (user != null && !newName.isEmpty()) {
            user.setName(newName);
            authRepository.updateUser(user, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User updatedUser) {
                    currentUser.postValue(updatedUser);
                }

                @Override
                public void onError(String message) {
                    errorMessage.postValue(message);
                }
            });
        }
    }
}
