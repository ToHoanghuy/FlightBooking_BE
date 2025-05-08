package com.example.FlightBooking.Models.Decorator;

import jakarta.persistence.*;
import lombok.*;
import org.apache.poi.hpsf.Decimal;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name ="voucher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vouchers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;
    private Long discountAmount; // Số tiền giảm giá
    private String voucherImageUrl;
    private String voucherName;
    private Long minOrder;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
