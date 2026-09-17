package com.example.recyclingapi.controller;

import com.example.recyclingapi.model.ItemResult;
import com.example.recyclingapi.service.GeminiVisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:3001"
})
public class ItemController {

    private final GeminiVisionService geminiVisionService;

    public ItemController(GeminiVisionService geminiVisionService) {
        this.geminiVisionService = geminiVisionService;
    }

    @GetMapping("/test")
    public String test() {
        return "Java backend is working!";
    }

    @PostMapping("/identify")
    public ResponseEntity<ItemResult> identify(
            @RequestParam("image") MultipartFile image) {

        if (image.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select an image."
            );
        }

        try {
            ItemResult result = geminiVisionService.identify(image);
            return ResponseEntity.ok(result);
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "The image recognition service failed.",
                    exception
            );
        }
    }
}