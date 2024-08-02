package io.github.semanticpie.pietunes.metadata.api.model.dto;

import io.github.semanticpie.pietunes.metadata.api.model.enums.ActionType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionEventDto {

    private ActionType type;

    private String entityUuid;

}
