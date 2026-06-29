package com.example.moneyapp.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.local.PreferenceManager;
import com.example.moneyapp.data.remote.request.CheckInRequest;
import com.example.moneyapp.data.remote.response.CheckInResponse;
import com.example.moneyapp.data.remote.response.CityResponse;
import com.example.moneyapp.model.User;
import com.example.moneyapp.data.remote.request.UserProfileRequest;
import com.example.moneyapp.data.remote.response.UserProfileResponse;
import com.example.moneyapp.data.repository.AuthRepository;
import com.example.moneyapp.data.repository.CityRepository;
import com.example.moneyapp.data.repository.UserRepository;
import com.example.moneyapp.utils.DateConverter;

import java.util.Date;

public class ProfileViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    public final MutableLiveData<User> currentUser = new MutableLiveData<>();
    public final MutableLiveData<CityResponse> cityData = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Biến quản lý Skeleton Loading bạn đã thêm rất chuẩn!
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        authRepository = new AuthRepository(application);
        userRepository = new UserRepository(context);
        cityRepository = new CityRepository(context);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchUserData() {
        // 💥 BẬT SKELETON TRƯỚC KHI GỌI API
        isLoading.setValue(true);

        fetchCityData();
        userRepository.getUserProfile(new UserRepository.UserCallback<UserProfileResponse>() {
            @Override
            public void onSuccess(UserProfileResponse response) {
                String defaultCurrency = response.getDefaultCurrency() != null ? response.getDefaultCurrency() : "VND";
                PreferenceManager.getInstance(getApplication()).setDefaultCurrency(defaultCurrency);

                User user = new User(
                        response.getId(),
                        response.getName(),
                        response.getEmail(),
                        response.getPhoneNumber(),
                        response.getImageUrl(),
                        response.getDailyStreak(),
                        response.isTodayCheckedIn(),
                        defaultCurrency,
                        DateConverter.convertStringToDate(response.getCreatedAt()),
                        DateConverter.convertStringToDate(response.getLastUpdatedAt())
                );
                currentUser.postValue(user);

                // 💥 TẮT SKELETON KHI TẢI THÀNH CÔNG
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);

                // 💥 TẮT SKELETON KỂ CẢ KHI LỖI ĐỂ TRÁNH BỊ KẸT UI
                isLoading.postValue(false);
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
                    user.getPhoneNumber(),
                    user.getDefaultCurrency()
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
                    user.getPhoneNumber(),
                    user.getDefaultCurrency()
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

    public void updateDefaultCurrency(String newCurrency) {
        User user = currentUser.getValue();
        if (user != null && !newCurrency.isEmpty()) {
            UserProfileRequest request = new UserProfileRequest(
                    user.getName(),
                    user.getEmail(),
                    user.getProfileImageUrl(),
                    user.getPhoneNumber(),
                    newCurrency
            );

            userRepository.updateUserProfile(request, new UserRepository.UserCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    user.setDefaultCurrency(newCurrency);
                    currentUser.postValue(user);
                    PreferenceManager.getInstance(getApplication()).setDefaultCurrency(newCurrency);
                    errorMessage.postValue("SUCCESS_CURRENCY");
                }

                @Override
                public void onError(String message) {
                    errorMessage.postValue(message);
                }
            });
        }
    }

    public void deleteAccount() {
        userRepository.deleteUser("permanent", new UserRepository.UserCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                PreferenceManager.getInstance(getApplication()).clear();
                errorMessage.postValue("SUCCESS_DELETE");
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void fetchCityData() {
        cityRepository.getCity().enqueue(new retrofit2.Callback<CityResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CityResponse> call, retrofit2.Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    cityData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CityResponse> call, Throwable t) {
                // Ignore error for simplicity or post error message
            }
        });
    }

    public void checkInToday() {
        String todayString = DateConverter.convertDateToString(new Date());
        CheckInRequest request = new CheckInRequest(todayString);

        userRepository.checkIn(request, new UserRepository.UserCallback<CheckInResponse>() {
            @Override
            public void onSuccess(CheckInResponse response) {
                User user = currentUser.getValue();
                if (user != null) {
                    user.setDailyStreak(response.getCurrentStreak());
                    user.setTodayCheckedIn(true);
                    currentUser.postValue(user);
                    errorMessage.postValue("CHECKIN_MSG:" + response.getMessage());

                    // Cập nhật lại điểm thành phố sau khi check-in
                    fetchCityData();
                }
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void restoreStreak() {
        String todayString = com.example.moneyapp.utils.DateConverter.convertDateToString(new java.util.Date());
        com.example.moneyapp.data.remote.request.CheckInRequest request =
                new com.example.moneyapp.data.remote.request.CheckInRequest(todayString);

        userRepository.restoreStreak(request, new com.example.moneyapp.data.repository.UserRepository.UserCallback<com.example.moneyapp.data.remote.response.CheckInResponse>() {
            @Override
            public void onSuccess(com.example.moneyapp.data.remote.response.CheckInResponse response) {
                User user = currentUser.getValue();
                if (user != null) {
                    user.setDailyStreak(response.getCurrentStreak());
                    user.setTodayCheckedIn(true);
                    currentUser.postValue(user);
                    errorMessage.postValue("SUCCESS_RESTORE:" + response.getMessage());

                    // Cập nhật lại điểm thành phố sau khi khôi phục chuỗi
                    fetchCityData();
                }
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
}