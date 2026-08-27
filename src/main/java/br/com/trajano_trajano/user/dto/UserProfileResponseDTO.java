package br.com.trajano_trajano.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponseDTO(UUID id,
                                     String username,
                                     String bio,
                                     int followersCount,
                                     int followingCount,
                                     int postsCount,
                                     Instant createdAt
){}