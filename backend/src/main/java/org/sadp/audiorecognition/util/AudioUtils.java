package org.sadp.audiorecognition.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
@Component
public class AudioUtils {

    public File convertMp3ToWav(MultipartFile mp3File) throws Exception{
//        System.out.println("Converting MP3 to WAV started");
        File tempMp3=File.createTempFile("Upload_", ".mp3");
        mp3File.transferTo(tempMp3);

        File outputDir=new File(System.getProperty("java.io.tmpdir"));
        if(!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File covertedToWav=convertToWav(tempMp3, outputDir);
        tempMp3.delete();

        return covertedToWav;
    }

    private static File convertToWav(File mp3File, File outputDir) throws Exception{
        String outputFilePath = new File(outputDir, mp3File.getName().replaceAll("\\.mp3$", "") + ".wav").getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", mp3File.getAbsolutePath(),
                "-ac", "1",
                "-ar", "44100",
                "-f", "wav",
                outputFilePath
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while(reader.readLine()!= null){
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg conversion failed.");
        }

        return new File(outputFilePath);
    }

    public float[] extractPcmSamples(File wavFile) throws Exception{
        AudioInputStream ais= AudioSystem.getAudioInputStream(wavFile);
        AudioFormat format=ais.getFormat();

        if(format.getEncoding()!=AudioFormat.Encoding.PCM_SIGNED){
            throw new UnsupportedAudioFileException("WAV file is not PCM_SIGNED format");
        }

        byte[] bytes= FileUtils.readFileToByteArray(wavFile);
        int frameSize=format.getFrameSize();
        int totalSamples=bytes.length/frameSize;

        float[] samples=new float[totalSamples];
        ByteBuffer bb=ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        for(int i=0;i<totalSamples;i++){
            short sample=bb.getShort();
            samples[i]=sample/32768.0f;
        }

        return samples;
    }

    public File convertWebmToWav(MultipartFile webmFile) throws Exception {
        File tempWebm=File.createTempFile("Upload_", ".webm");
        webmFile.transferTo(tempWebm);

        File outputDir=new File(System.getProperty("java.io.tmpdir"));
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File convertedToWav=convertWebToWav(tempWebm, outputDir);
        tempWebm.delete();

        return convertedToWav;
    }

    private static File convertWebToWav(File webmFile, File outputDir) throws Exception {
        String outputFilePath=new File(outputDir, webmFile.getName().replaceAll("\\.webm$", "") + ".wav").getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", webmFile.getAbsolutePath(),
                "-ac", "1",
                "-ar", "44100",
                "-f", "wav",
                outputFilePath
        );

        pb.redirectErrorStream(true);
        Process process=pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) {
            }
        }

        int exitCode=process.waitFor();
        if (exitCode!=0) {
            throw new RuntimeException("FFmpeg conversion failed.");
        }

        return new File(outputFilePath);
    }

}
