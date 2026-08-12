package com.hotel.controller;

import com.hotel.dto.BookingDto;
import com.hotel.dto.BookingRequest;
import com.hotel.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/my")
    public List<BookingDto> getMyBookings(Authentication authentication) {
        Long userId = getUserIdFromPrincipal(authentication);
        return bookingService.getBookingsForUser(userId);
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(Authentication authentication, @RequestBody BookingRequest request) {
        Long userId = getUserIdFromPrincipal(authentication);
        BookingDto booking = bookingService.createBooking(userId, request);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(Authentication authentication, @PathVariable Long bookingId) {
        Long userId = getUserIdFromPrincipal(authentication);
        return bookingService.cancelBooking(bookingId, userId) ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    private Long getUserIdFromPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.hotel.model.User user) {
            return user.getId();
        }
        return Long.parseLong(authentication.getName());
    }
}
