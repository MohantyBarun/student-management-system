package com.student.studentservice.service;
import com.student.studentservice.dto.*;
import com.student.studentservice.entity.LoginSecurity;
import com.student.studentservice.entity.RefreshToken;
import com.student.studentservice.entity.User;
import com.student.studentservice.exception.ResourceNotFoundException;
import com.student.studentservice.repository.LoginSecurityRepository;
import com.student.studentservice.repository.RefreshTokenRepository;
import com.student.studentservice.repository.UserRepository;
import com.student.studentservice.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final LoginSecurityRepository loginSecurityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public LoginResponseDTO userLogin(LoginRequestDTO loginRequestDTO){
        log.info("Login attempted for "+ loginRequestDTO.getEmail());
        // First we have to check if user exists or not
        User user= userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(()-> new ResourceNotFoundException("No account found with email: "+ loginRequestDTO.getEmail()));

        // Step 2 - Get login security record
        LoginSecurity loginSecurity = loginSecurityRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Login security record not found for user: " +loginRequestDTO.getEmail()));

        // Step 3 - Check if account is permanently blocked
        if (loginSecurity.getPermanentBlock()) {
            log.warn("Permanently blocked account login attempt: {}", loginRequestDTO.getEmail());
            throw new RuntimeException("Account permanently blocked. Please contact admin.");
        }

        // Step 4 - Check if account is temporarily blocked
        if (loginSecurity.getAccountStatus() == AccountStatus.BLOCKED) {
            if (LocalDateTime.now().isBefore(loginSecurity.getBlockExpiryAt())) {
                log.warn("Temporarily blocked account login attempt: {}", loginRequestDTO.getEmail());
                throw new RuntimeException("Account temporarily blocked. Try again after: "
                        + loginSecurity.getBlockExpiryAt());
            } else {
                // auto unblock — block duration has passed
                log.info("Auto unblocking account: {}", loginRequestDTO.getEmail());
                loginSecurity.setAccountStatus(AccountStatus.ACTIVE);
                loginSecurity.setWrongPasswordCount(0);
                loginSecurity.setUnblockedAt(LocalDateTime.now());
                loginSecurityRepository.save(loginSecurity);
            }
        }

        // Step 5 - Check if account is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated. Please contact admin.");
        }

        // Step 6 - Verify password
        boolean isPasswordValid = false;

        // Step 6a - Check if first time login (temp password)
        if (user.getIsFirstTimeLogin()) {
            log.info("First time login attempt for: {}", loginRequestDTO.getEmail());

            // check temp password expiry
            if (LocalDateTime.now().isAfter(user.getTempPasswordExpiry())) {
                throw new RuntimeException("Temp password expired. Please contact admin.");
            }

            // check temp password attempts
            if (user.getTempPasswordAttemptsCount() >= 3) {
                throw new RuntimeException("Max temp password attempts exceeded. Contact admin.");
            }

            // verify temp password
            isPasswordValid = passwordEncoder.matches(
                    loginRequestDTO.getPassword(), user.getTempPassword());

            if (!isPasswordValid) {
                // increment temp password attempts
                user.setTempPasswordAttemptsCount(user.getTempPasswordAttemptsCount() + 1);
                userRepository.save(user);
                log.warn("Wrong temp password for: {} attempt: {}",
                        loginRequestDTO.getEmail(), user.getTempPasswordAttemptsCount());
                throw new RuntimeException("Invalid temp password. Attempts remaining: "
                        + (3 - user.getTempPasswordAttemptsCount()));
            }

        } else {
            // Step 6b - Regular password check
            isPasswordValid = passwordEncoder.matches(
                    loginRequestDTO.getPassword(), user.getPassword());

            if (!isPasswordValid) {
                // increment wrong password count
                int wrongCount = loginSecurity.getWrongPasswordCount() + 1;
                loginSecurity.setWrongPasswordCount(wrongCount);
                loginSecurity.setLastFailedAt(LocalDateTime.now());

                // check if should block
                if (wrongCount > 10) {
                    // permanently block
                    loginSecurity.setPermanentBlock(true);
                    loginSecurity.setAccountStatus(AccountStatus.PERMANENTLY_BLOCKED);
                    loginSecurity.setBlockedAt(LocalDateTime.now());
                    loginSecurity.setBlockReason("Exceeded max wrong password attempts (10)");
                    log.warn("Account permanently blocked: {}", loginRequestDTO.getEmail());
                } else if (wrongCount >= 5) {
                    // temporarily block for 30 mins
                    loginSecurity.setAccountStatus(AccountStatus.BLOCKED);
                    loginSecurity.setBlockedAt(LocalDateTime.now());
                    loginSecurity.setBlockExpiryAt(LocalDateTime.now().plusMinutes(30));
                    loginSecurity.setBlockReason("5 wrong password attempts");
                    log.warn("Account temporarily blocked: {}", loginRequestDTO.getEmail());
                }

                loginSecurityRepository.save(loginSecurity);
                log.warn("Wrong password for: {} count: {}", loginRequestDTO.getEmail(), wrongCount);
                throw new RuntimeException("Invalid password. Attempts remaining: "
                        + (5 - wrongCount));
            }
        }

        // Step 7 - Password correct — reset wrong count
        loginSecurity.setWrongPasswordCount(0);
        loginSecurity.setLastLoginAt(LocalDateTime.now());
        loginSecurityRepository.save(loginSecurity);

        // Step 8 - Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String refreshTokenString = jwtUtil.generateRefreshToken(user.getEmail());

// Step 8a - Save refresh token to DB
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenString);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setIsRevoked(false);
        refreshTokenRepository.save(refreshToken);

        log.info("Login successful for: {}", loginRequestDTO.getEmail());


        log.info("Login successful for: {}", loginRequestDTO.getEmail());

        // Step 9 - Build and return response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenString);
        response.setTokenType("Bearer");
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());
        response.setIsFirstTimeLogin(user.getIsFirstTimeLogin());

        return response;
    }
    public String changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        log.info("Change password attempted for " + changePasswordRequestDTO.getEmail());

        User user = userRepository.findByEmail(changePasswordRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + changePasswordRequestDTO.getEmail()));

        LoginSecurity loginSecurity = loginSecurityRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Login security record not found"));

        if (loginSecurity.getAccountStatus() == AccountStatus.PERMANENTLY_BLOCKED) {
            throw new RuntimeException("Account is permanently blocked");
        }

        boolean isPasswordValid = passwordEncoder.matches(
                changePasswordRequestDTO.getOldPassword(), user.getTempPassword()
        );

        if (!isPasswordValid) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (!changePasswordRequestDTO.getNewPassword().equals(changePasswordRequestDTO.getConfirmNewPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        if (passwordEncoder.matches(changePasswordRequestDTO.getNewPassword(), user.getTempPassword())) {
            throw new RuntimeException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
        user.setTempPassword(null);
        user.setTempPasswordExpiry(null);
        user.setTempPasswordAttemptsCount(0);
        user.setIsFirstTimeLogin(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Password changed successfully for " + changePasswordRequestDTO.getEmail());
        return "Password changed successfully";
    }
    @Override
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String token = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getIsRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired. Please login again");
        }

        String newAccessToken = jwtUtil.generateAccessToken(
                refreshToken.getUser().getEmail(),
                refreshToken.getUser().getRole()
        );

        return new RefreshTokenResponseDTO(newAccessToken);
    }

    @Override
    public String logout(RefreshTokenRequestDTO request) {
        String token = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenRepository.revokeAllTokensByUser(refreshToken.getUser());

        return "Logged out successfully";
    }

}