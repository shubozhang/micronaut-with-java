package com.sz.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName="Music")
@ToString
public class Music {
    private String artist;
    private String songTitle;
    private String albumTitle;
    private Integer year;
    private String upc;

    @DynamoDBHashKey(attributeName="Artist")
    public String getArtist() { return artist;}
    public void setArtist(String artist) {this.artist = artist;}

    @DynamoDBRangeKey(attributeName="SongTitle")
    public String getSongTitle() { return songTitle;}
    public void setSongTitle(String songTitle) {this.songTitle = songTitle;}

    @DynamoDBIndexRangeKey(attributeName = "AlbumTitle", localSecondaryIndexName = "lsi_album")
    public String getAlbumTitle() { return albumTitle;}
    public void setAlbumTitle(String albumTitle) {this.albumTitle = albumTitle;}

    @DynamoDBAttribute(attributeName = "Year")
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    @DynamoDBIndexHashKey(attributeName = "Upc", globalSecondaryIndexName= "gsi_upc")
    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
}
