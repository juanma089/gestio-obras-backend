package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.dtos.user.UserInfoDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserServiceClientManager {

    private final WebClient usersWebClient;

    public UserServiceClientManager(WebClient.Builder webClientBuilder) {
        this.usersWebClient = webClientBuilder.baseUrl("http://localhost:8081/users").build();
    }

    public UserInfoDto getAuthenticatedUser(String jwtToken) {
        return usersWebClient.get()
                .uri("/me")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .retrieve()
                .bodyToMono(UserInfoDto.class)
                .block();
    }
}