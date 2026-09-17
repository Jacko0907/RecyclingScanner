package com.example.recyclingapi.model;

public record ItemResult(
        String name,
        String category,
        String instructions,
        double confidence,
        String uploadedFileName
) {}
