package org.sadp.audiorecognition.service;

import lombok.extern.slf4j.Slf4j;
import org.sadp.audiorecognition.fft.Complex;
import org.sadp.audiorecognition.fft.FFT;
import org.sadp.audiorecognition.model.DataPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SpectrogramService {

    private static final int WINDOW_SIZE=4096;
    private static final int HOP_SIZE=WINDOW_SIZE/2;

    public List<DataPoint> generateSpectrogram(float[] samples){
        List<DataPoint> spectrogram=new ArrayList<>();
        double[] window=hannWindow();

        for (int i = 0; i < samples.length - WINDOW_SIZE; i += HOP_SIZE) {
            float[] frame = new float[WINDOW_SIZE];
            System.arraycopy(samples, i, frame, 0, WINDOW_SIZE);

            Complex[] complexInput = new Complex[WINDOW_SIZE];
            for (int j = 0; j < WINDOW_SIZE; j++) {
                complexInput[j] = new Complex(frame[j] * window[j], 0); // Apply window
            }

            Complex[] spectrum= FFT.fft(complexInput);

            for (int freqBin = 0; freqBin < spectrum.length / 2; freqBin++) {
                double magnitude = spectrum[freqBin].abs();
                spectrogram.add(new DataPoint(freqBin, i / HOP_SIZE, magnitude));
            }
        }

        return spectrogram;
    }

    private double[] hannWindow() {
        double[] window = new double[SpectrogramService.WINDOW_SIZE];
        for (int i = 0; i < SpectrogramService.WINDOW_SIZE; i++) {
            window[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i / (SpectrogramService.WINDOW_SIZE - 1)));
        }
        return window;
    }
}
