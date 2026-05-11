package com.marur.backend.controller;

import com.marur.backend.dto.ProductResponse;
import com.marur.backend.entity.User;
import com.marur.backend.service.FavoriteService;
import com.marur.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(favoriteService.getFavorites(user));
    }

    @PostMapping("/{productId}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(favoriteService.toggle(user, productId));
    }
}