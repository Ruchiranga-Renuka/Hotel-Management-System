package com.hotel.service;

import com.hotel.dto.RoomDto;
import com.hotel.dto.RoomSearchRequest;
import com.hotel.model.Room;
import com.hotel.model.enums.RoomStatus;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    public RoomDto getRoomById(Long id) {
        return roomRepository.findById(id).map(this::toDto).orElse(null);
    }

    @SuppressWarnings("null")
    public RoomDto createRoom(Room room) {
        return toDto(roomRepository.save(room));
    }

    @SuppressWarnings("null")
    public RoomDto updateRoom(Long id, Room updatedRoom) {
        return roomRepository.findById((long) id)
                .map(room -> {
                    room.setRoomNumber(updatedRoom.getRoomNumber());
                    room.setType(updatedRoom.getType());
                    room.setPricePerNight(updatedRoom.getPricePerNight());
                    room.setDescription(updatedRoom.getDescription());
                    room.setImageUrl(updatedRoom.getImageUrl());
                    room.setStatus(updatedRoom.getStatus());
                    return toDto(roomRepository.save(room));
                })
                .orElse(null);
    }

    @SuppressWarnings("null")
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public List<RoomDto> searchAvailableRooms(RoomSearchRequest request) {
        List<Room> rooms = roomRepository.findByStatus(RoomStatus.AVAILABLE);
        return rooms.stream()
                .filter(room -> isRoomAvailable(room.getId(), request.getCheckInDate(), request.getCheckOutDate()))
                .filter(room -> request.getType() == null || room.getType() == request.getType())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Long roomId, java.time.LocalDate checkIn, java.time.LocalDate checkOut) {
        return bookingRepository.findOverlappingBookings(roomId, com.hotel.model.enums.BookingStatus.CONFIRMED, checkIn, checkOut).isEmpty();
    }

    private RoomDto toDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPricePerNight(),
                room.getDescription(),
                room.getImageUrl(),
                room.getStatus()
        );
    }
}
