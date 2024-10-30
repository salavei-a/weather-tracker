package com.asalavei.weathertracker.weather.location;

import com.asalavei.weathertracker.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "locations", indexes = {
        @Index(name = "idx_locations_user_id_id", columnList = "user_id, id"),
        @Index(name = "uidx_locations_user_id_latitude_longitude", columnList = "user_id, latitude, longitude", unique = true)
})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "latitude", precision = 8, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;
}
