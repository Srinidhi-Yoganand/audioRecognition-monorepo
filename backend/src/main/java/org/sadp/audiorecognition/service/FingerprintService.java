package org.sadp.audiorecognition.service;

import org.sadp.audiorecognition.entity.FingerprintEntity;
import org.sadp.audiorecognition.entity.Song;
import org.sadp.audiorecognition.model.DataPoint;
import org.sadp.audiorecognition.model.Fingerprint;
import org.sadp.audiorecognition.repository.FingerprintRepository;
import org.sadp.audiorecognition.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FingerprintService {

    @Autowired
    private FingerprintRepository fingerprintRepository;
    @Autowired
    private SongRepository songRepository;

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

    public Optional<Song> matchFingerprints(List<Fingerprint> sampleFingerprints) {
        Map<String, List<Integer>> hashToSampleTimeMap=new HashMap<>();
        for (Fingerprint fingerprint : sampleFingerprints) {
            String hash=fingerprint.getHash();
            int time=fingerprint.getTime();

            if (!hashToSampleTimeMap.containsKey(hash)) {
                hashToSampleTimeMap.put(hash, new ArrayList<>());
            }

            hashToSampleTimeMap.get(hash).add(time);
        }

        Map<Long, Map<Integer, Integer>> songOffsetMatchCounts=new HashMap<>();
        int bucketSize=2;
        for (Map.Entry<String, List<Integer>> entry : hashToSampleTimeMap.entrySet()) {
            String hash=entry.getKey();
            List<FingerprintEntity> matches=fingerprintRepository.findByHash(hash);

            for (FingerprintEntity match:matches) {
                Long songId=match.getSong().getId();
                for (Integer sampleTime:entry.getValue()) {
                    int offset=match.getTime()-sampleTime;
                    int offsetBucket = Math.round((float) offset / bucketSize);

                    Map<Integer, Integer> offsetCountMap=songOffsetMatchCounts.computeIfAbsent(songId, k -> new HashMap<>());
                    offsetCountMap.put(offsetBucket, offsetCountMap.getOrDefault(offsetBucket, 0) + 1);
                }
            }
        }

        long bestSongId=-1;
        int maxCount=0;

        for (Map.Entry<Long, Map<Integer, Integer>> songEntry:songOffsetMatchCounts.entrySet()) {
            Long songId = songEntry.getKey();
            Map<Integer, Integer> offsetMap = songEntry.getValue();

            int bestOffsetCount = offsetMap.values().stream().max(Integer::compareTo).orElse(0);
//            System.out.println("Song ID: " + songId + " best offset match count: " + bestOffsetCount);

            if (bestOffsetCount > maxCount) {
                maxCount = bestOffsetCount;
                bestSongId = songId;
            }
        }

        return bestSongId!=-1? songRepository.findById(bestSongId):Optional.empty();
    }
}
