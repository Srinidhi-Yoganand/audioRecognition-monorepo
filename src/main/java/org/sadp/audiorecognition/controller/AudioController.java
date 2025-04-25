package org.sadp.audiorecognition.controller;

import lombok.RequiredArgsConstructor;
import org.sadp.audiorecognition.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file){
        String response=audioService.processUploadedAudio(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/match")
    public ResponseEntity<String> match(@RequestParam("file")MultipartFile file){
        String result =audioService.matchAudio(file);
        return ResponseEntity.ok(result);
    }
}
