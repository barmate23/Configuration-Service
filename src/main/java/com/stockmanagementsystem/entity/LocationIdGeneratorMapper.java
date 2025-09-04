package com.stockmanagementsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationIdGeneratorMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_gen_sequence")
    @SequenceGenerator(name = "location_gen_sequence", sequenceName = "location_gen_sequence", allocationSize = 1)
    private Integer id;
    private String locationId;
    private Integer locationSequence;
    private String areaId;
    private String storeId;

}
