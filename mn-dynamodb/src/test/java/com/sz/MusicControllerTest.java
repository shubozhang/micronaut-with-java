package com.sz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sz.model.Music;
import com.sz.utils.ItemError;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriTemplate;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class MusicControllerTest {

    private static final Logger log = LoggerFactory.getLogger(MusicController.class);
    @Inject
    static EmbeddedApplication application;

    @Inject
    @Client("/music")
    RxHttpClient client;

    @Inject
    ObjectMapper mapper;

    @Test
    void testGetEventByPartitionKey() throws JsonProcessingException {
        Music music = new Music("Beyond", "Love", "Love", 1993, "09000");
        saveItem(music);

        Music requestMusic = new Music("Beyond", "Love", null, null, null);
        Music result = getItem(requestMusic);

        assertTrue(result.getArtist().equals("Beyond"));
        assertTrue(result.getAlbumTitle().equals("Love"));
        assertTrue(result.getYear() == 1993);
    }

    @Test
    void testGetEventByLSI() throws JsonProcessingException {
        Music music = new Music("Maroon 5", "One More Night", "Overexposed", 2012, "20120123");
        saveItem(music);
        Music music1 = new Music("Maroon 5", "Payphone", "Overexposed", 2012, "20120124");
        saveItem(music1);


        Music requestMusic = new Music("Maroon 5", null, "Overexposed", null, null);
        String musics = queryItemsByLSI(requestMusic);
        List<Music> musicList;

        musicList = mapper.readValue(musics, new TypeReference<>() {});
        Collections.sort(musicList, Comparator.comparing(Music::getUpc));
        assertTrue(musicList.size() == 2);
        assertTrue(musicList.get(0).getUpc().equals("20120123"));
        assertTrue(musicList.get(1).getUpc().equals("20120124"));
    }


    @Test
    void testGetEventByGSI() throws JsonProcessingException {
        Music music = new Music("MJ", "Dangerous", "Dangerous", 1991, "MJ-19910123");
        saveItem(music);

        Music requestMusic = new Music(null, null, null, null, "MJ-19910123");
        String musics = queryItemsByGSI(requestMusic);
        List<Music> musicList;

        musicList = mapper.readValue(musics, new TypeReference<>() {});
        assertTrue(musicList.size() == 1);
        assertTrue(musicList.get(0).getUpc().equals("MJ-19910123"));
        assertTrue(musicList.get(0).getArtist().equals("MJ"));
        assertTrue(musicList.get(0).getSongTitle().equals("Dangerous"));
        assertTrue(musicList.get(0).getAlbumTitle().equals("Dangerous"));
        assertTrue(musicList.get(0).getYear().equals(1991));
    }




    @Test
    void testDeleteEvent() throws JsonProcessingException {
        Music music = new Music("Robin", "Angel", "Angel", 2000, "08000");
        saveItem(music);

        Music delRequest = new Music("Robin", "Angel", null, null, null);
        deleteItem(delRequest);

        Music request = new Music("Robin", "Angel", null, null, null);
        try {
            getItem(request);
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
            Optional<ItemError> jsonError = e.getResponse().getBody(ItemError.class);
            assertTrue(jsonError.isPresent());
            log.debug("Custom Error: {}", jsonError.get());
            assertEquals(404, jsonError.get().getStatus());
            assertEquals("NOT_FOUND", jsonError.get().getError());
            assertEquals("item not found", jsonError.get().getMessage());
        }

    }


    private void saveItem(final Music music) throws JsonProcessingException {
        final String body = mapper.writeValueAsString(music);
        client.toBlocking().exchange(HttpRequest.POST("/", body));
    }

    private Music getItem(final Music music) {
        final String uri = new UriTemplate("/{?artist,songTitle}").expand(music);
        return client.toBlocking().retrieve(HttpRequest.GET(uri), Music.class);
    }

    private String queryItemsByLSI(Music music) {
        final String uri = new UriTemplate("/query/lsi{?artist,albumTitle}").expand(music);
        return client.toBlocking().retrieve(HttpRequest.GET(uri));
    }

    private String queryItemsByGSI(Music music) {
        final String uri = new UriTemplate("/query/gsi{?upc}").expand(music);
        return client.toBlocking().retrieve(HttpRequest.GET(uri));
    }
    private void deleteItem(final Music music) throws JsonProcessingException {
        final String delBody = mapper.writeValueAsString(music);
        client.toBlocking().exchange(HttpRequest.DELETE("/",delBody));
    }
}
