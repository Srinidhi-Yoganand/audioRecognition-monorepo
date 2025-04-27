package org.sadp.audiorecognition.service;

import lombok.RequiredArgsConstructor;
import org.sadp.audiorecognition.entity.Song;
import org.sadp.audiorecognition.model.DataPoint;
import org.sadp.audiorecognition.model.Fingerprint;
import org.sadp.audiorecognition.repository.SongRepository;
import org.sadp.audiorecognition.util.AudioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AudioService {
    private static final Logger log = LoggerFactory.getLogger(AudioService.class);
    private final AudioUtils audioUtils;
    private final SpectrogramService spectrogramService;
    private final FingerprintService fingerprintService;
    private final PeakExtractorService peakExtractorService;
    private final SongRepository songRepository;

    public String processUploadedAudio(MultipartFile file, String songName, String artistName){
        try{
            File wav=audioUtils.convertMp3ToWav(file);
//            System.out.println("Audio Service: Converted MP3 to WAV");

            float[] pcm=audioUtils.extractPcmSamples(wav);
//            log.info("Extracted {} PCM Sample", pcm.length);
//            for(int i=0;i<10;i++){
//                log.info("Sample {}: {}", i, pcm[i]);
//            }

            List<DataPoint> spectrogram=spectrogramService.generateSpectrogram(pcm);
//            log.info("Spectrogram generated with {} points", spectrogram.size());
//            for (int i = 0; i < Math.min(10, spectrogram.size()); i++) {
//                DataPoint p = spectrogram.get(i*10000);
//                log.info("Point {}: freq={}, time={}, magnitude={}", i, p.getFrequencyBin(), p.getTimeFrame(), p.getMagnitude());
//            }

            List<DataPoint> peaks=peakExtractorService.extractPeaks(spectrogram, 1024, 300);
//            log.info("Extracted {} peaks", peaks.size());
//            for (int i = 0; i < Math.min(10, peaks.size()); i++) {
//                DataPoint peak = peaks.get(i);
//                log.info("Peak {}: freq={}, time={}, magnitude={}", i, peak.getFrequencyBin(), peak.getTimeFrame(), peak.getMagnitude());
//            }

            List<Fingerprint> fingerprints=fingerprintService.generateFingerprints(peaks);
//            log.info("Extracted {} fingerprints", fingerprints.size());
//            for (int i = 0; i < Math.min(10, fingerprints.size()); i++) {
//                Fingerprint fingerprint = fingerprints.get(i*100);
//                log.info("Fingerprint {}: hash={}, time={}", i, fingerprint.getHash(), fingerprint.getTime());
//            }

            Song song=songRepository.save(new Song(null, songName, artistName));
            fingerprintService.saveFingerprints(fingerprints, song);

            wav.delete();
            return "Extracted " + peaks.size() + " peaks and generated " + fingerprints.size() + " fingerprints.";
        }catch(Exception e){
//            log.error("Error processing audio file", e);
            return "Error processing audio file: " + e.getMessage();
        }
    }

    public String matchAudio(MultipartFile file){
        try{
            File wav;
            if(file.getOriginalFilename().endsWith(".webm")){
                wav=audioUtils.convertWebmToWav(file);
            }else{
                wav=audioUtils.convertMp3ToWav(file);
            }
            float[] pcm=audioUtils.extractPcmSamples(wav);
            List<DataPoint> spectrogram=spectrogramService.generateSpectrogram(pcm);
            List<DataPoint> peaks = peakExtractorService.extractPeaks(spectrogram, 1024, 300);
            List<Fingerprint> fingerprints=fingerprintService.generateFingerprints(peaks);
            System.out.println("Sample generated " + fingerprints.size() + " fingerprints");
            for (int i = 0; i < Math.min(10, fingerprints.size()); i++) {
                System.out.println("Sample Fingerprint " + i + ": " + fingerprints.get(i).getHash() + " at " + fingerprints.get(i).getTime());
            }

            Optional<Song> matched = fingerprintService.matchFingerprints(fingerprints);

            return matched.map(song -> "Matched: " + song.getTitle() + " by " + song.getArtist())
                    .orElse("No match found");
        }catch (Exception e){
//            log.error("Error matching audio file", e);
            return "Error during matching: " + e.getMessage();
        }
    }
}
