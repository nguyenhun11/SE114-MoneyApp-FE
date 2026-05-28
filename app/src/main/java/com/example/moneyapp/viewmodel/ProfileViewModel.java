package com.example.moneyapp.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.model.User;
import com.example.moneyapp.data.remote.request.UserProfileRequest;
import com.example.moneyapp.data.remote.response.UserProfileResponse;
import com.example.moneyapp.data.repository.AuthRepository;
import com.example.moneyapp.data.repository.UserRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    public final MutableLiveData<User> currentUser = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        authRepository = new AuthRepository(application);
        userRepository = new UserRepository(context);
    }

    public void fetchUserData() {
        userRepository.getUserProfile(new UserRepository.UserCallback<UserProfileResponse>() {
            @Override
            public void onSuccess(UserProfileResponse response) {
                // Map API response to local User entity for UI compatibility
                User user = new User(
                        String.valueOf(response.getId()),
                        response.getName(),
                        response.getEmail(),
                        response.getPhoneNumber(),
                        response.getImageUrl(),
                        response.getDailyStreak(),
                        response.isTodayCheckedIn()
                );
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

        authRepository.changePassword(oldPass, newPass, new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
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
            UserProfileRequest request = new UserProfileRequest(
                    newName,
                    user.getEmail(),
                    user.getProfileImageUrl(),
                    user.getPhoneNumber()
            );

            userRepository.updateUserProfile(request, new UserRepository.UserCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    user.setName(newName);
                    currentUser.postValue(user);
                    errorMessage.postValue("SUCCESS");
                }

                @Override
                public void onError(String message) {
                    errorMessage.postValue(message);
                }
            });
        }
    }

    public void updateProfileImage(String imageUri) {
        User user = currentUser.getValue();
        if (user != null) {
            UserProfileRequest request = new UserProfileRequest(
                    user.getName(),
                    user.getEmail(),
                    imageUri,
                    user.getPhoneNumber()
            );

            userRepository.updateUserProfile(request, new UserRepository.UserCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    user.setProfileImageUrl(imageUri);
                    currentUser.postValue(user);
                    errorMessage.postValue("SUCCESS_IMAGE");
                }

                @Override
                public void onError(String message) {
                    errorMessage.postValue(message);
                }
            });
        }
    }

    public void deleteAccount() {
        // Mode can be "permanent" or "soft" based on your API logic
        userRepository.deleteUser("permanent", new UserRepository.UserCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                errorMessage.postValue("SUCCESS_DELETE");
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}
