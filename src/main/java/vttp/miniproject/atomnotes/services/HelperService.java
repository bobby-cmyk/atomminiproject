package vttp.miniproject.atomnotes.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

@Service
public class HelperService {
    
    public String getCurrentDateTime() {
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        return localDateTime.toString();
    }
}
