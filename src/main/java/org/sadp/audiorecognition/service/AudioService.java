package org.sadp.audiorecognition.service;

import lombok.RequiredArgsConstructor;
import org.sadp.audiorecognition.model.DataPoint;
import org.sadp.audiorecognition.util.AudioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AudioService {
    private static final Logger log = LoggerFactory.getLogger(AudioService.class);
    private final AudioUtils audioUtils;
    private final SpectrogramService spectrogramService;
    private final FingerprintService fingerprintService;
    private final PeakExtractorService peakExtractorService;

    public String processUploadedAudio(MultipartFile file){
        try{
            File wav=audioUtils.convertMp3ToWav(file);
//            System.out.println("Audio Service: Converted MP3 to WAV");
            float[] pcm=audioUtils.extractPcmSamples(wav);
            List<DataPoint> spectrogram=spectrogramService.generateSpectrogram(pcm);
            List<DataPoint> peaks = peakExtractorService.extractPeaks(spectrogram, 1024, 300);

//            log.info("Extracted {} PCM Sample", pcm.length);
//            for(int i=0;i<10;i++){
//                log.info("Sample {}: {}", i, pcm[i]);
//            }

//            log.info("Spectrogram generated with {} points", spectrogram.size());
//            for (int i = 0; i < Math.min(10, spectrogram.size()); i++) {
//                DataPoint p = spectrogram.get(i*10000);
//                log.info("Point {}: freq={}, time={}, magnitude={}", i, p.getFrequencyBin(), p.getTimeFrame(), p.getMagnitude());
//            }

            log.info("Extracted {} peaks", peaks.size());
            for (int i = 0; i < Math.min(10, peaks.size()); i++) {
                DataPoint peak = peaks.get(i);
                log.info("Peak {}: freq={}, time={}, magnitude={}", i, peak.getFrequencyBin(), peak.getTimeFrame(), peak.getMagnitude());
            }

            wav.delete();
            return "Extracted "+peaks.size()+" peaks";
        }catch(Exception e){
//            log.error("Error processing audio file", e);
            return "Error processing audio file: " + e.getMessage();
        }
    }

    public String matchAudio(MultipartFile file){
        return "Stub: matched"+ file.getOriginalFilename();
    }
}
