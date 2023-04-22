package hse.elysium.serverspring.controller;

import hse.elysium.databaseInteractor.TrackService;
import hse.elysium.entities.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ComponentScan("hse.elysium")
@RequestMapping("/elysium/track")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/getTrackInfo")
    ResponseEntity<Track> getTrackInfo(
            @RequestParam int trackId) {
        Track track = trackService.getTrackWithTrackId(trackId);
        if (track == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(track, HttpStatus.OK);
        }
    }

    @GetMapping("/getAll")
    ResponseEntity<List<Track>> getAll() {
        List<Track> list = trackService.getAllTracks();
        if (list == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
    }


}
