package com.marur.backend.service;

import com.marur.backend.dto.ProductResponse;
import com.marur.backend.entity.Favorite;
import com.marur.backend.entity.Product;
import com.marur.backend.entity.User;
import com.marur.backend.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductService productService;

    public List<ProductResponse> getFavorites(User user) {
        return favoriteRepository.findByUserId(user.getId())
                .stream()
                .map(f -> productService.getById(f.getProduct().getId()))
                .toList();
    }

    public Map<String, Boolean> toggle(User user, Long productId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndProductId(user.getId(), productId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return Map.of("favorited", false);
        }

        Product product = productService.findById(productId);
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        return Map.of("favorited", true);
    }
}