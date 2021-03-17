package com.sz;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sz.model.Music;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class MusicDao {
    private final DynamoDBMapper mapper = DBMapper.getMapper();
    private final Index lsiAlbum = DBMapper.getLSIAlbumTitle();
    private final Index gsiAlbum = DBMapper.getGSIUpc();

    public Music getItem(Music music) { return mapper.load(music); }

    public void saveItem(Music music) {
        mapper.save(music);
    }

    public void deleteItem(Music music) {
        mapper.delete(music);
    }

    public List<Music> queryItemByLSI(Music music) throws JsonProcessingException {
        List<Music> musicList = new ArrayList<>();
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("Artist = :v_artist and AlbumTitle = :v_title")
                .withValueMap(new ValueMap()
                        .withString(":v_artist", music.getArtist())
                        .withString(":v_title", music.getAlbumTitle()));

        ItemCollection<QueryOutcome> items = lsiAlbum.query(spec);

        Iterator<Item> itemsIter = items.iterator();

        while (itemsIter.hasNext()) {
            Item item = itemsIter.next();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
            musicList.add(mapper.readValue(item.toJSON(), Music.class));
        }

        return musicList;
    }

    public List<Music> queryItemByGSI(Music music) throws JsonProcessingException {
        List<Music> musicList = new ArrayList<>();
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("Upc = :v_upc")
                .withValueMap(new ValueMap().withString(":v_upc", music.getUpc()));

        ItemCollection<QueryOutcome> items = gsiAlbum.query(spec);

        Iterator<Item> itemsIter = items.iterator();

        while (itemsIter.hasNext()) {
            Item item = itemsIter.next();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
            musicList.add(mapper.readValue(item.toJSON(), Music.class));
        }

        return musicList;
    }
}
