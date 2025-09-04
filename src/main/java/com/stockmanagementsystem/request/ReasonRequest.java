package com.stockmanagementsystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor@NoArgsConstructor
public class ReasonRequest {

    private String reasonCode;

    private String reasonName;

    private String department;

}
