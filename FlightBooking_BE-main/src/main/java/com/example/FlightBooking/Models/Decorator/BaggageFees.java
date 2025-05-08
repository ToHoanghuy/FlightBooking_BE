package com.example.FlightBooking.Models.Decorator;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name ="baggage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaggageFees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double weightThreshold; // Ngưỡng trọng lượng
    private double feePerKg; // Phí trên mỗi kg vượt quá ngưỡng
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
