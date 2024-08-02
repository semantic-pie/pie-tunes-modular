package io.github.semanticpie.pietunes.metadata.core.models.mappers;

import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicAlbumDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicTrackDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerAlbumDto;
import io.github.semanticpie.pietunes.metadata.core.models.MusicAlbum;
import io.github.semanticpie.pietunes.metadata.core.models.MusicBand;
import io.github.semanticpie.pietunes.metadata.core.models.MusicTrack;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.MusicBandDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerBandDto;
import io.github.semanticpie.pietunes.metadata.api.model.dto.entity.inner.InnerTrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DomainEntityMapper {

    @Named("innerBand")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    InnerBandDto innerBand(MusicBand source);

    @Named("innerAlbum")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    InnerAlbumDto innerAlbum(MusicAlbum source);

    @Named("innerTrack")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "bitrate", source = "bitrate")
    @Mapping(target = "lengthInMilliseconds", source = "lengthInMilliseconds")
    InnerTrackDto innerTrack(MusicTrack source);

    @Named("bandWithoutAlbums")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    MusicBandDto outerBandWithoutAlbums(MusicBand source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicAlbumDto outerAlbumWithoutTracks(MusicAlbum source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "bitrate", source = "bitrate")
    @Mapping(target = "lengthInMilliseconds", source = "lengthInMilliseconds")
    @Mapping(target = "musicAlbum", source = "musicAlbum", qualifiedByName = "innerAlbum")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicTrackDto outerTrack(MusicTrack source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "albums", source = "albums", qualifiedByName = "innerAlbum")
    MusicBandDto outerBand(MusicBand source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    @Mapping(target = "tracks", source = "tracks", qualifiedByName = "innerTrack")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicAlbumDto outerAlbum(MusicAlbum source);
}
