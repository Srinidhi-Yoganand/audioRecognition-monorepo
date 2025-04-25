package org.sadp.audiorecognition.service;

import org.sadp.audiorecognition.model.DataPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PeakExtractorService {
    private static final int WINDOW_SIZE=15;

    public List<DataPoint> extractPeaks(List<DataPoint> spectrogram, int maxFrequencyBin, int maxTimeFrame){
        double[][] magnitudeMatrix=new double[maxFrequencyBin + 1][maxTimeFrame + 1];

        for (DataPoint point : spectrogram) {
            int freq = point.getFrequencyBin();
            int time = point.getTimeFrame();
            if (freq <= maxFrequencyBin && time <= maxTimeFrame) {
                magnitudeMatrix[freq][time] = point.getMagnitude();
            }
        }

        List<DataPoint> peaks=new ArrayList<>();

        for (int f=0; f<=maxFrequencyBin; f++) {
            for (int t=0; t<=maxTimeFrame; t++) {
                double magnitude=magnitudeMatrix[f][t];
                if (isLocalMaximum(magnitudeMatrix, f, t, magnitude)) {
                    peaks.add(new DataPoint(f, t, magnitude));
                }
            }
        }

        return peaks;
    }

    private boolean isLocalMaximum(double[][] matrix, int f, int t, double value) {
        int half=WINDOW_SIZE / 2;
        int fStart=Math.max(0, f - half);
        int fEnd=Math.min(matrix.length - 1, f + half);
        int tStart=Math.max(0, t - half);
        int tEnd=Math.min(matrix[0].length - 1, t + half);

        for (int i=fStart; i<=fEnd; i++) {
            for (int j=tStart; j<=tEnd; j++) {
                if (i==f && j==t) continue;
                if (matrix[i][j]>=value) {
                    return false;
                }
            }
        }
        return true;
    }
}
