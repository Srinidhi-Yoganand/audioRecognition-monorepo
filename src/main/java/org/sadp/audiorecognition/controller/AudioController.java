package org.sadp.audiorecognition.controller;

import lombok.RequiredArgsConstructor;
import org.sadp.audiorecognition.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file,
                                         @RequestParam("songName")String songName,
                                         @RequestParam("artistName")String artistName){
        String response=audioService.processUploadedAudio(file, songName, artistName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/match")
    public ResponseEntity<String> match(@RequestParam("file")MultipartFile file){
        String result=audioService.matchAudio(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/match/upload")
    public ResponseEntity<String> matchUpload(@RequestParam("file")MultipartFile file){
        String result=audioService.matchAudio(file);
        return ResponseEntity.ok(result);
    }
}
