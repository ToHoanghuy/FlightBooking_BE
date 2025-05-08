package com.example.FlightBooking.Models;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hpsf.Decimal;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;


@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "flights")
// Cai model nay la luu thong tin CHUYEN BAY
public class Flights {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightStatus; // Cai nay la dang delay, hay hoan, hay la san sang cat canh

    private Timestamp departureDate; //thoi gian cat canh
    private Timestamp arrivalDate; // thoi gian ha canh

    private Long duration; // uoc luong thoi gian bay
    private Long departureAirportId;
    private Long arrivalAirportId;
    private Long planeId; // Luu thong tin may bay cua hang nao

    private Double economyPrice;
    private Double businessPrice;
    private Double firstClassPrice;

    //
    @Lob
    @Column(name = "seat_statuses", nullable = false)
    private String seatStatuses; // Sử dụng JSON để lưu trạng thái chỗ ngồi

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
