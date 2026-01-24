package au.com.xtramile.mpiapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "hospital")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @Column(name = "id", length = 50, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

}
