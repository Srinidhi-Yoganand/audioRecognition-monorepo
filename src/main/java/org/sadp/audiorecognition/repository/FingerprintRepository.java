package org.sadp.audiorecognition.repository;

import org.sadp.audiorecognition.entity.FingerprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FingerprintRepository extends JpaRepository<FingerprintEntity, Long> {
    List<FingerprintEntity> findByHash(String hash);
}
