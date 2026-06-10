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
    private ImageService imageService; // Cloudinary இமேஜ் அப்லோடுக்காக

    // 1. ADD OR UPDATE SNACK WITH CLOUDINARY IMAGE (Theater Admin Module)
    public Snack saveSnack(Snack snack, MultipartFile snackImageFile) throws IOException {
        if (snackImageFile != null && !snackImageFile.isEmpty()) {
            String uploadedImageUrl = imageService.uploadImage(snackImageFile);
            snack.setImageUrl(uploadedImageUrl);
        } else if (snack.getImageUrl() == null) {
            // இமேஜ் அப்லோட் பண்ணலனா ஒரு டிஃபால்ட் ஸ்நாக்ஸ் இமேஜ் URL
            snack.setImageUrl("https://images.cloudinary.com/default-snack.jpg");
        }
        return snackRepository.save(snack);
    }

    // 2. GET ALL AVAILABLE SNACKS (For User Order Screen - Sorted by Price)
    public List<Snack> getAllSnacksSortedByPrice() {
        return snackRepository.findAllByOrderByPriceAsc();
    }

    // 3. MONITOR LOW STOCK SNACKS (Admin Dashboard Alert System)
    // ஸ்டாக் 10 அல்லது அதுக்கும் கீழ போனா அட்மினுக்கு அலர்ட் காட்ட இந்த லிஸ்ட் பயன்படும்
    public List<Snack> getLowStockAlerts() {
        int lowStockThreshold = 10;
        return snackRepository.findLowStockSnacks(lowStockThreshold);
    }

    // 4. DELETE SNACK ITEM FROM CANTEEN MENU
    public void deleteSnack(Long id) {
        if (!snackRepository.existsById(id)) {
            throw new RuntimeException("Snack item not found with ID: " + id);
        }
        snackRepository.deleteById(id);
    }

    // =========================================================================
// 🍿 GET ACTIVE SNACKS FOR USER SCREEN
// =========================================================================
    public List<Snack> getAllActiveSnacks() {
        // ஸ்டாக் 0-க்கு மேல இருக்குற எல்லா ஸ்நாக்ஸையும் பில்டர் பண்ணி எடுக்குறோம்
        return snackRepository.findByAvailableStockGreaterThan(0);
    }
}
