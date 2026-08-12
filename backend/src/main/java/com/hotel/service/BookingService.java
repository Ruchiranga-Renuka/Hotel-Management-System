package com.hotel.service;

import com.hotel.dto.BookingDto;
import com.hotel.dto.BookingRequest;
import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.model.enums.BookingStatus;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<BookingDto> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public BookingDto createBooking(Long userId, BookingRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Room> roomOptional = roomRepository.findById(request.getRoomId());

        if (userOptional.isEmpty() || roomOptional.isEmpty()) {
            return null;
        }

        Room room = roomOptional.get();
        if (room.getStatus() != com.hotel.model.enums.RoomStatus.AVAILABLE) {
            return null;
        }

        boolean hasOverlap = bookingRepository.findOverlappingBookings(room.getId(), BookingStatus.CONFIRMED, request.getCheckInDate(), request.getCheckOutDate())
                .stream()
                .findAny()
                .isPresent();

        if (hasOverlap) {
            return null;
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking(userOptional.get(), room, request.getCheckInDate(), request.getCheckOutDate(), BookingStatus.CONFIRMED, totalPrice);
        return toDto(bookingRepository.save(booking));
    }

    public boolean cancelBooking(Long bookingId, Long userId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            return false;
        }

        Booking booking = bookingOptional.get();
        if (!booking.getUser().getId().equals(userId)) {
            return false;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return true;
    }

    private BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getUser().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCreatedAt()
        );
    }
}
