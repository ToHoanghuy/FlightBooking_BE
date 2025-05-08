package com.example.FlightBooking.Models;

import com.example.FlightBooking.Components.Composite.AirlineComponent;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "airlines")
/*Annotation @Table là một annotation JPA được sử dụng
để chỉ định tên bảng database mà một entity class liên kết đến.*/
public class Airlines implements AirlineComponent {
    @Id  //Khóa chính
    @GeneratedValue (strategy = GenerationType.IDENTITY) // Sử dụng sequence hoặc auto-increment trong database để tự động tạo giá trị.
    // thường thì nó sẽ có giá trị 1 ++ lên dân
    private Long id;
    private String airlineName;
    private String logoUrl;

    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Planes> planes = new ArrayList<>();

    private List<String> promoForAirline;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public void addPlane(Planes plane) {
        planes.add(plane);
        plane.setAirline(this);
    }
    public void removePlane(Planes plane) {
        planes.remove(plane);
        plane.setAirline(null);
    }
    @Override
    public void execute() {
        System.out.println("Executing airline operations for: " + airlineName);
        for (AirlineComponent plane : planes) {
            plane.execute();
        }
    }
}
