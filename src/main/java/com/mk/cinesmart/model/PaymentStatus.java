package com.mk.cinesmart.model;

public enum PaymentStatus {
    SUCCESS,             // பேமெண்ட் கன்பர்ம் ஆகிடுச்சு
    FAILED,              // பேமெண்ட் ஃபெயில் ஆகிடுச்சு
    REFUNDED_PARTIAL,    // 50% இன்ஸ்டன்ட் கேன்சலேஷன் ரீஃபண்ட் செய்யப்பட்ட நிலை
    REFUNDED_FULL        // P2P ரீசேல் மூலமா டிக்கெட் வித்து 100% ரீஃபண்ட் செய்யப்பட்ட நிலை
}
