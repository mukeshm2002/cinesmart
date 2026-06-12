package com.mk.cinesmart.service;

import com.mk.cinesmart.model.Snack;
import com.mk.cinesmart.repository.SnackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class SnackService {

    @Autowired
    private SnackRepository snackRepository;

    @Autowired
    private ImageService imageService;

    // 1. ADD OR UPDATE SNACK
    public Snack saveSnack(Snack snack, MultipartFile snackImageFile) throws IOException {
        if (snackImageFile != null && !snackImageFile.isEmpty()) {
            String uploadedImageUrl = imageService.uploadImage(snackImageFile);
            snack.setImageUrl(uploadedImageUrl);
        } else if (snack.getImageUrl() == null) {
            snack.setImageUrl("https://images.cloudinary.com/default-snack.jpg");
        }
        return snackRepository.save(snack);
    }

    // 2. NEW: GET SNACKS BY THEATRE (Theater Admin Dashboard)
    public List<Snack> getSnacksByTheatre(Long theatreId) {
        return snackRepository.findByTheatreIdOrderByPriceAsc(theatreId);
    }

    // 3. NEW: LOW STOCK ALERTS BY THEATRE
    public List<Snack> getLowStockAlertsByTheatre(Long theatreId) {
        int lowStockThreshold = 10;
        return snackRepository.findLowStockSnacksByTheatre(lowStockThreshold, theatreId);
    }

    // 4. GET ACTIVE SNACKS FOR A SPECIFIC THEATRE (User Screen)
    public List<Snack> getActiveSnacksByTheatre(Long theatreId) {
        return snackRepository.findByTheatreIdAndAvailableStockGreaterThan(theatreId, 0);
    }

    // 5. DELETE SNACK
    public void deleteSnack(Long id) {
        if (!snackRepository.existsById(id)) {
            throw new RuntimeException("Snack item not found with ID: " + id);
        }
        snackRepository.deleteById(id);
    }

    public Long getTotalSnacksSoldToday() {
        return 0L; // ஆர்டர் டேபிளை லிங்க் செய்தபின் இதை அப்டேட் செய்யலாம்
    }
}