package org.sadp.audiorecognition.service;

import org.sadp.audiorecognition.entity.FingerprintEntity;
import org.sadp.audiorecognition.entity.Song;
import org.sadp.audiorecognition.model.DataPoint;
import org.sadp.audiorecognition.model.Fingerprint;
import org.sadp.audiorecognition.repository.FingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FingerprintService {

    @Autowired
    private FingerprintRepository fingerprintRepository;

    private static final int FAN_VALUE=10;

    public List<Fingerprint> generateFingerprints(List<DataPoint> peaks) {
        List<Fingerprint> fingerprints=new ArrayList<>();

        for (int i=0; i<peaks.size(); i++) {
            DataPoint anchor=peaks.get(i);

            for (int j=1; j<=FAN_VALUE && (i+j)<peaks.size(); j++) {
                DataPoint target=peaks.get(i + j);

                int freq1=anchor.getFrequencyBin();
                int freq2=target.getFrequencyBin();
                int t1=anchor.getTimeFrame();
                int t2=target.getTimeFrame();
                int deltaT=t2-t1;

                if (deltaT>=0 && deltaT<=200) {
                    String hashString=freq1 + "|" + freq2 + "|" + deltaT;
                    String hash=sha1Hash(hashString).substring(0, 10); // reduce to 10 chars for compactness
                    fingerprints.add(new Fingerprint(hash, t1));
                }
            }
        }

        return fingerprints;
    }

    private String sha1Hash(String input) {
        try {
            MessageDigest md=MessageDigest.getInstance("SHA-1");
            byte[] digest=md.digest(input.getBytes());
            StringBuilder sb=new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not supported", e);
        }
    }

    public void saveFingerprints(List<Fingerprint> fingerprints, Song song) {
        List<FingerprintEntity> entities=fingerprints.stream().map(fp -> {
            FingerprintEntity entity=new FingerprintEntity();
            entity.setHash(fp.getHash());
            entity.setTime(fp.getTime());
            entity.setSong(song);
            return entity;
        }).toList();

        fingerprintRepository.saveAll(entities);
    }
}
