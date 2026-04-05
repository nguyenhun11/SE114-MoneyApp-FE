package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.local.entity.User;
import com.example.moneyapp.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    // Dữ liệu gửi lên UI
    public final MutableLiveData<User> loginSuccess = new MutableLiveData<>();
    public final MutableLiveData<User> registerSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void login(String loginInput, String password){
        if (loginInput == null || loginInput.trim().isEmpty()){
            errorMessage.setValue("Invalid email or phone number");
            return;
        }
        if (password == null || password.trim().isEmpty()){
            errorMessage.setValue("Invalid password");
            return;
        }
        // setValue dùng cho luồng chính, postValue dùng cho luồng phụ (exec)
        isLoading.setValue(true);
        AuthRepository.AuthCallback callback = new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                loginSuccess.postValue(user);
                isLoading.postValue(false);
            }
            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        };

        if (loginInput.contains("@")){
            authRepository.loginByEmail(loginInput, password, callback);
        } else {
            authRepository.loginByPhoneNumber(loginInput, password, callback);
        }
    }
}