package com.bytecrew.uscode.dto;

public record RentalLocation(
        String address,
        String name,
        String phone,
        double latitude,
        double longitude
) {}
