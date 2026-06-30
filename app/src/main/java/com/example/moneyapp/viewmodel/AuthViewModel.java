package com.example.moneyapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.moneyapp.data.remote.response.UserProfileResponse;
import com.example.moneyapp.data.repository.UserRepository;
import com.example.moneyapp.model.User;
import com.example.moneyapp.data.repository.AuthRepository;
import com.example.moneyapp.utils.DateConverter;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public final MutableLiveData<User> loginSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> resetPasswordSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> resetCompleteSuccess = new MutableLiveData<>();
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
                        // 🌟 Dùng trực tiếp dữ liệu từ API trả về
                        User user = mapToUser(response);
                        loginSuccess.postValue(user);
                        isLoading.postValue(false);
                    }
                    @Override
                    public void onError(String message) {
                        // 🌟 Xử lý lỗi triệt để: Không còn SharePref để fallback, nên báo lỗi luôn
                        errorMessage.postValue("Đăng nhập thành công nhưng lỗi tải thông tin: " + message);
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

    public void loginWithGoogle(String idToken) {
        isLoading.setValue(true);
        AuthRepository.AuthCallback<Integer> callback = new AuthRepository.AuthCallback<Integer>() {
            @Override
            public void onSuccess(Integer userId) {
                userRepository.getUserProfile(new UserRepository.UserCallback<UserProfileResponse>() {
                    @Override
                    public void onSuccess(UserProfileResponse response) {
                        User user = mapToUser(response);
                        loginSuccess.postValue(user);
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.postValue("Đăng nhập Google thành công nhưng lỗi tải thông tin: " + message);
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

        authRepository.loginByGoogle(idToken, callback);
    }

    public void register(
            String name,
            String email,
            String password,
            String confirmPassword
    ){
        if (name == null || name.trim().isEmpty()){
            errorMessage.setValue("Họ và tên không được để trống");
            return;
        }
        if (name.trim().length() < 2){
            errorMessage.setValue("Họ và tên phải từ 2 ký tự trở lên");
            return;
        }
        if (name.trim().length() > 50){
            errorMessage.setValue("Họ và tên không được vượt quá 50 ký tự");
            return;
        }
        if (!name.trim().matches("^[\\p{L}\\s'-]+$")){
            errorMessage.setValue("Họ và tên chỉ được chứa chữ cái và khoảng trắng");
            return;
        }
        if (email == null || email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorMessage.setValue("Email không hợp lệ");
            return;
        }
        if (password == null || password.length() < 8){
            errorMessage.setValue("Mật khẩu phải từ 8 ký tự trở lên");
            return;
        }
        if (!password.matches(".*[A-Z].*")) {
            errorMessage.setValue("Mật khẩu phải chứa ít nhất 1 chữ cái in hoa");
            return;
        }
        if (!password.matches(".*[a-z].*")) {
            errorMessage.setValue("Mật khẩu phải chứa ít nhất 1 chữ cái thường");
            return;
        }
        if (!password.matches(".*\\d.*")) {
            errorMessage.setValue("Mật khẩu phải chứa ít nhất 1 chữ số");
            return;
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            errorMessage.setValue("Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt");
            return;
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()){
            errorMessage.setValue("Xác nhận mật khẩu không hợp lệ");
            return;
        }
        if (!password.equals(confirmPassword)){
            errorMessage.setValue("Mật khẩu không khớp");
            return;
        }

        isLoading.setValue(true);
        AuthRepository.AuthCallback<Integer> callback = new AuthRepository.AuthCallback<Integer>() {
            @Override
            public void onSuccess(Integer userId) {
                // 🌟 Chỉ cần báo thành công bằng Boolean để quay về màn hình đăng nhập
                registerSuccess.postValue(true);
                isLoading.postValue(false);
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
        authRepository.sendPasswordResetEmail(email, new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
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

    public void completeResetPassword(String email, String token, String newPassword, String confirmPassword) {
        if (token == null || token.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập mã xác nhận");
            return;
        }
        if (newPassword == null || newPassword.length() < 6) {
            errorMessage.setValue("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setValue("Mật khẩu xác nhận không khớp");
            return;
        }

        isLoading.setValue(true);
        authRepository.resetPassword(email, token, newPassword, new AuthRepository.AuthCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                resetCompleteSuccess.postValue(true);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private User mapToUser(UserProfileResponse response) {
        return new User(
                response.getId(),
                response.getName(),
                response.getEmail(),
                response.getPhoneNumber(),
                response.getImageUrl(),
                response.getDailyStreak(),
                response.isTodayCheckedIn(),
                response.getDefaultCurrency(),
                DateConverter.convertStringToDate(response.getCreatedAt()),
                DateConverter.convertStringToDate(response.getLastUpdatedAt())
        );
    }
}