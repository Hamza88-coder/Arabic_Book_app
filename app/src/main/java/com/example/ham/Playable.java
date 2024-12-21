package com.example.ham;

import java.io.IOException;

public interface Playable {
    void onTrackPrevious();
    void onTrackPlay() throws IOException;
    void onTrackPause();
    void onTrackNext();

}
