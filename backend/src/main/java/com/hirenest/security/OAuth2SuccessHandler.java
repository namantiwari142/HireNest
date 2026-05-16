package com.hirenest.security;

import com.hirenest.entity.*;
import com.hirenest.repository.ApplicantRepository;
import com.hirenest.repository.RecruiterRepository;
import com.hirenest.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final RecruiterRepository recruiterRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(
            JwtService jwtService,
            UserRepository userRepository,
            ApplicantRepository applicantRepository,
            RecruiterRepository recruiterRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.applicantRepository = applicantRepository;
        this.recruiterRepository = recruiterRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String registrationId = getRegistrationId(request);

        AuthProvider provider = "github".equals(registrationId) ? AuthProvider.GITHUB : AuthProvider.GOOGLE;
        String oauthId = oauthUser.getName();
        String email = extractEmail(oauthUser, provider);
        String name = oauthUser.getAttribute("name") != null
                ? oauthUser.getAttribute("name").toString()
                : email.split("@")[0];
        String picture = extractPicture(oauthUser, provider);

        String roleParam = request.getParameter("role");
        Role role = "RECRUITER".equalsIgnoreCase(roleParam) ? Role.RECRUITER : Role.APPLICANT;

        User user = userRepository.findByOauthIdAndProvider(oauthId, provider)
                .or(() -> userRepository.findByEmail(email))
                .orElseGet(() -> createOAuthUser(email, name, oauthId, provider, role, picture));

        if (picture != null && user.getProfileImageUrl() == null) {
            user.setProfileImageUrl(picture);
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        String base = frontendUrl == null ? "" : frontendUrl.trim().replaceAll("/+$", "");
        String redirectUrl = UriComponentsBuilder.fromUriString(base + "/oauth/callback")
                .queryParam("token", token)
                .queryParam("role", user.getRole().name())
                .queryParam("userId", user.getId())
                .queryParam("name", user.getName())
                .queryParam("email", user.getEmail())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private User createOAuthUser(String email, String name, String oauthId, AuthProvider provider, Role role, String picture) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setRole(role);
        user.setProvider(provider);
        user.setOauthId(oauthId);
        user.setProfileImageUrl(picture);
        user = userRepository.save(user);

        if (role == Role.RECRUITER) {
            Recruiter recruiter = new Recruiter();
            recruiter.setUser(user);
            recruiter.setCompanyName(name + " Company");
            recruiterRepository.save(recruiter);
        } else {
            Applicant applicant = new Applicant();
            applicant.setUser(user);
            applicantRepository.save(applicant);
        }
        return user;
    }

    private String extractEmail(OAuth2User user, AuthProvider provider) {
        if (provider == AuthProvider.GITHUB) {
            Object email = user.getAttribute("email");
            if (email != null) return email.toString();
            return user.getAttribute("login") + "@github.local";
        }
        return user.getAttribute("email").toString();
    }

    private String extractPicture(OAuth2User user, AuthProvider provider) {
        if (provider == AuthProvider.GITHUB) {
            return Optional.ofNullable(user.getAttribute("avatar_url")).map(Object::toString).orElse(null);
        }
        return Optional.ofNullable(user.getAttribute("picture")).map(Object::toString).orElse(null);
    }

    private String getRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("github")) return "github";
        return "google";
    }
}
