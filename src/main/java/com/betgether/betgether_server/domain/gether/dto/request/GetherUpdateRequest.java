package com.betgether.betgether_server.domain.gether.dto.request;

public record GetherUpdateRequest (
    String title,
    String description,
    String imageUrl,
    Boolean isPublic
) {
}
