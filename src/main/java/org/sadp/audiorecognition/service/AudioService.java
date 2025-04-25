package org.sadp.audiorecognition.service;

import lombok.RequiredArgsConstructor;
import org.sadp.audiorecognition.util.AudioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AudioService {
    private static final Logger log = LoggerFactory.getLogger(AudioService.class);
    private final AudioUtils audioUtils;
    private final SpectrogramService spectrogramService;
    private final FingerprintService fingerprintService;

    public String processUploadedAudio(MultipartFile file){
        try{
            File wav=audioUtils.convertMp3ToWav(file);
//            System.out.println("Audio Service: Converted MP3 to WAV");
            float[] pcm=audioUtils.extractPcmSamples(wav);

//            log.info("Extracted {} PCM Sample", pcm.length);
//            for(int i=0;i<10;i++){
//                log.info("Sample {}: {}", i, pcm[i]);
//            }

            wav.delete();
            return "Extracted "+pcm.length+" PCM Samples";
        }catch(Exception e){
//            log.error("Error processing audio file", e);
            return "Error processing audio file: " + e.getMessage();
        }
    }

    public String matchAudio(MultipartFile file){
        return "Stub: matched"+ file.getOriginalFilename();
    }
}
