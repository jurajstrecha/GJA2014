package cz.fit.gja.twitter.model;

public class Configuration {

    final private Integer characters_reserved_per_media;
    final private Integer max_media_per_upload;
    final private Integer photo_size_limit;
    final private Integer short_url_length;
    final private Integer short_url_length_https;

    public Configuration(Integer characters_reserved_per_media, Integer max_media_per_upload,
                         Integer photo_size_limit, Integer short_url_length,
                         Integer short_url_length_https) {
        this.characters_reserved_per_media = characters_reserved_per_media;
        this.max_media_per_upload = max_media_per_upload;
        this.photo_size_limit = photo_size_limit;
        this.short_url_length = short_url_length;
        this.short_url_length_https = short_url_length_https;
    }

    public Integer getCharacters_reserved_per_media() {
        return characters_reserved_per_media;
    }

    public Integer getMax_media_per_upload() {
        return max_media_per_upload;
    }

    public Integer getPhoto_size_limit() {
        return photo_size_limit;
    }

    public Integer getShort_url_length() {
        return short_url_length;
    }

    public Integer getShort_url_length_https() {
        return short_url_length_https;
    }

}
