package org.sadp.audiorecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataPoint {
    private int frequencyBin;     // Frequency bin index
    private int timeFrame;        // Time frame index
    private double magnitude;     // Magnitude of the FFT at this bin and time
}
