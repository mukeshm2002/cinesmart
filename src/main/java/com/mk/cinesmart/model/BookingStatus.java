package com.mk.cinesmart.model;

public enum BookingStatus {
    CONFIRMED,       // நார்மலா புக் ஆகி கன்பர்ம் ஆன நிலை
    CANCELLED,       // யூசர் இன்ஸ்டன்ட்டா கேன்சல் பண்ண நிலை (50% Refunded)
    RESALE_LISTED,   // யூசர் ரீசேல் மார்க்கெட்ல விக்க லிஸ்ட் பண்ண நிலை (Seat turns Yellow)
    RESALE_SOLD      // வேற ஒரு யூசர் இதை வாங்கிட்ட நிலை (Original seller gets 100% Refunded)
}
