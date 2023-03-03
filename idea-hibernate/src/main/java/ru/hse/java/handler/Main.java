package ru.hse.java.handler;

import ru.hse.java.entities.Track;
import ru.hse.java.entities.User;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        /*Set<Integer> set = Handler.getUserFavouritesWithUserId(13);
        Set<Track> tracks = Handler.getTracksWithTrackIds(set);
        for (Track track : tracks) {
            System.out.println(track.getName());
        }
        Handler.closeHandler();*/
        // Handler.addNewTrackWithNameAuthorGenreMood("Рассвет", "Джизус", "Rock", "Sad");
        /*Track track = Handler.getTrackWithTrackId(5);
        track.setCoverUrl("dummy");
        Handler.updateTrackAllParamsWithUpdatedTrack(5, track);*/
        // Handler.deleteTrackWithTrackId(5);
        /*int result = Handler.incrementStreamsWithTrackId(4);
        if (result == 1) {
            System.out.println("going well");
        }*/
    }
}
