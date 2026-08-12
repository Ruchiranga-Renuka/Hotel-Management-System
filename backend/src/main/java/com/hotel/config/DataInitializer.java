package com.hotel.config;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.model.enums.BookingStatus;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import com.hotel.model.enums.Role;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                      RoomRepository roomRepository,
                                      BookingRepository bookingRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User("Admin User", "admin@hotel.com", passwordEncoder.encode("admin123"), Role.ADMIN);
                User guest = new User("Jane Guest", "guest@hotel.com", passwordEncoder.encode("guest123"), Role.GUEST);
                userRepository.save(admin);
                userRepository.save(guest);

                Room room1 = new Room("101", RoomType.SINGLE, new BigDecimal("85.00"), "Cozy single room.", "https://placehold.co/400x300", RoomStatus.AVAILABLE);
                Room room2 = new Room("102", RoomType.DOUBLE, new BigDecimal("120.00"), "Spacious double room.", "https://placehold.co/400x300", RoomStatus.AVAILABLE);
                Room room3 = new Room("201", RoomType.SUITE, new BigDecimal("220.00"), "Luxury suite with premium amenities.", "https://placehold.co/400x300", RoomStatus.MAINTENANCE);
                roomRepository.save(room1);
                roomRepository.save(room2);
                roomRepository.save(room3);

                bookingRepository.save(new Booking(guest, room1, LocalDate.now().plusDays(2), LocalDate.now().plusDays(5), BookingStatus.CONFIRMED, room1.getPricePerNight().multiply(new BigDecimal(3))));
            }
        };
    }
}
