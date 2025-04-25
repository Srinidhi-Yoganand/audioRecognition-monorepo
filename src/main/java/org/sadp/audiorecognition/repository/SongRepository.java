package org.sadp.audiorecognition.repository;

import org.sadp.audiorecognition.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByTitle(String title);
    List<Song> findByArtist(String artist);
}
