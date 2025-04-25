package org.sadp.audiorecognition.service;

import lombok.RequiredArgsConstructor;
import org.sadp.audiorecognition.util.AudioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AudioService {
    private final AudioUtils audioUtils;
    private final SpectrogramService spectrogramService;
    private final FingerprintService fingerprintService;

    public String processUploadedAudio(MultipartFile file){
        return "Stub: uploaded"+ file.getOriginalFilename();
    }

    public String matchAudio(MultipartFile file){
        return "Stub: matched"+ file.getOriginalFilename();
    }
}
