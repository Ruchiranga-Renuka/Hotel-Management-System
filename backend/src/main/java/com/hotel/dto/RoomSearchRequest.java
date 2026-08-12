package com.hotel.dto;

import com.hotel.model.enums.RoomType;

import java.time.LocalDate;

public class RoomSearchRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private RoomType type;

    public RoomSearchRequest() {
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }
}
