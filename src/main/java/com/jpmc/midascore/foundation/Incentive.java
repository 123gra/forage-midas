package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
//Sometimes the incoming JSON contains more fields than your Java class.
//Without this annotation, Jackson will throw an exception.

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Incentive {
    public float amount;
}
