package org.sadp.audiorecognition.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Fingerprint {

    private String hash;
    private int time;
}
