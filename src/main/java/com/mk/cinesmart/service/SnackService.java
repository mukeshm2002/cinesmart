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

    // 💡 NEW: ADDED THIS TO PREVENT "Cannot resolve method" ERROR
    public List<Snack> getAllSnacks() {
        return snackRepository.findAll();
    }

    // 💡 NEW: ADDED FOR ADMIN DASHBOARD (Snacks Sold Count)
    // குறிப்பு: உங்க டேட்டாபேஸ்ல ஆர்டர் டேபிள் இருந்தா அதை இங்க கணக்கு பண்ணிக்கோங்க.
    // இப்போதைக்கு ஸ்டாக் சேஞ்ச்-ஐ வச்சு தோராயமா (0) ரிட்டர்ன் பண்றேன்.
    public Long getTotalSnacksSoldToday() {
        return 0L;
    }

    // 2. GET ALL AVAILABLE SNACKS (Sorted)
    public List<Snack> getAllSnacksSortedByPrice() {
        return snackRepository.findAllByOrderByPriceAsc();
    }

    // SnackService.java-வில் இதை மட்டும் மாத்துங்க
    public List<Snack> getLowStockAlerts() {
        int lowStockThreshold = 10;
        // இங்க உங்க கஸ்டம் குவரியை கூப்பிடுறோம்
        return snackRepository.findLowStockSnacks(lowStockThreshold);
    }

    // 4. DELETE SNACK
    public void deleteSnack(Long id) {
        if (!snackRepository.existsById(id)) {
            throw new RuntimeException("Snack item not found with ID: " + id);
        }
        snackRepository.deleteById(id);
    }

    // 5. GET ACTIVE SNACKS FOR USER
    public List<Snack> getAllActiveSnacks() {
        return snackRepository.findByAvailableStockGreaterThan(0);
    }
}