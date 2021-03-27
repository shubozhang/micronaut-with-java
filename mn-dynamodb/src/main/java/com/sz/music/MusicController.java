package com.sz.music;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sz.music.model.Music;
import com.sz.utils.ItemError;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@Controller("/music")
public class MusicController {
    private static final Logger log = LoggerFactory.getLogger(MusicController.class);

    @Inject
    MusicDao musicDao;

    @Inject
    ObjectMapper mapper;


    @Get("{?music*}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getEvent(Music music) {
        Music item = musicDao.getItem(music);
        if (item == null) {
            final ItemError notFound = ItemError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("item not found")
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(item);
    }

    @Get("/query/lsi{?music*}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getEventByLSI(Music music) throws JsonProcessingException {
        List<Music> items = musicDao.queryItemByLSI(music);
        if (items.isEmpty()) {
            final ItemError notFound = ItemError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("item not found")
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(items);
    }

    @Get("/query/gsi{?music*}")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse getEventByGSI(Music music) throws JsonProcessingException {
        List<Music> items = musicDao.queryItemByGSI(music);
        if (items.isEmpty()) {
            final ItemError notFound = ItemError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("item not found")
                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(items);
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse saveEvent(@Body String body) throws JsonProcessingException {
        musicDao.saveItem(mapper.readValue(body, Music.class));
        return HttpResponse.ok();
    }

    @Delete
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse deleteEvent(@Body String body) throws JsonProcessingException {
        musicDao.deleteItem(mapper.readValue(body, Music.class));
        return HttpResponse.ok();
    }
}
