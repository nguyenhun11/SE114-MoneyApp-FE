package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.response.UserProfileResponse;
import com.example.moneyapp.data.repository.UserRepository;
import com.example.moneyapp.model.User;
import com.example.moneyapp.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public final MutableLiveData<User> loginSuccess = new MutableLiveData<>();
    public final MutableLiveData<User> registerSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> resetPasswordSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        userRepository = new UserRepository(application);
    }

    public void login(String loginInput, String password){
        if (loginInput == null || loginInput.trim().isEmpty() || !loginInput.contains("@")){
            errorMessage.setValue("Email không hợp lệ!");
            return;
        }
        if (password == null || password.trim().isEmpty()){
            errorMessage.setValue("Mật khẩu không được để trống");
            return;
        }

        isLoading.setValue(true);
        AuthRepository.AuthCallback<Integer> callback = new AuthRepository.AuthCallback<Integer>() {
            @Override
            public void onSuccess(Integer userId) {
                userRepository.getUserProfile(new UserRepository.UserCallback<UserProfileResponse>() {
                    @Override
                    public void onSuccess(UserProfileResponse response) {
                        User completeUser = PreferenceManager.getInstance(getApplication()).getCurrentUser();
                        loginSuccess.postValue(completeUser);
                        isLoading.postValue(false);
                    }
                    @Override
                    public void onError(String message) { // Đăng nhập thành công nhưng lấy thông tin không thành cong
                        User basicUser = PreferenceManager.getInstance(getApplication()).getCurrentUser();
                        loginSuccess.postValue(basicUser);
                        isLoading.postValue(false);
                    }
                });
            }
            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        };

        authRepository.loginByEmail(loginInput, password, callback);
    }

    public void register(
            String name,
            String email,
            String password,
            String confirmPassword
    ){
        if (email == null || email.trim().isEmpty()){
            errorMessage.setValue("Invalid email");
            return;
        }
        if (password == null || password.trim().isEmpty()){
            errorMessage.setValue("Invalid password");
            return;
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()){
            errorMessage.setValue("Invalid confirm password");
            return;
        }
        if (!password.equals(confirmPassword)){
            errorMessage.setValue("Passwords do not match");
            return;
        }

        isLoading.setValue(true);
        AuthRepository.AuthCallback<Integer> callback = new AuthRepository.AuthCallback<Integer>() {
            @Override
            public void onSuccess(Integer userId) {
                userRepository.getUserProfile(new UserRepository.UserCallback<>() {
                    @Override
                    public void onSuccess(UserProfileResponse response) {
                        User user = PreferenceManager.getInstance(getApplication()).getCurrentUser();
                        registerSuccess.postValue(user);
                        isLoading.postValue(false);
                    }
                    @Override
                    public void onError(String message) { // Đăng ký thành công nhưng lấy thông tin không thành cong
                        User user = PreferenceManager.getInstance(getApplication()).getCurrentUser();
                        registerSuccess.postValue(user);
                        isLoading.postValue(false);
                    }
                });
            }
            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        };

        authRepository.register(name, email, password, callback);
    }

    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập email");
            return;
        }
        isLoading.setValue(true);
        authRepository.sendPasswordResetEmail(email, new AuthRepository.AuthCallback<>() {
            @Override
            public void onSuccess(String message) {
                resetPasswordSuccess.postValue(true);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }
}